# ================================================================================
# Script de inicio e importación automática para OPDeckManager
# ================================================================================
# Este script:
#   1. Inicia la aplicación Spring Boot
#   2. Espera a que esté disponible
#   3. Llama al endpoint de importación de datos
#   4. Mantiene la aplicación ejecutándose
# ================================================================================

param(
    [switch]$SkipImport,  # Si se usa -SkipImport, no importa datos
    [int]$Timeout = 120   # Timeout en segundos para esperar al servidor
)

$ErrorActionPreference = "Stop"
$APP_NAME = "OPDeckManager"
$BASE_URL = "http://localhost:8080"
$IMPORT_ENDPOINT = "$BASE_URL/api/import/all"
$HEALTH_ENDPOINT = "$BASE_URL/actuator/health"

# Colores para output
function Write-Info    { Write-Host "[INFO]    $($args -join ' ')" -ForegroundColor Cyan }
function Write-Success { Write-Host "[SUCCESS] $($args -join ' ')" -ForegroundColor Green }
function Write-Warning { Write-Host "[WARNING] $($args -join ' ')" -ForegroundColor Yellow }
function Write-Error   { Write-Host "[ERROR]   $($args -join ' ')" -ForegroundColor Red }

# ================================================================================
# 1. Verificar prerequisitos
# ================================================================================
Write-Info "Verificando prerequisitos..."

# Verificar Java
try {
    $javaVersion = java -version 2>&1 | Select-String -Pattern 'version' | Select-Object -First 1
    Write-Info "Java encontrado: $javaVersion"
} catch {
    Write-Error "Java no está instalado o no está en el PATH"
    exit 1
}

# Verificar Maven
try {
    $mvnVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Info "Maven encontrado: $mvnVersion"
} catch {
    Write-Error "Maven no está instalado o no está en el PATH"
    exit 1
}

# ================================================================================
# 2. Iniciar aplicación Spring Boot en background
# ================================================================================
Write-Info "Iniciando $APP_NAME..."

$job = Start-Job -ScriptBlock {
    Set-Location $using:PWD
    mvn spring-boot:run
}

Write-Info "Proceso iniciado en segundo plano (Job ID: $($job.Id))"

# ================================================================================
# 3. Esperar a que la aplicación esté disponible
# ================================================================================
if (-not $SkipImport) {
    Write-Info "Esperando a que la aplicación esté disponible (timeout: ${Timeout}s)..."
    
    $startTime = Get-Date
    $isReady = $false
    
    while ($true) {
        # Verificar si el job sigue corriendo
        $jobState = (Get-Job -Id $job.Id).State
        if ($jobState -ne "Running") {
            Write-Error "La aplicación falló al iniciar. Revisa los logs del job $($_job.Id)"
            Receive-Job -Id $job.Id
            exit 1
        }
        
        # Intentar conectar
        try {
            $response = Invoke-WebRequest -Uri $HEALTH_ENDPOINT -TimeoutSec 5 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                $isReady = $true
                break
            }
        } catch {
            # Ignorar errores, seguir intentando
        }
        
        # Verificar timeout
        $elapsed = (New-TimeSpan -Start $startTime -End (Get-Date)).TotalSeconds
        if ($elapsed -ge $Timeout) {
            Write-Error "Timeout: La aplicación no estuvo disponible en ${Timeout} segundos"
            Write-Warning "Puedes aumentar el timeout con: .\start-import.ps1 -Timeout 180"
            Stop-Job -Id $job.Id
            Remove-Job -Id $job.Id
            exit 1
        }
        
        Start-Sleep -Seconds 2
    }
    
    Write-Success "¡Aplicación lista después de $([math]::Round($elapsed)) segundos!"
    
    # ================================================================================
    # 4. Llamar al endpoint de importación
    # ================================================================================
    Write-Info "Iniciando importación de datos desde GitHub..."
    Write-Info "Esto puede tardar varios minutos dependiendo de la cantidad de cartas..."
    
    try {
        $response = Invoke-WebRequest -Uri $IMPORT_ENDPOINT -Method POST -TimeoutSec 300 -UseBasicParsing
        Write-Success "Importación completada!"
        Write-Info "Respuesta: $($response.Content)"
    } catch {
        Write-Warning "La importación encontró errores: $($_.Exception.Message)"
        Write-Warning "Pero la aplicación sigue ejecutándose"
    }
} else {
    Write-Info "SkipImport activado, saltando importación de datos..."
}

# ================================================================================
# 5. Mantener aplicación corriendo
# ================================================================================
Write-Info ""
Write-Info "================================================================================"
Write-Success "¡$APP_NAME está corriendo en $BASE_URL!"
Write-Info "================================================================================"
Write-Info "Comandos útiles:"
Write-Info "  - Consola H2: $BASE_URL/h2-console"
Write-Info "  - Importar manualmente: curl -X POST $IMPORT_ENDPOINT"
Write-Info "  - Detener aplicación: Ctrl+C o ejecutar .\stop-opdeck.ps1"
Write-Info "================================================================================"
Write-Info ""

# Mantener el script corriendo mientras el job esté activo
try {
    while ((Get-Job -Id $job.Id).State -eq "Running") {
        Start-Sleep -Seconds 5
    }
} catch {
    # Job terminó
}

Write-Warning "La aplicación se ha detenido"
Receive-Job -Id $job.Id
Remove-Job -Id $job.Id
