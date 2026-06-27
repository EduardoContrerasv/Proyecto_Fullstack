# ms-currency

Microservicio de monedas del juego: billeteras de los usuarios (GOLD, FRACTAL,
AMETHYST) y sus movimientos.

## Responsabilidades

- Acreditar (sumar) saldo a la billetera de un usuario.
- Descontar saldo, validando fondos suficientes.
- Consultar saldos (todos o por tipo de moneda).
- Es consumido vía Feign por `ms-shop` (compras), `ms-quest` (recompensas) y
  `ms-combat` (recompensas de combate).

## Puerto

`8095`

## Base de datos

`db_currency` (MySQL, Flyway).

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_currency?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/currency/add/{userId}` | Acreditar moneda |
| POST | `/api/v1/currency/deduct/{userId}` | Descontar moneda |
| GET | `/api/v1/currency/{userId}` | Saldos del usuario (todas las monedas) |
| GET | `/api/v1/currency/{userId}/{currencyType}` | Saldo de una moneda específica |

## Reglas de negocio relevantes

- Si no existe billetera para esa moneda al acreditar, se crea automáticamente.
- Descuento sin fondos suficientes → error de negocio (`400`).
- Descuento sobre billetera inexistente → `404 Not Found`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```

## Documentación interactiva

`http://localhost:8095/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=CurrencyServiceImplTest
```
Valida (con Mockito, sin BD): creación/acumulación de billetera, descuento,
fondos insuficientes (no persiste cambios), billetera inexistente (404).

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
