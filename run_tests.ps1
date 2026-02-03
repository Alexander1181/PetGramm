# ============================================
# Ejecutar Unit Tests locales (JUnit / Robolectric)
# ============================================

Write-Host "Ejecutando Unit Tests locales..." -ForegroundColor Cyan

# Ejecuta las pruebas con Gradle
# El flag --info o --stacktrace puede ayudar si falla, pero por defecto usamos lo básico.
.\gradlew testDebugUnitTest

# Verificamos el resultado del comando anterior
if ($LASTEXITCODE -eq 0) {
    # Ruta del reporte generado por Gradle (ajustar si el módulo no es 'app')
    $reportPath = "app\build\reports\tests\testDebugUnitTest\index.html"

    if (Test-Path $reportPath) {
        Write-Host "Pruebas completadas exitosamente." -ForegroundColor Green
        Write-Host "Abriendo reporte: $reportPath" -ForegroundColor Green
        # Abrimos el reporte en el navegador predeterminado
        Start-Process $reportPath
    }
    else {
        Write-Host "Las pruebas pasaron, pero no se encontró el reporte en: $reportPath" -ForegroundColor Yellow
    }
}
else {
    Write-Host "Ocurrió un error al ejecutar los tests. Revisa la salida de la consola." -ForegroundColor Red
}
