package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.CardImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> importAllCards() {
        try {
            cardImportService.importAllCards();
            return ResponseEntity.ok("Importación completada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error durante la importación: " + e.getMessage());
        }
    }
}
