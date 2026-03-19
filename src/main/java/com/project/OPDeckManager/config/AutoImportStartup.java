package com.project.OPDeckManager.config;

import com.project.OPDeckManager.service.CardImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

/**
 * Componente que ejecuta la importación automática de cartas al iniciar la aplicación.
 * Se ejecuta después de que el contexto de Spring esté completamente inicializado.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AutoImportStartup {

    private final CardImportService importService;
    private final AutoImportProperties properties;

    /**
     * Se ejecuta automáticamente después del startup de la aplicación.
     * Usa CompletableFuture para no bloquear el hilo principal.
     */
    @PostConstruct
    public void onStartup() {
        if (properties.isEnabled()) {
            log.info("============================================================");
            log.info("IMPORTACIÓN AUTOMÁTICA ACTIVADA");
            log.info("Iniciando importación de cartas desde GitHub...");
            log.info("Este proceso puede tardar varios minutos.");
            log.info("============================================================");
            
            // Ejecutar en segundo plano para no bloquear el startup
            CompletableFuture.runAsync(() -> {
                try {
                    importService.importAllCards();
                    log.info("============================================================");
                    log.info("IMPORTACIÓN AUTOMÁTICA COMPLETADA");
                    log.info("============================================================");
                } catch (Exception e) {
                    log.error("Error en importación automática: {}", e.getMessage(), e);
                }
            });
        } else {
            log.info("============================================================");
            log.info("IMPORTACIÓN AUTOMÁTICA DESACTIVADA");
            log.info("Para importar cartas, ejecuta:");
            log.info("  POST http://localhost:8080/api/import/all");
            log.info("O usa el script: .\\start-import.ps1");
            log.info("============================================================");
        }
    }

    /**
     * Propiedades de configuración para la importación automática.
     */
    @Configuration
    @ConfigurationProperties(prefix = "opdeckmanager.auto-import")
    public static class AutoImportProperties {
        
        /**
         * Si está habilitada, importa todas las cartas al startup.
         */
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
