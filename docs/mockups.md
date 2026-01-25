# Mockups y Diseño UX - PetGram

## Flujo Principal: Navegación

```mermaid
graph TD
    Splash[Splash Screen] --> Login[Login / Registro]
    Login --> Home[Home Feed]
    
    Home -->|Click Foto| Detalle[Detalle Post]
    Home -->|Click FAB (+)| Camara[Cámara / Galería]
    
    Camara -->|Confirmar Foto| Editor[Editor / Filtros]
    Editor --> Formulario[Formulario Nueva Publicación]
    Formulario -->|Publicar| Home
    
    Home -->|Tab Perfil| Perfil[Perfil Usuario]
    Home -->|Tab Adopción| Filtros[Buscador Adopciones]
```

## Wireframes (Baja Fidelidad)

### 1. Pantalla Home (Feed)
```text
+-----------------------------+
|  PetGram             [Msg]  |
+-----------------------------+
| [Historias/Destacados.....] |
| ( O ) ( O ) ( O ) ( O )     |
+-----------------------------+
| [Avatar] @usuario_refugio   |
|                             |
|  [ FOTO PRINCIPAL 1:1 ]     |
|                             |
+-----------------------------+
| <3  (_)  (^)           [...] |
| 75 likes                    |
| **Rex** busca casa! #adopta |
+-----------------------------+
| [Avatar] @juan_perez        |
|  [ FOTO MASCOTA ]           |
...
+-----------------------------+
| [Home]  [Buscar]  (+)  [Yo] |
+-----------------------------+
```

### 2. Pantalla Cámara / Post
```text
+-----------------------------+
|  X        Nueva Foto    Next |
+-----------------------------+
|                             |
|      VISOR DE CÁMARA        |
|                             |
|                             |
+-----------------------------+
| [Galería]   (SHUTTER)       |
+-----------------------------+
| Recent:                     |
| [img1] [img2] [img3] [img4] |
+-----------------------------+
```
