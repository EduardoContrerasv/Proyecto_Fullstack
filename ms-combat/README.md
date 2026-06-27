# ms-combat

Microservicio de combate: escenarios de batalla, asignación de equipo y
resolución del combate con recompensas.

## Responsabilidades

- Crear escenarios de combate (enemigo, capacidad, recompensas base).
- Asignar un equipo de personajes del usuario al combate (valida contra
  `ms-character`).
- Resolver el combate (resultado aleatorio victoria/derrota) y acreditar las
  monedas ganadas en `ms-currency`.

## Puerto

`8097`

## Base de datos

`db_combat` (MySQL, Flyway). Tablas: `combats`, `combat_participants`,
`combat_character_names`.

## Dependencias de otros servicios (Feign)

| Servicio | Uso |
|---|---|
| `ms-character` | Obtener los personajes desbloqueados del usuario para el equipo |
| `ms-currency` | Acreditar las monedas ganadas al finalizar el combate |

## Variables de entorno

| Variable | Default |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_combat?...` |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/combats/setup` | Crear escenario de combate |
| POST | `/api/v1/combats/{id}/assign-team/{userId}` | Asignar equipo de personajes |
| POST | `/api/v1/combats/{id}/play` | Resolver el combate |
| GET | `/api/v1/combats` | Listar combates |

## Reglas de negocio relevantes

- **Victoria**: recompensa completa (experiencia y monedas base).
- **Derrota**: recompensa reducida al 10%.
- En ambos casos se acreditan las monedas ganadas en `ms-currency` (si hay
  usuario asignado).
- Combate o personaje inexistente → `404 Not Found`.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```
Requiere MySQL y que `ms-character` y `ms-currency` estén accesibles.

## Documentación interactiva

`http://localhost:8097/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=CombatServiceImplTest
```
Valida (con Mockito, sin BD): coherencia de recompensa según resultado,
acreditación de monedas, valores por defecto del escenario, combate
inexistente (404).

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
