# ms-character

Microservicio de personajes: catálogo de héroes base, desbloqueo para usuarios,
roster del jugador y equipamiento.

## Responsabilidades

- Definición de héroes base por arquetipo (ATTACK, VANGUARD, STRATEGIST, SUPPORT),
  cada uno con multiplicadores propios de salud/ataque/defensa.
- Desbloqueo de un héroe para un usuario (valida contra `ms-user`).
- Roster de personajes del usuario y equipamiento de ítems (valida contra
  `ms-inventory` y `ms-item`).

## Puerto

`8091`

## Base de datos

`db_character` (MySQL, Flyway). Tablas: `base_characters`, `user_characters`.

## Dependencias de otros servicios (Feign)

| Servicio | Uso |
|---|---|
| `ms-user` | Verificar que el usuario existe antes de desbloquear un héroe |
| `ms-inventory` | Verificar que el usuario posee el ítem a equipar |
| `ms-item` | Verificar el tipo del ítem a equipar (arma/armadura/cosmético) |

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_character?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/character/admin/create` | Crear héroe base (admin) |
| GET | `/api/v1/character` | Listar héroes base (roster admin) |
| POST | `/api/v1/character/unlock` | Desbloquear un héroe para un usuario |
| GET | `/api/v1/character/roster/{userId}` | Roster de personajes del usuario |
| POST | `/api/v1/character/equip` | Equipar un ítem en un personaje |

## Reglas de negocio relevantes

- Nombre de héroe duplicado → `409 Conflict`.
- Héroe ya poseído por el usuario → `409 Conflict`.
- Usuario o héroe inexistente → `404 Not Found`.
- Equipar un ítem cuyo tipo no corresponde al slot (ej. armadura en slot de arma) → `400 Bad Request`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```
Requiere MySQL y que `ms-user`, `ms-inventory` y `ms-item` estén accesibles en las
URLs configuradas en los `@FeignClient` (por defecto, nombres de host Docker).

## Documentación interactiva

`http://localhost:8091/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=CharacterServiceImplTest
```

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
