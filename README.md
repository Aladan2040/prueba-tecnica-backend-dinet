
# 📦 MicroServicio de Carga de Pedidos

Este proyecto es una prueba técnica que implementa un API REST para la carga masiva de pedidos desde archivos CSV.
Esta construido siguiendo estrictamente los principios de Arquitectura Hexagonal (Ports & Adapters) para asegurar el desacoplamiento y la mantenibilidad




## 📋 **Características Principales**

* **Arquitectura Hexagonal:** Separación clara entre Dominio (reglas de negocio), Aplicación (casos de uso) e Infraestructura (Web, Persistencia).

* **Procesamiento Batch:** Estrategia optimizada para validar y persistir grandes volúmenes de datos con mínimo impacto en la base de datos.

* **Idempotencia:** Control de duplicidad basado en Idempotency-Key y Hash SHA-256 del contenido del archivo.

* **Flyway:** Gestión automatizada de versiones y migraciones de base de datos.

* **Validaciones de Negocio:** Reglas complejas (Cadena de frío, fechas futuras, clientes activos) validadas en el dominio.

* **Seguridad:** Configuración preparada para OAuth2 Resource Server (JWT).
## 🚀 **Guía de Inicio Rápido**

**Prerrequisitos**

Java 17 o superior.

Docker Desktop (recomendado para la base de datos).

Maven (opcional, el proyecto incluye el wrapper mvnw).
    
**1. Levantar la infraestructura (Base de datos)**  
El proyecto incluye un archivo docker-compose.yml en la raíz. Ejecuta:

```bash
  docker-compose up -d
```

Esto iniciará una instancia en PostgreSQL en el puerto 5433 con la base de datos ```pedidos_db```

**2. Ejecutar la aplicacion**  
Usa el wrapper de Maven incluido para iniciar el servicio:

**En Mac/Linux**
```bash
  ./mvnw spring-boot:run
```

**En Windows**
```bash
  mvnw.cmd spring-boot:run
```

*Nota: Al iniciar, Flyway ejecutará automáticamente los scripts `V1` y`V2` para crear las tablas y poblar los datos de prueba.*


## 🧪 **Como Probar la API**

**Documentación Interactiva (Swagger UI)**

Una vez levantada la aplicación, accede a:
👉 http://localhost:8080/swagger-ui.html

**Usando Postman**

En la raíz de este repositorio encontrarás el archivo `pedidos_collection.json`

**1.**  Abre Postman

**2.**  Importa dicho archivo 

**3.** Usa la petición pre-configurada "Cargar Pedidos CSV

**Archivos de Prueba (Samples)**  

En la carpeta `/samples` de este repositorio encontrarás el archivo `pedidos_prueba.csv` diseñado para probar tanto casos exitosos como errores de validación(clientes inactivos, zonas sin frio, fechas pasadas).


## ⚡ **Estrategia de Batch**

Para cumplir con el requisito de eficiencia y bajo consumo de recursos en cargas masivas(500-1000 reigstros), se implementó la siguiente lógica en `CargarPedidosService` :

* Lectura Streaming: Se lee el CSV utilizando OpenCSV para mapear las filas a objetos Java.

* Recolección de Claves: Se extraen todos los IDs (Clientes, Zonas, Números de Pedido) en Sets en memoria.

* Bulk Fetching (Consultas Masivas):

  Se realiza 1 sola consulta a la DB para traer todos los Clientes requeridos.

  Se realiza 1 sola consulta a la DB para traer todas las Zonas requeridas.

  Se realiza 1 sola consulta para verificar duplicados existentes.

* #Validación en Memoria: Se iteran los registros validando reglas de negocio contra los mapas en memoria (complejidad O(1)), evitando el problema "N+1 queries".

* Batch Insert: Los pedidos válidos se persisten utilizando saveAll(), aprovechando las optimizaciones de JDBC Batch de Hibernate.

**Resultado: El proceso realiza 4 interacciones con la base de datos independientemente del tamaño del archivo, en lugar de realizar una consulta por cada fila.**

## 🔒 **Seguridad**

El proyecto incluye la dependencia spring-boot-starter-oauth2-resource-server.

* Modo Evaluación (Actual): La configuración de seguridad (SecurityConfig.java) está en modo PERMISIVO por defecto. Esto permite probar los endpoints y visualizar Swagger sin necesidad de configurar un proveedor de identidad externo (IdP).

* Modo Producción: En el código se encuentran comentadas las líneas necesarias para activar la validación estricta de tokens JWT (.authenticated()), cumpliendo con el requisito de "Todas las rutas protegidas".

## 📂 **Estructura del Proyecto(Hexagonal)**

```bash
com.prueba.pruebaTecnica
├── application          # Casos de uso y Servicios (Orquestación)
├── domain               # Lógica pura de negocio (Modelos, Puertos)
└── infrastructure       # Adaptadores (REST Controller, JPA Entities, Config)
```

## 🛠 **Tecnologías**

* Spring Boot 3.5.8

* Java 17

* PostgreSQL

* Flyway Migration

* Lombok

* OpenCSV

* OpenAPI (SpringDoc)

* JUnit 5 & Mockito
