# ms-user

Microservicio de usuarios: registro, autenticación y gestión de cuentas. Emite el
**JWT** que el API Gateway valida en el resto de peticiones del ecosistema.

## Responsabilidades

- Registro de usuarios con contraseña hasheada (**BCrypt**).
- Login: verifica credenciales y emite un JWT firmado (HMAC-SHA256).
- CRUD de usuarios (email, contraseña, estado de cuenta, nivel).

## Puerto

`8090`

## Base de datos

`db_user` (MySQL). Esquema gestionado con **Flyway** (`src/main/resources/db/migration`).

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql-db:3306/db_user?...` | URL de conexión a MySQL |
| `SPRING_DATASOURCE_PASSWORD` | (vacío) | Password de la BD |
| `JWT_SECRET` | (ver `application.properties`) | Secreto HMAC para firmar el JWT. **Debe coincidir** con el del API Gateway. |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Tiempo de expiración del token |

## Endpoints

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/api/v1/user/register` | Registrar usuario | Pública |
| POST | `/api/v1/user/login` | Login, devuelve `{ token, userId, username }` | Pública |
| GET | `/api/v1/user/all` | Listar todos los usuarios | Bearer |
| GET | `/api/v1/user/getUserId/{id}` | Usuario por ID | Bearer |
| GET | `/api/v1/user/getEmail/{email}` | Usuario por email | Bearer |
| GET | `/api/v1/user/getstatus/{status}` | Usuarios por estado de cuenta | Bearer |
| PUT | `/api/v1/user/updateEmail/{id}` | Actualizar email | Bearer |
| PUT | `/api/v1/user/updatePassword/{id}` | Actualizar contraseña (re-hashea) | Bearer |
| PUT | `/api/v1/user/updateStatus/{id}/{status}` | Actualizar estado de cuenta | Bearer |
| DELETE | `/api/v1/user/{id}` | Eliminar usuario | Bearer |

> En este servicio no se valida el JWT directamente (la validación es responsabilidad
> del API Gateway); "Bearer" indica que, en el ecosistema completo, ese endpoint
> requiere pasar por el gateway con un token válido.

## Ejecutar de forma standalone

```bash
./mvnw spring-boot:run
```

Requiere un MySQL accesible. Variables mínimas:
```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/db_user?createDatabaseIfNotExist=true"
export SPRING_DATASOURCE_PASSWORD="tu-password"
```

## Documentación interactiva

`http://localhost:8090/swagger-ui.html`

## Pruebas unitarias

```bash
./mvnw test -Dtest=UserServiceImplTest
```
Valida (con Mockito, sin BD): hash de contraseñas, email duplicado (409),
emisión de JWT en login, credenciales inválidas (401), re-hash al actualizar
contraseña.

## Requisitos

- Java 21
- MySQL 8
- Spring Boot 4.0.6
