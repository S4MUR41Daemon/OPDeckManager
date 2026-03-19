# ================================================================================
# Script para detener OPDeckManager
# ================================================================================
# Detiene todos los procesos de Java que estén corriendo la aplicación
# ================================================================================

$ErrorActionPreference = "Stop"
$APP_NAME = "OPDeckManager"

function Write-Info    { Write-Host "[INFO]    $($args -join ' ')" -ForegroundColor Cyan }
function Write-Success { Write-Host "[SUCCESS] $($args -join ' ')" -ForegroundColor Green }
function Write-Warning { Write-Host "[WARNING] $($args -join ' ')" -ForegroundColor Yellow }

Write-Info "Buscando procesos de $APP_NAME..."

# Buscar procesos Java que estén corriendo Spring Boot
$processes = Get-Process java -ErrorAction SilentlyContinue | Where-Object {
    $_.CommandLine -like "*OPDeckManager*" -or $_.CommandLine -like "*spring-boot*"
}

if ($processes) {
    Write-Info "Encontrados $($processes.Count) proceso(s) relacionado(s)"
    
    foreach ($proc in $processes) {
        Write-Info "Deteniendo proceso PID: $($proc.Id)..."
        Stop-Process -Id $proc.Id -Force
    }
    
    Write-Success "Todos los procesos han sido detenidos"
} else {
    # Alternativa: buscar por puerto
    $connection = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | 
                  Where-Object { $_.State -eq "Listen" }
    
    if ($connection) {
        $proc = Get-Process -Id $connection.OwningProcess
        Write-Info "Encontrado proceso en puerto 8080: $($proc.ProcessName) (PID: $($proc.Id))"
        Stop-Process -Id $proc.Id -Force
        Write-Success "Proceso detenido"
    } else {
        Write-Warning "No se encontraron procesos de $APP_NAME corriendo"
    }
}
