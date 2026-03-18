================================================================================
                    OPDeckManager - Resumen del Proyecto
                    One Piece TCG Deck Manager
================================================================================

PROPÓSITO DEL PROYECTO
----------------------
Gestión de mazos para el juego de cartas coleccionables de One Piece (OP-TCG).
El sistema importa cartas desde una API externa (optcgapi.com) y permite a los
usuarios crear mazos personalizados con reglas válidas del juego.

CARACTERÍSTICAS PRINCIPALES
---------------------------
1. Importación automática de cartas desde optcgapi.com
2. Gestión de mazos (decks) con validación de reglas:
   - Máximo 60 cartas por deck
   - Máximo 4 copias de la misma carta
   - Soporte para cartas de 1-2 colores
3. Búsqueda y filtrado de cartas por:
   - Set (OP01, OP02, ST01, etc.)
   - Color (Red, Green, Blue, Yellow, Purple, Black)
   - Tipo (Leader, Character, Event, Stage)
   - Rareza

TECNOLOGÍAS UTILIZADAS
----------------------
- Java 21
- Spring Boot 4.0.3
- Spring Data JPA
- Liquibase (gestión de cambios en BD)
- H2 Database (archivo local)
- Lombok (reducción de boilerplate)
- HttpClient (para consumir API REST)
- Jackson (parseo JSON)

ESTRUCTURA DEL PROYECTO
-----------------------
src/main/java/com/project/OPDeckManager/
├── controller/          - Endpoints REST
├── domain/entities/     - Entidades JPA
├── repository/          - Interfaces de acceso a datos
├── service/             - Lógica de negocio
│   └── dto/            - Objetos de transferencia para API
└── Application.java     - Punto de entrada

src/main/resources/db/changelog/
├── db.changelog-master.xml    - Índice de cambios
└── changes/
    ├── 001-create-cards-table.xml
    ├── 002-create-colors-table.xml
    ├── 003-create-card-colors-table.xml
    ├── 004-create-decks-table.xml
    └── 005-create-deck-cards-table.xml

ENDPOINTS DISPONIBLES
---------------------
POST /api/import/all           - Importar TODAS las cartas de la API
POST /api/import/set/{setId}   - Importar un set específico

CONFIGURACIÓN DE BASE DE DATOS
------------------------------
- Tipo: H2 (archivo)
- Ubicación: ./data/onepiece-db
- Consola H2: http://localhost:8080/h2-console
- JDBC: jdbc:h2:file:./data/onepiece-db

FLUJO DE IMPORTACIÓN
--------------------
1. El usuario llama a POST /api/import/all
2. CardImportService itera sobre todos los sets (OP01-OP15, ST01-ST29, etc.)
3. Para cada set, hace GET a la API externa
4. Parsea el JSON a CardApiDTO
5. Convierte a entidad Card y guarda en BD
6. Asocia los colores (CardColor)
7. Registra el precio y metadatos

PRÓXIMOS PASOS SUGERIDOS
------------------------
1. Añadir endpoints para listar cartas (GET /api/cards)
2. Añadir endpoints para crear/decks (POST /api/decks)
3. Añadir validación de 60 cartas exactas en el deck
4. Añadir autenticación de usuarios
5. Migrar a MySQL/PostgreSQL en producción

================================================================================
