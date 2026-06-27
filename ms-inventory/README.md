# ms-inventory

Microservicio de inventario: qué ítems posee cada usuario y en qué cantidad.

## Responsabilidades

- Agregar ítems al inventario de un usuario, con límites según tipo:
  - **Cosméticos**: máximo 1 por usuario.
  - **Consumibles**: máximo 20 unidades acumuladas por usuario.
  - **Equipo** (armas/armaduras): máximo 500 unidades acumuladas por usuario.
- Consumir (descontar/eliminar) ítems del inventario.
- Consultar el inventario de un usuario, enriquecido con datos de `ms-user`
  (username) y `ms-item` (nombre y tipo del ítem).

## Puerto

`8093`

## Base de datos

`db_inventory` (MySQL, Flyway).

## Dependencias de otros servicios (Feign)

| Servicio | Uso |
|---|---|
| `ms-user` | Validar que el usuario existe; obtener su username |
| `ms-item` | Validar que el ítem existe; obtener su nombre y tipo |

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_inventory?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/inventory/add` | Agregar ítem al inventario |
| POST | `/api/v1/inventory/consume` | Consumir/descontar ítem del inventario |
| GET | `/api/v1/inventory/getUserId/{userId}` | Inventario completo del usuario |
| GET | `/api/v1/inventory/user/{userId}/item/{itemId}` | Detalle de un ítem específico |
| GET | `/api/v1/inventory/user/{userId}/item/{itemId}/check` | ¿El usuario posee este ítem? (boolean) |

## Reglas de negocio relevantes

- Cantidad ≤ 0 → `400 Bad Request`.
- Límite de tipo excedido (cosmético duplicado, consumibles > 20, equipo > 500) → error de negocio.
- Stock insuficiente al consumir → error de negocio.
- Usuario o ítem inexistente (validado contra otros servicios) → `404 Not Found`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```
Requiere MySQL y que `ms-user` y `ms-item` estén accesibles.

## Documentación interactiva

`http://localhost:8093/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=InventoryServiceImplTest
```

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
