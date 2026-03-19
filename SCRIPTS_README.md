# Scripts de Automatización - OPDeckManager

## Descripción
Estos scripts automatizan el proceso de inicio e importación de datos para OPDeckManager.

---

## Scripts Disponibles

### 1. `start-import.ps1` - Inicio con Importación Automática

**Propósito:** Inicia la aplicación y automáticamente importa todas las cartas desde GitHub.

**Uso básico:**
```powershell
.\start-import.ps1
```

**Parámetros opcionales:**

| Parámetro | Descripción | Default |
|-----------|-------------|---------|
| `-SkipImport` | No importa datos, solo inicia la app | No |
| `-Timeout` | Segundos para esperar al servidor | 120 |

**Ejemplos:**

```powershell
# Importación automática completa
.\start-import.ps1

# Solo iniciar la aplicación (sin importar)
.\start-import.ps1 -SkipImport

# Aumentar timeout a 3 minutos
.\start-import.ps1 -Timeout 180
```

**Qué hace el script:**
1. ✅ Verifica que Java y Maven estén instalados
2. ✅ Inicia Spring Boot en segundo plano
3. ✅ Espera a que la aplicación esté disponible (health check)
4. ✅ Llama a `POST /api/import/all` para importar datos
5. ✅ Mantiene la aplicación corriendo

---

### 2. `stop-opdeck.ps1` - Detener Aplicación

**Propósito:** Detiene todos los procesos de OPDeckManager.

**Uso:**
```powershell
.\stop-opdeck.ps1
```

**Qué hace el script:**
- Busca procesos Java relacionados con OPDeckManager
- Los detiene forzadamente
- Si no encuentra procesos, busca por el puerto 8080

---

## Importación Automática al Startup

Puedes configurar la aplicación para que importe datos automáticamente cada vez que se inicie.

### Opción A: Desde `application.properties`

Edita `src/main/resources/application.properties`:

```properties
# Cambiar a true para importación automática
opdeckmanager.auto-import.enabled=true
```

### Opción B: Desde línea de comandos

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--opdeckmanager.auto-import.enabled=true"
```

**Comportamiento:**
- `true`: Importa todas las cartas automáticamente al iniciar
- `false` (default): Requiere llamar manualmente al endpoint

---

## Endpoints Relacionados

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/import/all` | POST | Importa todas las cartas desde GitHub |
| `/actuator/health` | GET | Verifica si la aplicación está lista |
| `/h2-console` | GET | Consola web de la base de datos H2 |

---

## Flujo Recomendado

### Primer uso en otro PC:

```powershell
# 1. Clonar el repositorio
git clone <tu-repo>
cd OPDeckManager

# 2. Ejecutar con importación automática
.\start-import.ps1

# La aplicación:
# - Creará la base de datos en ./data/
# - Importará todas las cartas desde GitHub
# - Estará disponible en http://localhost:8080
```

### Uso diario (con datos ya importados):

```powershell
# Opción 1: Sin importar (más rápido)
.\start-import.ps1 -SkipImport

# Opción 2: Forzar re-importación
.\start-import.ps1
```

---

## URLs de Acceso

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| Aplicación | http://localhost:8080 | - |
| H2 Console | http://localhost:8080/h2-console | JDBC: `jdbc:h2:file:./data/onepiece-db`<br/>User: `sa`<br/>Password: `password` |
| Health Check | http://localhost:8080/actuator/health | - |

---

## Solución de Problemas

### La aplicación no inicia
```powershell
# Verificar Java
java -version

# Verificar Maven
mvn -version
```

### Timeout al esperar la aplicación
```powershell
# Aumentar timeout
.\start-import.ps1 -Timeout 180
```

### Error en la importación
- Revisa los logs en la consola
- Verifica conexión a internet (necesita acceder a GitHub)
- La base de datos puede estar corrupta, elimina `./data/` y reintenta

### La aplicación no se detiene
```powershell
# Forzar detención por puerto
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | 
    ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

---

## Notas Importantes

1. **Base de datos:** Se crea en `./data/onepiece-db.mv.db` (archivo local)
2. **Idempotencia:** Puedes ejecutar la importación múltiples veces sin duplicar datos
3. **Tiempo de importación:** Depende de la cantidad de cartas, típicamente 1-3 minutos
4. **Permisos PowerShell:** Si hay error de ejecución, ejecuta:
   ```powershell
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```
