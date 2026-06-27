# ms-shop

Microservicio de tienda: catálogo de ítems en venta y compras de los usuarios.

## Responsabilidades

- Publicar ítems en la tienda con su precio y tipo de moneda.
- Procesar compras: descuenta la moneda (`ms-currency`) y entrega el ítem al
  inventario del comprador (`ms-inventory`), validando el ítem en `ms-item`.
- **Compensación tipo saga**: si la moneda se descuenta pero la entrega al
  inventario falla, se reembolsa automáticamente al usuario.

## Puerto

`8096`

## Base de datos

`db_shop` (MySQL, Flyway). Tabla: `shop_catalog`.

## Dependencias de otros servicios (Feign)

| Servicio | Uso |
|---|---|
| `ms-item` | Validar el ítem y obtener su nombre |
| `ms-currency` | Descontar y, si es necesario, reembolsar moneda |
| `ms-inventory` | Entregar el ítem comprado al inventario del usuario |

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_shop?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/shop/admin/create` | Publicar ítem en la tienda (admin) |
| GET | `/api/v1/shop/catalog` | Catálogo de la tienda |
| POST | `/api/v1/shop/purchase` | Comprar un ítem |

## Reglas de negocio relevantes

- Cantidad ≤ 0 → `400 Bad Request`.
- Listing de tienda inexistente → `404 Not Found`.
- Ítem inexistente en `ms-item` → `404 Not Found`.
- Fondos insuficientes o billetera inexistente → `400 Bad Request`.
- Falla al entregar al inventario tras haber cobrado → se reembolsa la moneda
  automáticamente y se informa el error (`502 Bad Gateway`).

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```
Requiere MySQL y que `ms-item`, `ms-currency` y `ms-inventory` estén accesibles.

## Documentación interactiva

`http://localhost:8096/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=ShopServiceImplTest
```
Valida (con Mockito, sin BD): compra exitosa, cantidad inválida, reembolso ante
falla de entrega (saga), listing inexistente.

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
