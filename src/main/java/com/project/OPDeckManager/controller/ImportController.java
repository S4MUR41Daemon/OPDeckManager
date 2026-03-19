package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.CardImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImportController {

    private final CardImportService cardImportService;

    /**
     * Importa todas las cartas desde el JSON de GitHub.
     * POST /api/import/all
     */
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> importAllCards() {
        try {
            long startTime = System.currentTimeMillis();
            cardImportService.importAllCards();
            long endTime = System.currentTimeMillis();

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Importación completada correctamente",
                "duration_seconds", (endTime - startTime) / 1000
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Error durante la importación: " + e.getMessage()
                ));
        }
    }
}
