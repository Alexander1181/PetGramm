# Documento de Requerimientos - PetGram

## 1. Contexto del Negocio y Objetivos
**PetGram** es una aplicación móvil Android diseñada para conectar refugios y amantes de los animales.

- **Objetivo Principal**: Fomentar la adopción de mascotas y crear una comunidad de apoyo.
- **Propuesta de Valor**: Una plataforma visual (tipo Instagram) donde dar visibilidad a mascotas en adopción y situaciones de emergencia, combinando la viralidad social con la seriedad de un refugio.

## 2. Usuarios
- **Usuario Estándar (Adoptante/Comunidad)**: Puede ver, dar like, comentar, contactar refugios y subir fotos de sus propias mascotas.
- **Refugio/Administrador (Provider)**: Perfil verificado con capacidad de publicar animales en adopción, gestionar estados (En adopción/Adoptado) y recibir solicitudes.

## 3. Requerimientos Funcionales

### 3.1 Módulo Social (Feed)
- **RF-001 Ver Feed**: El usuario debe poder ver un listado infinito de publicaciones (fotos + descripción).
- **RF-002 Filtrar**: Capacidad de filtrar por categoría (Perro, Gato, Urgente, Final Feliz).
- **RF-003 Interacción**: Dar "Like" a publicaciones.
- **RF-004 Detalle**: Ver perfil completo de la mascota (Edad, Raza, Estado de Salud, Historia).

### 3.2 Módulo de Creación de Contenido (Cámara) 🔴 CRÍTICO
- **RF-005 Tomar Foto**: La app debe invocar la cámara nativa del dispositivo para capturar una imagen nueva.
- **RF-006 Selección de Galería**: La app debe permitir seleccionar una imagen existente del almacenamiento del dispositivo.
- **RF-007 Edición Básica (Opcional MVP)**: Recorte cruadrado (1:1).
- **RF-008 Publicar**: Formulario para añadir descripción, etiquetas (tags) y categoría antes de subir.

### 3.3 Gestión de Adopciones
- **RF-009 Estado de Mascota**: Indicadores visuales de "En Adopción", "Reservado", "Adoptado".
- **RF-010 Contacto**: Botón directo para contactar al refugio (WhatsApp/Email) desde la ficha de la mascota.

## 4. Requerimientos No Funcionales
- **RNF-001 Plataforma**: Android nativo (Min SDK 24).
- **RNF-002 Rendimiento**: Carga fluida de imágenes (Lazy loading).
- **RNF-003 Usabilidad**: Diseño intuitivo similar a redes sociales populares para reducir curva de aprendizaje.
- **RNF-004 Disponibilidad**: Funcionamiento offline básico (caché de últimas publicaciones vistas).

## 5. Alcance MVP (Entrega Inmediata)
- Integración con Cámara y Galería [PRIORIDAD ALTA].
- Feed de visualización de items locales (Demo).
- Modelo de datos listo para escalado.
