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

$SqlFile = Join-Path $PSScriptRoot "load_test_data.sql"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  ABAXIAL PORTAL TECNICO - CARGA DE DATOS DE PRUEBA" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Ejecutando script: $SqlFile" -ForegroundColor Yellow

$env:PGHOST = $DbHost
$env:PGPORT = $DbPort
$env:PGUSER = $DbUser
$env:PGPASSWORD = $DbPassword
$env:PGDATABASE = $DbName

& $PsqlPath -U $DbUser -d $DbName -f $SqlFile

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Carga finalizada correctamente." -ForegroundColor Green
    Write-Host "--- Resumen de la Base de Datos ---" -ForegroundColor White
    $q = "SELECT (SELECT COUNT(*) FROM clientes WHERE codigo LIKE 'CLI-TEST-%') AS clientes_test, (SELECT COUNT(*) FROM clientes WHERE codigo NOT LIKE 'CLI-TEST-%') AS clientes_reales, (SELECT COUNT(*) FROM equipos WHERE codigo_inventario LIKE 'EQ-TEST-%') AS equipos_test, (SELECT COUNT(*) FROM webs WHERE url LIKE '%test-empresa%') AS webs_test, (SELECT COUNT(*) FROM eventos WHERE titulo LIKE '[TEST]%') AS eventos_test;"
    & $PsqlPath -U $DbUser -d $DbName -c $q
} else {
    Write-Host "[ERROR] Fallo la ejecucion del script SQL de carga." -ForegroundColor Red
}
