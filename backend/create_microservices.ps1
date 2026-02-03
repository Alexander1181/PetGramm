# Script para crear los microservicios faltantes (post-service y comment-service)
# Basado en la estructura de 'auth-service'

$baseDir = "C:\Users\aleto\OneDrive\Escritorio\Backend"
$authService = "$baseDir\auth-service"
$postService = "$baseDir\post-service"
$commentService = "$baseDir\comment-service"

# Función para copiar y limpiar un servicio
function Create-Service {
    param (
        [string]$TargetDir,
        [string]$ServiceName
    )

    Write-Host "Creando $ServiceName en $TargetDir..." -ForegroundColor Cyan

    # 1. Copiar todo el contenido de auth-service excepto lo específico
    # Robocopy es robusto para clonar carpetas
    # /E = subdirectorios, /XD = excluir directorios (.git, target, .idea, etc)
    robocopy $authService $TargetDir /E /XD ".git" ".idea" "target" "src" ".vscode"

    # 2. Copiar src manualmente pero filtrando
    # Copiamos la estructura de directorios de src
    Get-ChildItem -Path "$authService\src" -Recurse -Directory | ForEach-Object {
        $destPath = $_.FullName.Replace($authService, $TargetDir)
        New-Item -ItemType Directory -Path $destPath -Force | Out-Null
    }

    # Copiamos MainApplication.java (el punto de entrada) y pom.xml
    # Ajustaremos el nombre del paquete/archivos después si es necesario
    # Por ahora simplemente clonamos la estructura base y luego el usuario (o yo) escribirá el código.
    
    # Vamos a recrear la estructura src básica limpia
    $mainPkg = "$TargetDir\src\main\java\com\petgram"
    $svcPkg = "$mainPkg\$($ServiceName.Replace('-', '_'))"
    
    New-Item -ItemType Directory -Path "$svcPkg\controller" -Force | Out-Null
    New-Item -ItemType Directory -Path "$svcPkg\model" -Force | Out-Null
    New-Item -ItemType Directory -Path "$svcPkg\repository" -Force | Out-Null

    # Crear clase principal básica
    $mainClassContent = @"
package com.petgram.$($ServiceName.Replace('-', '_'));

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class $(($ServiceName.Replace('-', '_') -split '_' | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join '')Application {

	public static void main(String[] args) {
		SpringApplication.run($(($ServiceName.Replace('-', '_') -split '_' | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join '')Application.class, args);
	}

}
"@
    Set-Content -Path "$svcPkg\$(($ServiceName.Replace('-', '_') -split '_' | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join '')Application.java" -Value $mainClassContent

    # 3. Ajustar pom.xml
    $pomContent = Get-Content "$authService\pom.xml" -Raw
    $pomContent = $pomContent -replace "auth-service", $ServiceName
    Set-Content -Path "$TargetDir\pom.xml" -Value $pomContent

    Write-Host "$ServiceName creado exitosamente." -ForegroundColor Green
}

# Crear Post Service
Create-Service -TargetDir $postService -ServiceName "post-service"

# Crear Comment Service
Create-Service -TargetDir $commentService -ServiceName "comment-service"

Write-Host "Microservicios generados. Ahora debes abrirlos en VS Code." -ForegroundColor Yellow
