# Script para iniciar todos los microservicios de Spring Boot
$backendDir = "C:\Users\aleto\OneDrive\Escritorio\Backend"

Write-Host "Iniciando Auth Service (Puerto 8080)..." -ForegroundColor Cyan
Start-Process cmd -ArgumentList "/k", "cd /d $backendDir\auth-service && mvnw spring-boot:run"

Write-Host "Iniciando Post Service (Puerto 8081)..." -ForegroundColor Cyan
Start-Process cmd -ArgumentList "/k", "cd /d $backendDir\post-service && mvnw spring-boot:run"

Write-Host "Iniciando Comment Service (Puerto 8082)..." -ForegroundColor Cyan
Start-Process cmd -ArgumentList "/k", "cd /d $backendDir\comment-service && mvnw spring-boot:run"

Write-Host "Servicicos iniciandose en ventanas separadas." -ForegroundColor Green
