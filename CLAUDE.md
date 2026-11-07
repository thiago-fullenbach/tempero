# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Tempero is a multi-module Android app in Kotlin + Jetpack Compose that browses recipes from [TheMealDB](https://www.themealdb.com/api.php). Root package: `br.com.thiago.tempero`. minSdk 28, targetSdk/compileSdk 36, JVM target 11. Code comments and KDoc are written in Portuguese.

## Commands

Run from the repo root (on Windows use `.\gradlew.bat` instead of `./gradlew`):

- Build debug APK: `./gradlew assembleDebug`
- Install on a device/emulator: `./gradlew installDebug`
- Unit tests (all modules): `./gradlew testDebugUnitTest` (the JVM-only modules `:core:model`/`:core:domain` expose it as an alias of `test`)
- One module's tests: `./gradlew :feature:explore:testDebugUnitTest`
- Single unit test: `./gradlew :feature:explore:testDebugUnitTest --tests "br.com.thiago.tempero.feature.explore.ExploreViewModelTest"`
- Instrumented tests (device required): `./gradlew connectedDebugAndroidTest`
- Android lint: `./gradlew lint`

Dependencies and plugin versions live in the version catalog `gradle/libs.versions.toml`; reference them as `libs.*` in each module's `build.gradle.kts`. Parallel execution, build cache and configuration cache are enabled in `gradle.properties`.

## Configuration

The TheMealDB API key is read from `local.properties` (`MEALDB_API_KEY=...`) or the `MEALDB_API_KEY` env var into `BuildConfig.MEALDB_API_KEY` of `:core:network`, defaulting to the public test key `"1"`. The key is embedded in the base URL path (`THE_MEAL_DB_API_BASE_URL` in `core/network/.../api/TheMealDBAPI.kt`). `ANALYTICS_IN_DEBUG=true` in `local.properties` enables Analytics in debug builds (`BuildConfig` of `:app`).

## Modules

The package of each module is `br.com.thiago.tempero.<path>` (e.g. `:core:domain` → `...core.domain`, `:feature:detail` → `...feature.detail`).

```
:app ──► :feature:{explore,search,favorites,detail} ──► :core:ui ──► :core:designsystem
  │                          └───────────────────────► :core:domain ──► :core:model
  ├──► :core:data ──► :core:network | :core:database | :core:common  (and :core:domain)
  └──► :core:analytics ──► :core:domain
:core:testing (tests only) ──► domain test fixtures, :core:network, :core:database
```

**Dependency rules:** a feature never depends on another feature, and features only see `core:ui`, `core:designsystem`, `core:domain` and `core:model` (never `data`/`network`/`database`). Only `:app` knows the implementations and wires them through Hilt (`:core:data` exposes `common`/`network`/`database` with `api` so the app-level Hilt aggregation finds their modules).

| Module | Kind | Contents |
|---|---|---|
| `:core:model` | JVM | `Meal`, `MealCategory`, `MealStep`, `MealIngredient`, `DataError` sealed hierarchy (`Network`, `Unavailable`, `InvalidResponse`, `Storage`, `Unknown`) |
| `:core:domain` | JVM | `MealRepository`, `FavoriteRepository`, use cases, `AnalyticsTracker`/`AnalyticsEvent`, `ConnectivityObserver`; also publishes `testFixtures` (domain fakes, `meal()`, `MainDispatcherRule`, `assertFails`) |
| `:core:common` | Android + Hilt | `@IoDispatcher` / `@DefaultDispatcher` qualifiers and `DispatcherModule` |
| `:core:network` | Android + Hilt + KSP | Retrofit `TheMealDBAPI`, DTOs (Moshi codegen), `MealRemoteDataSource`, `ApiModule`, the API key `BuildConfig`, `INTERNET` permission |
| `:core:database` | Android + Hilt + Room | `TemperoDatabase`, DAO, entity, converters, `MealLocalDataSource`, `DatabaseModule`, schema in `core/database/schemas/` |
| `:core:data` | Android + Hilt | `MealRepositoryImpl`, `FavoriteRepositoryImpl`, mappers (`toDomain()`/`toEntity()`), `MealStepsParser`, `AndroidConnectivityObserver`, repository/data source/connectivity Hilt modules |
| `:core:analytics` | Android + Hilt | `FirebaseAnalyticsTracker` and `AnalyticsModule` |
| `:core:designsystem` | Android + Compose | `TemperoTheme`, Color/Shape/Type, fonts (`preloaded_fonts` meta-data), shared icons |
| `:core:ui` | Android + Compose | Shared composables (`FavoriteButton`, `MealListItem`, `OfflineBanner`, `StateMessage`), `PreviewData`, shared strings (`common_*`, ...) |
| `:feature:*` | Android + Compose + Hilt | One screen each: entry, screen, UiState, Intent, Event, ViewModel, components, own strings and **navigation** (route + `navigateToXxx` / `xxxScreen` extensions) |
| `:core:testing` | Android | Fakes that need network/database types (`FakeMealRemoteDataSource`, `FakeMealLocalDataSource`, DTO builders); re-exports the domain test fixtures |
| `:app` | Application | `TemperoApplication`, `MainActivity`, `TemperoNavHost`/`TemperoCoordinator`/bottom bar, Firebase plugins, signing, `BuildConfig.ANALYTICS_IN_DEBUG` |

Build configuration is shared through convention plugins in `build-logic/` (included build): `tempero.android.application`, `tempero.android.library`, `tempero.android.compose`, `tempero.android.feature`, `tempero.android.room`, `tempero.hilt`, `tempero.jvm.library` and `tempero.detekt`. Apply them via `alias(libs.plugins.tempero.*)`; a new feature module is just `alias(libs.plugins.tempero.android.feature)` plus a `namespace`, and an `include(...)` in `settings.gradle.kts`.

## Architecture

Clean-architecture layering with Hilt DI, MVI-style presentation:

- **Domain** (`:core:model`, `:core:domain`) — plain Kotlin. Callers (ViewModels) catch `DataError`, not raw exceptions. The codebase uses `jakarta.inject.Inject`/`Singleton`.
- **Data** (`:core:network`, `:core:database`, `:core:data`) — `MealRepositoryImpl` → `MealRemoteDataSource` (interface + impl) → Retrofit `TheMealDBAPI` (Moshi with KSP codegen); `FavoriteRepositoryImpl` → `MealLocalDataSource` → Room DAO. DTOs/entities are converted to domain models via extension functions in `core/data/.../remote/mapper/` and `.../local/mapper/`. Every repository method is wrapped in `safeApiCall`, which maps Moshi/IO/HTTP exceptions to `DataError` and rethrows `CancellationException`.
- **DI** — Hilt modules live next to what they provide. Inject `@IoDispatcher` / `@DefaultDispatcher` (from `:core:common`) instead of using `Dispatchers.*` directly (network on IO, list sorting/transformation on Default).
- **Presentation** — `MainActivity` (`@AndroidEntryPoint`) hosts `TemperoTheme` and `TemperoNavHost`. Each feature follows this pattern:
  - `XxxEntry` — obtains the `@HiltViewModel` via `hiltViewModel()`, collects state, and passes plain values/lambdas to the stateless `XxxScreen`.
  - `XxxUiState` — sealed interface (`Loading` / `Error(DataError)` / `Success(...)`).
  - `XxxIntent` — sealed user actions, dispatched through a single `viewModel.onHandleIntent(intent)`.
  - `XxxEvent` — semantic navigation events emitted to the app; `TemperoCoordinator` (in `:app`) is the only place that decides where to go, using each feature's `navigation/XxxRoute.kt` (`@Serializable` route + `NavController.navigateToXxx` + `NavGraphBuilder.xxxScreen`).
  - String resources shared by several features live in `:core:ui` and are referenced with an alias import (`import ...core.ui.R as UiR`), since `android.nonTransitiveRClass=true`.

### Explore screen data flow

TheMealDB has no paginated "list all meals" endpoint, so `ExploreViewModel` loads everything up front: it fetches categories, then `getAllMeals(categories)` calls `filter.php?c=<category>` for every category in parallel, flattens and sorts by name. The full list is held in memory, and paging is done locally by `MealPagingSource` (in `:feature:explore`, package `...explore.paging`), which slices the in-memory list and filters by the selected category. A new `Pager` is created via `flatMapLatest` whenever the meal list or the selected category changes, and `.cachedIn(viewModelScope)`.

Debug builds enable `StrictMode` (thread + VM policies, log penalty) in `TemperoApplication`, and OkHttp logs full bodies only in debug.

## CI

Um único workflow, `.github/workflows/ci.yml` (GitHub Actions, Temurin 17), roda em PRs e pushes para `develop` e `main`: `./gradlew detekt lintDebug testDebugUnitTest assembleDebug` (todos os módulos). Fluxo de branches informal (Gitflow): `feat/*` → `develop` → `main`.

- Gradle 8.13 não roda com JDK 25+; localmente use JDK 17/21 (ex.: `JAVA_HOME` apontando para o `jbr-21` do Android Studio).
- Configs: `config/detekt/detekt.yml`, `.editorconfig`; cada módulo tem o próprio `detekt-baseline.xml` e `lint-baseline.xml` (regenerar com `./gradlew detektBaseline` / `./gradlew updateLintBaseline`). O lint cria o baseline e falha uma vez se o arquivo não existir — ao criar um módulo, rode `lintDebug` duas vezes e versione o arquivo gerado.
- Bibliotecas devem declarar no próprio manifest as permissões que suas APIs exigem (ex.: `INTERNET` em `:core:network`, `ACCESS_NETWORK_STATE` em `:core:data`), senão o lint (`MissingPermission`) falha.
- `app/build.gradle.kts` já aceita assinatura de release via env vars `TEMPERO_KEYSTORE_PATH`, `TEMPERO_KEYSTORE_PASSWORD`, `TEMPERO_KEY_ALIAS`, `TEMPERO_KEY_PASSWORD` e versão via `-Ptempero.versionName` / `-Ptempero.versionCode`, para um futuro workflow de release (sem elas, `assembleRelease` gera APK não assinado).
