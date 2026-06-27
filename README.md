# RPG Microservices (DUOC)

Plataforma de un juego tipo RPG construida con **microservicios Spring Boot 4 (Java 21)**,
MySQL 8, comunicación entre servicios con **OpenFeign**, migraciones con **Flyway**,
documentación con **Swagger/OpenAPI**, un **API Gateway** (Spring Cloud Gateway) como
punto de entrada y **autenticación JWT** centralizada en el gateway.

## Arquitectura

```
                      ┌─────────────────────────┐
   Cliente  ───────▶  │  API Gateway  :8080     │  (valida JWT, enruta)
                      └───────────┬─────────────┘
                                  │
   ┌──────────┬──────────┬───────┼────────┬──────────┬──────────┬──────────┐
   ▼          ▼          ▼       ▼        ▼          ▼          ▼          ▼
 ms-user  ms-character ms-item ms-inv.  ms-quest ms-currency ms-shop  ms-combat
  :8090     :8091      :8092   :8093     :8094     :8095      :8096    :8097
   └──────────┴──────────┴───────┴────────┴──────────┴──────────┴──────────┘
                                  │
                          ┌───────▼────────┐
                          │  MySQL  :3306  │  (1 base de datos por servicio)
                          └────────────────┘
```

| Servicio       | Puerto | Base de datos  | Swagger UI                              |
|----------------|--------|----------------|-----------------------------------------|
| ms-gateway     | 8080   | —              | (punto de entrada seguro)               |
| ms-user        | 8090   | db_user        | http://localhost:8090/swagger-ui.html   |
| ms-character   | 8091   | db_character   | http://localhost:8091/swagger-ui.html   |
| ms-item        | 8092   | db_item        | http://localhost:8092/swagger-ui.html   |
| ms-inventory   | 8093   | db_inventory   | http://localhost:8093/swagger-ui.html   |
| ms-quest       | 8094   | db_quest       | http://localhost:8094/swagger-ui.html   |
| ms-currency    | 8095   | db_currency    | http://localhost:8095/swagger-ui.html   |
| ms-shop        | 8096   | db_shop        | http://localhost:8096/swagger-ui.html   |
| ms-combat      | 8097   | db_combat      | http://localhost:8097/swagger-ui.html   |

## Cómo ejecutar (Docker Compose)

Requisitos: **Docker** + **Docker Compose**.

```bash
docker compose up --build
```

Esto compila cada microservicio dentro de su imagen (Dockerfiles multi-stage, no
necesitas compilar a mano), levanta MySQL, ejecuta las migraciones Flyway y arranca
todos los servicios + el gateway.

Para detener:

```bash
docker compose down          # conserva los datos
docker compose down -v       # borra también el volumen de MySQL
```

### Variables de entorno (opcionales)

Tienen valores por defecto; puedes sobreescribirlas con un archivo `.env`
(ver `.env.example`):

| Variable     | Defecto                          | Uso                                  |
|--------------|----------------------------------|--------------------------------------|
| `DB_PASSWORD`| `duocgame2026`                   | Password de root de MySQL            |
| `JWT_SECRET` | `change-this-...-2026`           | Secreto HMAC para firmar/validar JWT |

> En producción cambia ambos valores y **no publiques** los puertos de los
> microservicios (deja solo el gateway expuesto).

## Autenticación (JWT)

1. **Registro**
   ```
   POST http://localhost:8080/api/v1/user/register
   { "email": "ana@duoc.cl", "username": "ana", "password": "secreta123" }
   ```
2. **Login** (devuelve el token)
   ```
   POST http://localhost:8080/api/v1/user/login
   { "email": "ana@duoc.cl", "password": "secreta123" }
   → { "token": "eyJhbGci...", "userId": 1, "username": "ana" }
   ```
3. **Usar el token** en el resto de llamadas a través del gateway:
   ```
   GET http://localhost:8080/api/v1/character/roster/1
   Authorization: Bearer eyJhbGci...
   ```

`login` y `register` son públicas; cualquier otra ruta a través del gateway exige
un `Authorization: Bearer <token>` válido (si falta o es inválido → `401`).
El gateway propaga la identidad a los servicios con las cabeceras `X-User-Id` y `X-Username`.

> Las contraseñas se almacenan **hasheadas con BCrypt**.

## Documentación de la API (Swagger)

Cada microservicio expone su propia documentación interactiva en
`http://localhost:<puerto>/swagger-ui.html` (ver tabla de arriba) y el JSON OpenAPI
en `http://localhost:<puerto>/v3/api-docs`.

## Desarrollo sin Docker

Cada módulo es una app Spring Boot estándar. Con un MySQL local (root sin password,
o ajusta `application.properties`) puedes ejecutar un servicio con:

```bash
cd ms-user
./mvnw spring-boot:run
```

## Estructura

```
Proyecto/
├── docker-compose.yml          # Orquesta MySQL + 8 microservicios + gateway
├── db-init/                    # Scripts de init de MySQL (crea las 8 bases)
├── ms-gateway/                 # API Gateway (rutas + validación JWT)
├── ms-user/                    # Usuarios + login/JWT + BCrypt
├── ms-character/               # Héroes base y roster del usuario
├── ms-item/                    # Catálogo de ítems
├── ms-inventory/               # Inventario por usuario
├── ms-currency/                # Billeteras / monedas
├── ms-shop/                    # Tienda (compra con saga de reembolso)
├── ms-quest/                   # Misiones y progreso
└── ms-combat/                  # Combates y recompensas
```
