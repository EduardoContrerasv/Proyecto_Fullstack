# ms-quest

Microservicio de misiones: catálogo de misiones, asignación a usuarios y
seguimiento de progreso con recompensas.

## Responsabilidades

- CRUD de misiones (título, descripción, tipo, objetivo, recompensas de
  experiencia y oro).
- Asignar una misión a un usuario (valida contra `ms-user`).
- Registrar avance de una misión asignada; al completarse, acredita la
  recompensa de oro en `ms-currency`.

## Puerto

`8094`

## Base de datos

`db_quest` (MySQL, Flyway). Tablas: `quests`, `user_quest`.

## Dependencias de otros servicios (Feign)

| Servicio | Uso |
|---|---|
| `ms-user` | Validar que el usuario existe al asignar una misión |
| `ms-currency` | Acreditar la recompensa de oro al completar una misión |

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_quest?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/quest` | Crear misión |
| GET | `/api/v1/quest` | Listar misiones |
| GET | `/api/v1/quest/{id}` | Misión por ID |
| DELETE | `/api/v1/quest/{id}` | Eliminar misión |
| POST | `/api/v1/quest/{id}/assign/{userId}` | Asignar misión a un usuario |
| PUT | `/api/v1/quest/user/{userId}/quest/{questId}/progress?delta=N` | Registrar avance |

## Reglas de negocio relevantes

- Usuario inexistente al asignar → `404 Not Found`.
- Misión inexistente → `404 Not Found`.
- Avanzar una misión ya completada es **idempotente** (no hace nada).
- Al llegar el progreso a 0 objetivos restantes, se marca completada y se
  acredita la recompensa de oro vía `ms-currency`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```
Requiere MySQL y que `ms-user` y `ms-currency` estén accesibles.

## Documentación interactiva

`http://localhost:8094/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=QuestServiceImplTest
```
Valida (con Mockito, sin BD): completar misión y acreditar recompensa, avance
parcial, idempotencia, seguimiento inexistente (404).

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
