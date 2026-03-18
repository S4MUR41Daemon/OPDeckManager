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
     * Endpoint para importar todas las cartas desde la API externa
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

    /**
     * Endpoint para importar un set específico
     * POST /api/import/set/OP01
     */
    @PostMapping("/set/{setId}")
    public ResponseEntity<String> importSet(@PathVariable String setId) {
        try {
            int imported = cardImportService.importSet(setId);
            return ResponseEntity.ok(String.format("Set %s importado: %d cartas", setId, imported));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error durante la importación: " + e.getMessage());
        }
    }
}
