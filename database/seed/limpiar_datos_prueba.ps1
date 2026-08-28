param(
    [string]$DbUser = "postgres",
    [string]$DbPassword = "1234",
    [string]$DbName = "portal_abaxial",
    [string]$DbHost = "localhost",
    [string]$DbPort = "5432"
)

$PsqlPath = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
if (-not (Test-Path $PsqlPath)) {
    $PsqlPath = "psql"
}

$SqlFile = Join-Path $PSScriptRoot "clean_test_data.sql"

Write-Host "========================================================" -ForegroundColor Magenta
Write-Host "  ABAXIAL PORTAL TECNICO - LIMPIEZA SEGURA DE DATOS TEST" -ForegroundColor Magenta
Write-Host "========================================================" -ForegroundColor Magenta

$env:PGHOST = $DbHost
$env:PGPORT = $DbPort
$env:PGUSER = $DbUser
$env:PGPASSWORD = $DbPassword
$env:PGDATABASE = $DbName

Write-Host "--- Comprobacion de seguridad previa ---" -ForegroundColor White
$checkQ = "SELECT (SELECT COUNT(*) FROM clientes WHERE codigo LIKE 'CLI-TEST-%') AS clientes_a_eliminar, (SELECT COUNT(*) FROM clientes WHERE codigo NOT LIKE 'CLI-TEST-%') AS clientes_reales_protegidos;"
& $PsqlPath -U $DbUser -d $DbName -c $checkQ

Write-Host "Ejecutando limpieza: $SqlFile" -ForegroundColor Yellow
& $PsqlPath -U $DbUser -d $DbName -f $SqlFile

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Limpieza finalizada correctamente." -ForegroundColor Green
    Write-Host "--- Estado de la Base de Datos tras Limpieza ---" -ForegroundColor White
    $postQ = "SELECT (SELECT COUNT(*) FROM clientes WHERE codigo LIKE 'CLI-TEST-%') AS clientes_test_restantes, (SELECT COUNT(*) FROM clientes WHERE codigo NOT LIKE 'CLI-TEST-%') AS clientes_reales_intactos, (SELECT COUNT(*) FROM equipos) AS total_equipos, (SELECT COUNT(*) FROM webs) AS total_webs;"
    & $PsqlPath -U $DbUser -d $DbName -c $postQ
} else {
    Write-Host "[ERROR] Fallo la ejecucion del script SQL de limpieza." -ForegroundColor Red
}
