# Saliendo de Casa

App Android nativa (Kotlin + Jetpack Compose) para recordar qué llevar antes de salir de casa: checklists persistentes, organizadas por categorías y listas para usar en cualquier momento.

---

## 1. Solución

La aplicación resuelve un problema cotidiano: salir de casa y olvidarse de cosas. Para ello ofrece:

| # | Funcionalidad | Cómo se resuelve |
|---|---------------|------------------|
| 1 | **Agregar y eliminar ítems** | `MainViewModel.addItem()` / `deleteItem()` → `ItemRepository` → Room (`@Insert` / `@Delete`). Inserciones en blanco y duplicados (ignorando mayúsculas) se descartan antes de tocar la base de datos. |
| 2 | **Distintas checklists por salida** | Cuatro listas predefinidas (**General, Trabajo, Ejercicio, Estudio**) diferenciadas por el campo `listName` en la misma tabla `items`. Cambiar de pestaña re-consulta con `SELECT * FROM items WHERE listName = :listName`. |
| 3 | **Recordar la última checklist abierta** | `SharedPreferences` (clave `KEY_LAST_LIST`). Se escribe en `changeList()` y se lee en el `init` del ViewModel, restaurando la lista al volver a abrir la app. |
| 4 | **Restablecer los checks sin borrar los ítems** | Query específica `UPDATE items SET isChecked = 0 WHERE listName = :listName` (`ItemDao.resetChecks`) |
| 5 | **Botón "¡Todo Listo!"** | Llama a `MainViewModel.resetChecks()`, que dispara el reseteo anterior dejando la lista completa pero desmarcada, lista para una nueva salida. |

**Persistencia:** Room3 sobre SQLite (`saliendo_de_casa_local_db`), 100% local.

---

## 2. Arquitectura

### 2.1. Patrón de paquetes: Package-by-Feature

Se eligió **Package-by-Feature** porque agrupa el código que cambia junto en un solo lugar, aumentando la **cohesión**, disminuyendo la **dispersión** y así evitando saltar por carpetas al hacer una funcionalidad, facilitando la **mantenibilidad** y mejorando la **experiencia de desarrollo**.

En vez de `data/`, `domain/`, `presentation/` de todo el proyecto en paralelo (package-by-layer), cada feature contiene sus tres capas internamente. Agregar una nueva funcionalidad = agregar **un solo paquete** nuevo, sin tocar los existentes.

### 2.2. Estructura de paquetes

```
com.example.saliendodecasa/
│
├── MainApplication.kt                   # Punto de entrada de DI: crea BD y Repositorio (lazy)
│
├── core/                                # INFRAESTRUCTURA COMPARTIDA
│   └── database/
│       └── LocalAppDatabase.kt          # Base de datos Room (SQLite, singleton)
│
└── checklist/                           # FEATURE: Salida de Casa
    ├── data/                            # CAPA DE DATOS
    │   ├── ItemEntity.kt                # Entidad SQLite (tabla "items")
    │   ├── ItemDao.kt                   # Interfaz de consultas SQL
    │   ├── ItemMapper.kt                # Mappers Entity <-> Domain (Kotlin puro)
    │   └── LocalItemRepositoryImpl.kt   # Implementación del repositorio con Mappers
    │
    ├── domain/                          # CAPA DE DOMINIO (Kotlin puro)
    │   ├── ItemModel.kt                 # Modelo de negocio (Kotlin puro, sin Android)
    │   └── ItemRepository.kt            # Contrato del repositorio (interfaz)
    │
    └── presentation/                    # CAPA DE PRESENTACIÓN (MVVM)
        ├── MainActivity.kt              # Solo dibuja la UI (Compose) y escucha cambios
        ├── MainViewModel.kt             # Pide datos al repositorio y sostiene el estado
        └── MainUiState.kt               # Estado de la pantalla (currentListName + items)
```

### 2.3. Las tres capas

- **`domain/`** — Kotlin puro, sin dependencias de Android. Define *qué* es un ítem (`ItemModel`) y *qué* se puede hacer con él (`ItemRepository`: obtener por lista, insertar, actualizar, borrar, resetear checks).
- **`data/`** — Única implementación del contrato. `ItemEntity` es lo que vive en SQLite, `ItemDao` traduce anotaciones Room a SQL, y `ItemMapper` + `LocalItemRepositoryImpl` traducen entre el mundo de la base de datos y el mundo del dominio. La capa de dominio **nunca** ve una `ItemEntity`.
- **`presentation/`** — `MainActivity` solo compone UI y delega todo en `MainViewModel`, que expone `uiState: StateFlow<MainUiState>` y ejecuta las operaciones en `viewModelScope` con coroutines.

### 2.4. Composición de dependencias

La app es de un solo módulo y la inyección es manual y explícita en `MainApplication`:

```kotlin
class MainApplication : Application() {
    val database by lazy { LocalAppDatabase.getDatabase(this) }
    val itemRepository by lazy { LocalItemRepositoryImpl(database.itemDao()) }
}
```

`MainActivity` construye el `MainViewModel` con `viewModelFactory` pasándole `application.itemRepository` y `SharedPreferences`. Así el ViewModel solo conoce el contrato `ItemRepository`, nunca Room.

---

