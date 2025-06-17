# Tenpo Challenge Backend 🏦

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)

> **Backend Challenge x Sebastián Kravetz @ Tenpo CL**

Servicio Backend API REST construido en Spring Boot, que expone servicios de cálculo con aplicación de porcentaje adicional. Cuenta con caché para el valor del porcentaje obtenido de servicio externo y registro asíncrono de historial de llamadas. Esto fue desarrollado como parte del desafío técnico de Backend Engineer para Tenpo CL.

## Tabla de Contenidos

- [Componentes](#componentes)
- [Características Principales](#características-principales)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Configuración y Ejecución](#configuración-y-ejecución)
- [Variables de Entorno](#variables-de-entorno)
- [Endpoints](#endpoints)
- [Testing](#testing)
- [Documentación API](#documentación)

## Componentes

El proyecto está compuesto por **dos microservicios** independientes y una **base de datos**:

### 1. **Servicio: ms-calculator** (Puerto 8080)
Servicio principal que maneja:
- Cálculos matemáticos con porcentajes dinámicos
- Sistema de caché (30 minutos TTL)
- Registro asíncrono de historial de llamadas
- Conexión con la base de datos PostgreSQL
  - Este servicio fue diseñado con una estructura de proyecto que sigue una arquitectura híbrida entre 
    *Clean Architecture* y *Vertical Slicing*, separando módulos principalmente basados en *dominio* (contextos de negocio, inspirado en Domain-Driven-Design), y luego dentro de cada feature definendo capas de arquitectura que tengan alta cohesión (modelos, dtos, servicios, controllers).
    - Ejemplo de la estructura con explicaciones:

    ````
    - com.tenpo.mscalculator
      - calculation     # feature: cálculos c/ porcentaje adicional
          - docs                      # decorador custom que abstrae los docs swagger
          - domain                    # objetos de dominio
          - dto                       # objetos de transporte de datos (o dto's)
          - mapper                    # métodos mapeadores (por ejemplo, de dto's a objeto de dominio, o de entidad relacional a objeto de dominio)
            - CalculationController   # controlador de transporte http
            - CalculationService      # lógica de negocios ó servicio
      - config          # implementaciones de @Configuration
      - history         # feature: historial de solicitudes
      - infrastructure  # capa de infraestructura
      - percentage      # feature: obtener porcentaje dinámico
      - shared          # utilitarios compartidos (sin lógica de negocios)
    ````

### 2. **Servicio: ms-percentage** (Puerto 8081)
Microservicio que proporciona:
- Porcentajes dinámicos
- Simulación de servicio externo

### 3. **Base de datos** (Puerto 5432)
- Se incluye:
  - Definición SQL con la que se pobló originalmente la DB: [`/assets/db.sql`](./assets/db.sql)
  - Volumen de Docker con datos poblados y conectado al contenedor de Postgres: 
    [`/docker/pgdata`](./docker/pgdata)

## Características Principales

### Cálculo con Porcentaje Dinámico
- Recibe dos números (`firstAmount` y `secondAmount`) en formato decimal.
- Los suma y aplica un porcentaje obtenido del servicio externo
- Fórmula: `resultado = (num1 + num2) + ((num1 + num2) * porcentaje)`

### Sistema de Caché con TTL (Time-to-live)
- **TTL configurable**: 30 minutos por defecto
- **Fallback automático**: Si el servicio externo falla, utiliza el último valor cacheado hasta la duración del TTL.
- **Manejo de errores**: Si no hay valor disponible en la caché, retorna error.

### Historial de Llamadas
- **Registro asíncrono**: No afecta el rendimiento de las respuestas
- **Almacenamiento completo**: Fecha, endpoint, parámetros, respuesta exitosa ó error
- **Paginación**: Endpoints con soporte para paginación y ordenamiento
- **HATEOAS**: La paginación también cuenta con links para las páginas previas ó siguientes.
- **Persistencia**: Almacenado en PostgreSQL. Los JSON del payload de entrada así como la 
  respuesta de salida se almacenan en formato JSONB

## Tecnologías Utilizadas

### Backend
- **Java 21** 
- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **Spring Cache (c/ Caffeine)**
- **Spring WebFlux**
- **Spring HATEOAS**

### Base de Datos
- **PostgreSQL 15**
- **Hibernate (ORM)**

### Documentación y Testing
- **SpringDoc OpenAPI 3** - Documentación Swagger
- **JUnit 5** - Testing framework
- **Mockito** - Mocking para tests
- **Testcontainers** - Tests de integración

### DevOps
- **Docker & Docker Compose** - Para containeizar los servicios
- **Gradle** - Gestor de dependencias


## Configuración y Ejecución

### Prerrequisitos
- **Docker CLI ó Docker Desktop** instalado
  - Si corre en una VM cloud, debe instalarse el CLI de Docker Compose por separado.
- **Java 21**
- **Git**

### Ejecución con Docker Compose

1. **Clonar el repositorio**
```bash
git clone <url-del-repositorio>
cd tenpocl_backend_challenge
```

2. **Levantar todos los servicios**
- Opción 1: Con Imágenes buildeadas localmente:
```bash
docker compose -f docker-compose.yml up
# o si quiere forzar el rebuild de los contenedores:
docker compose -f --build docker-compose.yml up
# o si utiliza una versión anterior de docker compose:
docker-compose ...
```  

- Opción 2: Con Imágenes desde Docker Hub:
  > NOTA: Las imágenes subidas a Docker Hub soportan arquitecturas `linux/amd64` y `linux/arm64`
```bash
  docker compose -f docker-compose.remote.yml up
  # o si quiere forzar el rebuild de los contenedores:
  docker compose -f --build docker-compose.remote.yml up
  # o si utiliza una versión anterior de docker compose:
  docker-compose ...
```

3. **Verificar que los servicios están funcionando**
```bash
# Verificar estado de los contenedores
docker ps

# Ver logs del servicio calculadora
docker compose logs -f ms-calculator

# Ver logs del servicio porcentajes
docker compose logs -f ms-percentage
```

4. **Rutas expuestas**
- **API Calculadora**: http://localhost:8080/api/v1
- **API Porcentajes**: http://localhost:8081/api/v1
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui/index.html
- **Base de Datos**: localhost:5432

### Detener los servicios
```bash
docker compose down
# ó en versiones anteriores del compose CLI:
docker-compose down
```

### Resetear la base de datos
```bash
docker compose down -v # elimina el volumen de postgresql
# ó en versiones anteriores del compose CLI:
docker-compose down -v  
```

## Variables de Entorno

### Configuración del Servicio Calculadora

| Variable | Descripción                                           |
|----------|-------------------------------------------------------|
| `MSCALC_DB_HOST` | Host de PostgreSQL                            |
| `MSCALC_DB_PORT` | Puerto de PostgreSQL                          |
| `MSCALC_DB_NAME` | Nombre de la base de datos                    |
| `MSCALC_DB_USER` | Usuario de la base de datos                   |
| `MSCALC_DB_PASSWORD` | Contraseña de la base de datos            |
| `MSCALC_API_VERSION` | Versión de la API                         |
| `MSCALC_API_CACHE_TTL` | TTL del caché de porcentajes            |
| `MSCALC_WEBSERVICE_PERCENTAGE` | URL del servicio de porcentajes |

### Personalizar Configuración

Para modificar las variables, edita el archivo `docker-compose.yml`:

```yaml
services:
  ms-calculator:
    environment:
      - MSCALC_API_CACHE_TTL=15m    # Cambiar TTL a 15 minutos
      - MSCALC_DB_PASSWORD=otra_contraseña
```

## Endpoints

### Servicio Calculadora

#### `POST /api/v1/calculation/calculate`
Realiza cálculo con porcentaje dinámico adicional.

**Request Body:**
```json
{
  "firstAmount": 1.0,
  "secondAmount": 7.0
}
```

**Ejemplo Response:**
```json
{
  "result": 9.520,
  "percentageApplied": 0.19
}
```

#### `GET /api/v1/history?page=0&size=10&order=DESC`
Obtiene el historial de llamadas con paginación y links de navegación a página previa o siguiente (HATEOAS).

**Ejemplo Response:**
```json
{
  "_embedded": {
    "records": [
      {
        "requestId": 11,
        "requestDate": "2025-06-16T20:49:27.034936Z",
        "requestEndpoint": "/calculation/calculate",
        "requestType": "SUCCESS",
        "requestParameters": {
          "firstAmount": 1.0,
          "secondAmount": 7.0
        },
        "responseBody": {
          "result": 9.52,
          "percentageApplied": 0.19
        }
      },,
      ...,
      ...
    ]
  },
  "_links": {
        "first": {
            "href": "http://localhost:8080/api/v1/history?order=DESC&page=0&size=4&sort=requestDate,desc"
        },
        "self": {
            "href": "http://localhost:8080/api/v1/history?order=DESC&page=0&size=4&sort=requestDate,desc"
        },
        "next": {
            "href": "http://localhost:8080/api/v1/history?order=DESC&page=1&size=4&sort=requestDate,desc"
        },
        "last": {
            "href": "http://localhost:8080/api/v1/history?order=DESC&page=2&size=4&sort=requestDate,desc"
        }
    },
    "page": {
        "size": 4,
        "totalElements": 11,
        "totalPages": 3,
        "number": 0
    }
}
```

### Servicio Porcentajes (Puerto 8081)

#### `GET /api/v1/percentage`
Obtiene un porcentaje dinámico.

**Ejemplo Response:**
```json
{
  "percentage": 0.15
}
```

## Testing

![test_exec_results.png](assets/test_exec_results.png)

### Ejecutar Tests Unitarios
```bash
# Desde el directorio ms_calculator_tenpo
./gradlew test
```

### Tests Implementados
- **CalculationControllerTest**: Tests del controlador principal de cálculos con porcentaje.
- **CalculationServiceTest de integración**: Tests del servicio de cálculos con porcentaje.
- **PercentageWebClientTest**: Tests del cliente HTTP hacia el servicio de porcentajes, con simulación de fallos


## Documentación

### Postman

- [Colección en Postman.com](https://www.postman.com/valhallaa/workspace/shared/collection/29461457-d2b2dd2a-5654-4666-9b21-c3cc6c33fc00?action=share&creator=29461457)
  - Accesible inmediatamente
- [Colección en archivo .JSON](./assets/SPRING%20BOOT%20-%20TENPO%20CHALLENGE%20-%20CALCULATOR%20MICROSERVICE.postman_collection.json)
  - Para descargar y cargar en [Postman](https://www.postman.com/downloads/) u otro compatible (ej: [Bruno](https://www.usebruno.com))

### Swagger UI
Una vez que la aplicación está ejecutándose, se puede acceder a la documentación interactiva generada con Swagger UI:

**URL**: http://localhost:8080/api/v1/swagger-ui/index.html

![img.png](assets/swagger_ui.png)

### OpenAPI Spec
- **JSON**: http://localhost:8080/api/v1/api-docs
- **YAML**: http://localhost:8080/api/v1/api-docs.yaml


## Desarrollador
**Desarrollado por**: Sebastián Kravetz  
**Email**: root@sebastiankravetz.dev  
**Website**: https://sebastiankravetz.dev  

**Para TENPO CL**
