# ms-item

Microservicio de catálogo de ítems del juego (armas, armaduras, consumibles,
monedas de juego y cosméticos).

## Responsabilidades

- CRUD del catálogo de ítems.
- Búsqueda por nombre, nivel requerido, rareza y tipo.
- Es consultado vía Feign por `ms-character`, `ms-inventory` y `ms-shop` para
  validar la existencia y el tipo de un ítem.

## Puerto

`8092`

## Base de datos

`db_item` (MySQL, Flyway).

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_item?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/item` | Listar todos los ítems |
| GET | `/api/v1/item/getItemId/{id}` | Ítem por ID |
| GET | `/api/v1/item/rarity/{rarity}` | Ítems por rareza (COMMON, UNCOMMON, RARE, EPIC, LEGENDARY) |
| GET | `/api/v1/item/type/{itemType}` | Ítems por tipo (WEAPON, ARMOR, CONSUMABLE, CURRENCY, COSMETIC) |
| POST | `/api/v1/item` | Crear ítem |
| PUT | `/api/v1/item/{id}` | Actualizar ítem |
| DELETE | `/api/v1/item/{id}` | Eliminar ítem |

## Reglas de negocio relevantes

- Nombre vacío, precio negativo o nivel requerido negativo → `400 Bad Request`.
- Ítem inexistente → `404 Not Found`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```

## Documentación interactiva

`http://localhost:8092/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=ItemServiceImplTest
```

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
