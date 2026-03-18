package com.project.OPDeckManager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.OPDeckManager.domain.entities.*;
import com.project.OPDeckManager.service.dto.CardApiDTO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Class used to import cards from the API and to save them in the database. It fetches the cards from the API,
 * maps them to the Card entity and saves them in the database.
 *
 * @author dmaicas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CardImportService {

    private static final String API_BASE_URL = "https://optcgapi.com/api";
    private static final List<String> SET_IDS = List.of(
        "OP01", "OP02", "OP03", "OP04", "OP05", "OP06", "OP07", "OP08",
        "OP09", "OP10", "OP11", "OP12", "OP13", "OP14", "OP15",
        "EB01", "EB02", "EB03", "EB04",
        "ST01", "ST02", "ST03", "ST04", "ST05", "ST06", "ST07", "ST08",
        "ST09", "ST10", "ST11", "ST12", "ST13", "ST14", "ST15", "ST16",
        "ST17", "ST18", "ST19", "ST20", "ST21", "ST22", "ST23", "ST24",
        "ST25", "ST26", "ST27", "ST28", "ST29"
    );

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Transactional
    public void importAllCards() {
        log.info("Iniciando importación de cartas desde la API...");

        int totalImported = 0;

        for (String setId : SET_IDS) {
            try {
                int imported = importSet(setId);
                totalImported += imported;
                log.info("Set {} importado: {} cartas", setId, imported);
            } catch (Exception e) {
                log.error("Error importando set {}: {}", setId, e.getMessage(), e);
            }
        }

        log.info("Importación completada. Total de cartas importadas: {}", totalImported);
    }

    @Transactional
    public int importSet(String setId) throws IOException, InterruptedException {
        List<CardApiDTO> apiCards = fetchCardsFromSet(setId);

        int imported = 0;
        for (CardApiDTO apiCard : apiCards) {
            if (saveCard(apiCard)) {
                imported++;
            }
        }

        return imported;
    }

    private List<CardApiDTO> fetchCardsFromSet(String setId) throws IOException, InterruptedException {
        String url = API_BASE_URL + "/sets/filtered/?set_id=" + setId;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.warn("Error fetching set {}: HTTP {}", setId, response.statusCode());
            return Collections.emptyList();
        }

        return objectMapper.readValue(response.body(), new TypeReference<List<CardApiDTO>>() {});
    }

    private boolean saveCard(CardApiDTO apiCard) {
        try {
            Card existingCard = entityManager.find(Card.class, apiCard.getCardSetId());
            if (existingCard != null) {
                log.debug("Carta {} ya existe, saltando...", apiCard.getCardSetId());
                return false;
            }

            Card card = new Card();
            card.setCardSetId(apiCard.getCardSetId());
            card.setName(apiCard.getCardName());
            card.setType(apiCard.getCardType());
            card.setCost(apiCard.getCardCost());

            if (apiCard.getCardPower() != null && !apiCard.getCardPower().isEmpty()) {
                try {
                    card.setPower(Integer.parseInt(apiCard.getCardPower()));
                } catch (NumberFormatException e) {
                    log.debug("Power no numérico para {}: {}", apiCard.getCardSetId(), apiCard.getCardPower());
                }
            }

            if (apiCard.getLife() != null && !apiCard.getLife().isEmpty()) {
                try {
                    card.setLife(Integer.parseInt(apiCard.getLife()));
                } catch (NumberFormatException e) {
                    log.debug("Life no numérico para {}: {}", apiCard.getCardSetId(), apiCard.getLife());
                }
            }

            card.setCounterAmount(apiCard.getCounterAmount());
            card.setAttribute(apiCard.getAttribute());
            card.setSubTypes(apiCard.getSubTypes());
            card.setCardText(apiCard.getCardText());
            card.setRarity(apiCard.getRarity());
            card.setSetId(apiCard.getSetId());
            card.setSetName(apiCard.getSetName());
            card.setImageUrl(apiCard.getCardImage());
            card.setImageId(apiCard.getCardImageId());

            if (apiCard.getMarketPrice() != null) {
                card.setMarketPrice(BigDecimal.valueOf(apiCard.getMarketPrice()));
            }
            if (apiCard.getInventoryPrice() != null) {
                card.setInventoryPrice(BigDecimal.valueOf(apiCard.getInventoryPrice()));
            }

            card.setImportedAt(LocalDateTime.now());

            entityManager.persist(card);

            saveCardColors(card, apiCard.getCardColor());

            return true;

        } catch (Exception e) {
            log.error("Error guardando carta {}: {}", apiCard.getCardSetId(), e.getMessage(), e);
            return false;
        }
    }

    private void saveCardColors(Card card, String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            return;
        }

        String[] colorNames = colorString.trim().split("\\s+");

        for (String colorName : colorNames) {
            if (colorName.trim().isEmpty()) continue;

            Color color = findColorByName(colorName.trim());
            if (color == null) {
                log.warn("Color '{}' no encontrado en BD, saltando...", colorName);
                continue;
            }

            CardColor cardColor = new CardColor();
            CardColorId cardColorId = new CardColorId();
            cardColorId.setCardSetId(card.getCardSetId());
            cardColorId.setColorId(color.getId());

            cardColor.setId(cardColorId);
            cardColor.setCard(card);
            cardColor.setColor(color);

            entityManager.persist(cardColor);
            card.getCardColors().add(cardColor);
        }
    }

    private Color findColorByName(String colorName) {
        List<Color> colors = entityManager.createQuery(
                "SELECT c FROM Color c WHERE c.name = :name", Color.class)
            .setParameter("name", colorName)
            .getResultList();

        return colors.isEmpty() ? null : colors.get(0);
    }
}
