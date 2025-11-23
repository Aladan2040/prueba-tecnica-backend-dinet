Microservicio de Carga de Pedidos (Prueba Técnica)

API REST desarrollada con Java 17 y Spring Boot 3 bajo Arquitectura Hexagonal. Permite la carga masiva de pedidos desde archivos CSV con validaciones de negocio, persistencia en PostgreSQL y manejo de idempotencia.

📋 Características Principales

Arquitectura Hexagonal: Separación estricta entre Dominio, Aplicación e Infraestructura.

Batch Processing: Estrategia de carga optimizada para minimizar consultas a BD.

Idempotencia: Validación por Idempotency-Key y Hash SHA-256 del archivo.

Flyway: Versionamiento de base de datos automatizado.

Seguridad: Preparado para OAuth2 Resource Server (JWT).

🚀 Instrucciones de Ejecución

Prerrequisitos

Java 17+

Docker (o PostgreSQL local en puerto 5433)

Maven

Paso 1: Base de Datos

Usa Docker, levanta la base de datos desde la raiz del proyecto con el comando:

docker-compose up -d


Esto iniciará PostgreSQL en el puerto 5433 con la base de datos pedidos_db.

Paso 2: Ejecutar la Aplicación

./mvnw spring-boot:run


Flyway ejecutará automáticamente las migraciones (V1 y V2) al iniciar.

Paso 3: Probar (Swagger)

Accede a la documentación interactiva:

URL: http://localhost:8080/swagger-ui.html

Endpoint: POST /pedidos/cargar

Nota sobre Seguridad: El proyecto está configurado en modo Permisivo para facilitar la evaluación. Para activar la validación estricta de JWT, revisar SecurityConfig.java.

🧪 Ejecución de Pruebas

El proyecto cuenta con pruebas unitarias usando JUnit 5 y Mockito.

Desde Terminal:

mvn test


Cobertura:
Se ha priorizado la cobertura del servicio de dominio CargarPedidosService, validando:

Carga exitosa completa.

Detección de errores de negocio (fechas pasadas, clientes inactivos).

Bloqueo por idempotencia duplicada.

⚡ Estrategia de Batch

Para cumplir con el requisito de procesar hasta 1000 registros eficientemente, se implementó la siguiente estrategia en CargarPedidosService:

Lectura en Memoria: Se parsea el CSV completo a objetos Java (OpenCSV).

Recolección de IDs: Se extraen todos los IDs de Clientes, Zonas y Números de Pedido en Set<String>.

Consultas Agrupadas (Bulk Fetch):

Se realiza 1 consulta para traer todos los Clientes involucrados (findAllById).

Se realiza 1 consulta para traer todas las Zonas involucradas.

Se realiza 1 consulta para verificar duplicados existentes.

Procesamiento en Memoria: Se itera la lista validando contra los mapas en memoria (O(1)).

Persistencia Batch: Los pedidos válidos se guardan en una sola operación transaccional (saveAll), aprovechando las optimizaciones de Hibernate JDBC Batch.

Resultado: Se reduce la interacción con la BD de ~4000 consultas (N*4) a solo 4 consultas + 1 insert batch, independientemente del tamaño del archivo.
