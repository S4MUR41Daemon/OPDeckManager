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
 * Class used to import cards from the API and to save them in the database. It fetches the cards from the API,
 * maps them to the Card entity and saves them in the database.
 *
 * API actualizada: https://optcg-api.ryanmichaelhirst.us/api/v1
 *
 * @author dmaicas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CardImportService {

    private static final String API_BASE_URL = "https://optcg-api.ryanmichaelhirst.us/api/v1";
    
    // Lista de sets actualizada (la nueva API usa códigos diferentes)
    // La nueva API devuelve el código del set en el campo "set" y el code de la carta contiene el prefijo
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

        // La nueva API no tiene filtro por set, así que importamos todas las cartas de una vez
        try {
            totalImported = importAllCardsFromApi();
        } catch (Exception e) {
            log.error("Error importando todas las cartas: {}", e.getMessage(), e);
        }

        log.info("Importación completada. Total de cartas importadas: {}", totalImported);
    }

    /**
     * Importa todas las cartas desde la nueva API (que no tiene filtro por set)
     */
    @Transactional
    public int importAllCardsFromApi() throws IOException, InterruptedException {
        int page = 1;
        int totalPages = 1;
        int imported = 0;
        int skipped = 0;
        Set<String> seenCodes = new HashSet<>();

        while (page <= totalPages) {
            String url = API_BASE_URL + "/cards?page=" + page;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Error fetching pagina {}: HTTP {}", page, response.statusCode());
                break;
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            
            if (page == 1) {
                totalPages = rootNode.path("total_pages").asInt(1);
                int totalCards = rootNode.path("total").asInt(0);
                log.info("API reporta {} cartas en {} paginas", totalCards, totalPages);
            }

            JsonNode dataNode = rootNode.get("data");
            if (dataNode == null || !dataNode.isArray() || dataNode.isEmpty()) {
                break;
            }

            List<CardApiDTO> cardsPage = objectMapper.readValue(
                dataNode.toString(), 
                new TypeReference<List<CardApiDTO>>() {}
            );

            for (CardApiDTO apiCard : cardsPage) {
                String code = apiCard.getCode();
                if (code == null || seenCodes.contains(code)) {
                    skipped++;
                    continue;
                }
                seenCodes.add(code);
                if (saveCard(apiCard)) {
                    imported++;
                }
            }

            log.info("Pagina {}/{} procesada (importadas: {}, duplicadas/saltadas: {})", page, totalPages, imported, skipped);
            page++;
            
            Thread.sleep(200);
        }

        return imported;
    }

    @Transactional
    public int importSet(String setId) throws IOException, InterruptedException {
        log.info("Importando cartas del set {}...", setId);
        
        int page = 1;
        int totalPages = 1;
        int imported = 0;
        Set<String> seenCodes = new HashSet<>();

        while (page <= totalPages) {
            String url = API_BASE_URL + "/cards?page=" + page;
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("Error fetching pagina {}: HTTP {}", page, response.statusCode());
                break;
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            
            if (page == 1) {
                totalPages = rootNode.path("total_pages").asInt(1);
            }

            JsonNode dataNode = rootNode.get("data");
            if (dataNode == null || !dataNode.isArray() || dataNode.isEmpty()) {
                break;
            }

            List<CardApiDTO> cardsPage = objectMapper.readValue(
                dataNode.toString(), 
                new TypeReference<List<CardApiDTO>>() {}
            );

            for (CardApiDTO card : cardsPage) {
                String code = card.getCode();
                if (code != null && code.startsWith(setId + "-") && !seenCodes.contains(code)) {
                    seenCodes.add(code);
                    if (saveCard(card)) {
                        imported++;
                    }
                }
            }

            log.info("Pagina {}/{} procesada para set {} (importadas hasta ahora: {})", page, totalPages, setId, imported);
            page++;
            Thread.sleep(200);
        }

        return imported;
    }

    private boolean saveCard(CardApiDTO apiCard) {
        try {
            // Usar el código de la carta como ID único (ej. EB01-001)
            String cardSetId = apiCard.getCode();
            if (cardSetId == null || cardSetId.trim().isEmpty()) {
                log.warn("Carta sin código, saltando...");
                return false;
            }

            Card existingCard = entityManager.find(Card.class, cardSetId);
            if (existingCard != null) {
                log.debug("Carta {} ya existe, saltando...", cardSetId);
                return false;
            }

            boolean isLeader = "LEADER".equalsIgnoreCase(apiCard.getType());

            Card card;
            if (isLeader) {
                Leader leader = new Leader();
                leader.setLeaderLife(apiCard.getCost() != null ? String.valueOf(apiCard.getCost()) : null);
                card = leader;
            } else {
                card = new Card();
            }

            card.setCardSetId(cardSetId);
            card.setName(apiCard.getName());
            card.setType(apiCard.getType());
            card.setPower(apiCard.getPower());

            if (!isLeader) {
                card.setCost(apiCard.getCost());
            }
            
            card.setCounterAmount(apiCard.getCounter());
            card.setAttribute(apiCard.getAttribute());
            card.setSubTypes(apiCard.getCardClass());
            card.setCardText(apiCard.getEffect());
            card.setRarity(apiCard.getRarity());
            
            String setId = extractSetId(apiCard.getCode());
            card.setSetId(setId);
            
            card.setSetName(apiCard.getSetName());
            card.setImageUrl(apiCard.getImageUrl());
            card.setImageId(apiCard.getId());
            card.setImportedAt(LocalDateTime.now());

            entityManager.persist(card);

            saveCardColors(card, apiCard.getColor());

            return true;

        } catch (Exception e) {
            log.error("Error guardando carta {}: {}", apiCard.getCode(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extrae el ID del set desde el código de la carta
     * Ej: EB01-001 -> EB01, OP05-023 -> OP05
     */
    private String extractSetId(String cardCode) {
        if (cardCode == null) return "";
        
        // Patrón para extraer letras y números iniciales antes del guion
        Pattern pattern = Pattern.compile("^([A-Z]{2}\\d+)-");
        Matcher matcher = pattern.matcher(cardCode);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Para casos especiales, devolver todo hasta el primer guion
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

        // La nueva API usa formato "Red/Green", así que separamos por /
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
