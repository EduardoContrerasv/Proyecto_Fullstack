# Plan de Pruebas Unitarias

## Objetivo
Validar las **reglas de negocio** de cada microservicio de forma aislada, sin base de
datos ni servicios externos reales, usando **mocks**.

## Framework y herramientas
- **JUnit 5 (Jupiter)** — motor de pruebas.
- **Mockito** (`@Mock`, `@InjectMocks`, `MockitoExtension`) — dobles de prueba para
  repositorios y clientes Feign.
- **AssertJ** (`assertThat`, `assertThatThrownBy`) — aserciones legibles.
- Todo viene incluido en `spring-boot-starter-test`.

## Estrategia
- Se prueba la **capa de servicio** (`*ServiceImpl`), donde vive la lógica de negocio.
- Las dependencias se **mockean**:
  - Repositorios JPA → `@Mock` (no se toca la BD).
  - Clientes Feign (`UserFeignClient`, `ItemFeignClient`, `CurrencyClient`, …) → `@Mock`
    (no se llama a otros servicios).
  - `PasswordEncoder` y `JwtService` (ms-user) → `@Mock`.
- Se verifican: el resultado, los cambios de estado, las **interacciones** con los mocks
  (`verify`, `ArgumentCaptor`) y las **excepciones** esperadas.
- Son pruebas rápidas y deterministas: `mvn test` corre sin Docker ni MySQL.

## Cómo ejecutar
```bash
# Todas las pruebas unitarias de un servicio
cd ms-currency && ./mvnw test -Dtest=*ServiceImplTest

# Una clase puntual
./mvnw test -Dtest=CurrencyServiceImplTest
```
> Las pruebas `*ApplicationTests` (`@SpringBootTest`) son de integración y requieren la
> BD; ejecútalas con el stack levantado. Las unitarias de abajo no la necesitan.

## Casos por microservicio

### ms-user — `UserServiceImplTest`
| Caso | Regla de negocio | Mocks |
|---|---|---|
| register con email nuevo | hashea la contraseña con BCrypt antes de guardar | UserRepository, PasswordEncoder |
| register con email existente | rechaza con 409 (Conflict) | UserRepository |
| login correcto | verifica hash y emite JWT | PasswordEncoder, JwtService |
| login contraseña incorrecta | rechaza con 401 | PasswordEncoder |
| login email inexistente | rechaza con 401 (sin revelar cuál falló) | UserRepository |
| updatePassword | re-hashea la nueva contraseña | PasswordEncoder |

### ms-currency — `CurrencyServiceImplTest`
| Caso | Regla |
|---|---|
| add sin billetera previa | crea billetera y acredita |
| add con billetera existente | acumula el saldo |
| deduct con fondos suficientes | descuenta |
| deduct con fondos insuficientes | lanza excepción y **no** guarda |
| deduct sin billetera | 404 |
| getSpecificBalance | devuelve el monto o 0 |

### ms-inventory — `InventoryServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| add cosmético ya poseído | máximo 1, rechaza duplicado | InventoryRepository, ItemFeignClient, UserFeignClient |
| add consumible sobre el límite | tope 20 por usuario | idem |
| add equipo apilable | acumula cantidad | idem |
| consume con stock insuficiente | rechaza | InventoryRepository, ItemFeignClient |
| consume que deja 0 | elimina la fila | idem |
| cantidad <= 0 | IllegalArgument (400) | — |

### ms-shop — `ShopServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| compra exitosa | descuenta moneda y entrega ítem | CurrencyClient, InventoryClient, ItemClient, ShopItemRepository |
| cantidad <= 0 | IllegalArgument (400) | — |
| falla la entrega al inventario | **reembolsa** la moneda (saga) y aborta | CurrencyClient, InventoryClient |
| listing inexistente | 404 | ShopItemRepository |

### ms-quest — `QuestServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| trackProgress que completa | marca completada y acredita la recompensa en ms-currency | UserQuestRepository, CurrencyFeignClient |
| trackProgress ya completada | no hace nada (idempotente) | UserQuestRepository |
| trackProgress sin seguimiento | EntityNotFound (404) | UserQuestRepository |
| deleteQuest inexistente | 404 | QuestRepository |

### ms-combat — `CombatServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| playCombat victoria | recompensa completa y acredita monedas vía Feign | CombatRepository, CurrencyFeignClient |
| playCombat derrota | recompensa reducida (10%) | CombatRepository |
| createScenario | normaliza valores nulos y fija moneda por defecto | CombatRepository |
| combate inexistente | 404 | CombatRepository |

### ms-character — `CharacterServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| createBaseCharacter por arquetipo | aplica multiplicadores de stats (ATTACK/VANGUARD/…) | BaseCharacterRepository |
| nombre de héroe duplicado | 409 | BaseCharacterRepository |
| unlock de héroe ya poseído | 409 | UserCharacterRepository, BaseCharacterRepository, UserFeignClient |
| unlock de héroe inexistente | 404 | idem |
| equip de ítem con slot incorrecto | rechaza (tipo no coincide) | UserCharacterRepository, InventoryClient, ItemClient |

### ms-item — `ItemServiceImplTest`
| Caso | Regla | Mocks |
|---|---|---|
| createItem válido | persiste y mapea a DTO | ItemRepository |
| precio negativo | rechaza | ItemRepository |
| findById inexistente | 404 | ItemRepository |
