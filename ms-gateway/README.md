# ms-gateway

API Gateway del ecosistema de microservicios RPG. Es el **único punto de entrada**
expuesto al cliente: valida el token JWT y enruta cada petición al microservicio
correspondiente (Spring Cloud Gateway, reactivo).

## Responsabilidades

- Enrutamiento estático hacia los 8 microservicios por nombre de host/contenedor.
- Validación del JWT emitido por `ms-user` en cada petición (filtro global).
- Rutas públicas (sin token): `/api/v1/user/login`, `/api/v1/user/register`,
  `/swagger-ui*`, `/v3/api-docs*`, `/actuator*`.
- Propaga la identidad del usuario autenticado a los microservicios mediante las
  cabeceras `X-User-Id` y `X-Username`.

## Puerto

`8080`

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `JWT_SECRET` | (ver `application.properties`) | Secreto HMAC para validar el JWT. **Debe coincidir** con el de `ms-user`. |
| `MS_USER_URI` | `http://ms-user:8090` | URI del microservicio de usuarios |
| `MS_CHARACTER_URI` | `http://ms-character:8091` | URI del microservicio de personajes |
| `MS_ITEM_URI` | `http://ms-item:8092` | URI del microservicio de items |
| `MS_INVENTORY_URI` | `http://ms-inventory:8093` | URI del microservicio de inventario |
| `MS_QUEST_URI` | `http://ms-quest:8094` | URI del microservicio de misiones |
| `MS_CURRENCY_URI` | `http://ms-currency:8095` | URI del microservicio de monedas |
| `MS_SHOP_URI` | `http://ms-shop:8096` | URI del microservicio de tienda |
| `MS_COMBAT_URI` | `http://ms-combat:8097` | URI del microservicio de combate |

## Rutas

| Prefijo | Destino |
|---|---|
| `/api/v1/user/**` | ms-user |
| `/api/v1/character/**` | ms-character |
| `/api/v1/item/**` | ms-item |
| `/api/v1/inventory/**` | ms-inventory |
| `/api/v1/quest/**` | ms-quest |
| `/api/v1/currency/**` | ms-currency |
| `/api/v1/shop/**` | ms-shop |
| `/api/v1/combats/**` | ms-combat |

## Ejecutar

Requiere que los 8 microservicios estén accesibles en las URIs configuradas
(por defecto, nombres de contenedor Docker — ver `docker-compose.yml` en el
repositorio de orquestación).

```bash
./mvnw spring-boot:run
```

O con Docker:
```bash
docker build -t ms-gateway .
docker run -p 8080:8080 -e JWT_SECRET=mi-secreto ms-gateway
```

## Flujo de autenticación

```
POST /api/v1/user/register   (público)
POST /api/v1/user/login      (público) -> { "token": "..." }
GET  /api/v1/<recurso>        Authorization: Bearer <token>
```

Sin token o con token inválido: `401 Unauthorized`.

## Requisitos

- Java 21
- Spring Boot 4.0.6 / Spring Cloud 2025.1.1
