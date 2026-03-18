package com.project.OPDeckManager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.OPDeckManager.domain.entities.*;
import com.project.OPDeckManager.service.dto.CardApiDTO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio de importación de cartas desde el JSON completo de GitHub:
 * https://raw.githubusercontent.com/nemesis312/OnePieceTCGEngCardList/main/CardDb3.json
 *
 * @author dmaicas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CardImportService {

    private static final String GITHUB_JSON_URL =
            "https://raw.githubusercontent.com/nemesis312/OnePieceTCGEngCardList/main/CardDb3.json";

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Transactional
    public void importAllCards() {
        log.info("Iniciando importación de cartas desde GitHub...");
        try {
            int totalImported = fetchAndImportAll();
            log.info("Importación completada. Total de cartas importadas: {}", totalImported);
        } catch (Exception e) {
            log.error("Error importando cartas: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public int fetchAndImportAll() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_JSON_URL))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error fetching JSON desde GitHub: HTTP " + response.statusCode());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode cardsNode = rootNode.get("Cards");

        if (cardsNode == null || !cardsNode.isArray()) {
            throw new IOException("El JSON no contiene un array 'Cards' en la raíz");
        }

        List<CardApiDTO> allCards = objectMapper.readValue(
                cardsNode.toString(),
                new TypeReference<List<CardApiDTO>>() {}
        );

        log.info("JSON cargado: {} cartas encontradas", allCards.size());

        int imported = 0;
        int skipped = 0;
        Set<String> seenIds = new HashSet<>();

        for (CardApiDTO apiCard : allCards) {
            String cardSetId = cleanCardNum(apiCard.getCardNum());
            if (cardSetId == null || cardSetId.isBlank() || seenIds.contains(cardSetId)) {
                skipped++;
                continue;
            }
            seenIds.add(cardSetId);

            if (saveCard(apiCard, cardSetId)) {
                imported++;
            } else {
                skipped++;
            }

            if (imported % 500 == 0 && imported > 0) {
                log.info("Progreso: {} cartas importadas, {} saltadas", imported, skipped);
            }
        }

        return imported;
    }

    private boolean saveCard(CardApiDTO apiCard, String cardSetId) {
        try {
            Card existingCard = entityManager.find(Card.class, cardSetId);
            if (existingCard != null) {
                return false;
            }

            boolean isLeader = "LEADER".equalsIgnoreCase(apiCard.getCardType());

            Card card;
            if (isLeader) {
                Leader leader = new Leader();
                leader.setLeaderLife(apiCard.getCost() != null ? apiCard.getCost() : "-");
                card = leader;
            } else {
                card = new Card();
            }

            card.setCardSetId(cardSetId);
            card.setName(apiCard.getName());
            card.setType(apiCard.getCardType());

            if (!isLeader) {
                card.setCost(parseIntOrNull(apiCard.getCost()));
            }

            card.setPower(parseIntOrNull(apiCard.getPower()));
            card.setCounterAmount(parseIntOrNull(apiCard.getCounter()));
            card.setAttribute(apiCard.getAttribute());
            card.setSubTypes(apiCard.getType());
            card.setCardText(apiCard.getEffect());
            card.setRarity(apiCard.getRarity());

            String setId = extractSetId(cardSetId);
            card.setSetId(setId);
            card.setSetName(apiCard.getCardSets());
            card.setImageUrl(apiCard.getImg());
            card.setImportedAt(LocalDateTime.now());

            entityManager.persist(card);

            saveCardColors(card, apiCard.getColor());

            return true;

        } catch (Exception e) {
            log.error("Error guardando carta {}: {}", cardSetId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * "#OP01-001" -> "OP01-001"
     */
    private String cleanCardNum(String cardNum) {
        if (cardNum == null) return null;
        String cleaned = cardNum.trim();
        if (cleaned.startsWith("#")) {
            cleaned = cleaned.substring(1);
        }
        return cleaned;
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank() || "-".equals(value.trim())) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractSetId(String cardCode) {
        if (cardCode == null) return "";

        Pattern pattern = Pattern.compile("^([A-Z]{2,3}\\d+)-");
        Matcher matcher = pattern.matcher(cardCode);

        if (matcher.find()) {
            return matcher.group(1);
        }

        int dashIndex = cardCode.indexOf('-');
        if (dashIndex > 0) {
            return cardCode.substring(0, dashIndex);
        }

        return cardCode;
    }

    private void saveCardColors(Card card, String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            return;
        }

        String[] colorNames = colorString.split("/");

        for (String colorName : colorNames) {
            String trimmedName = colorName.trim();
            if (trimmedName.isEmpty()) continue;

            Color color = findColorByName(trimmedName);
            if (color == null) {
                log.warn("Color '{}' no encontrado en BD, saltando...", trimmedName);
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
