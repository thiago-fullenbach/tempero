# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Tempero is a single-module (`:app`) Android app in Kotlin + Jetpack Compose that browses recipes from [TheMealDB](https://www.themealdb.com/api.php). Package: `br.com.thiago.tempero`. minSdk 28, targetSdk/compileSdk 36, JVM target 11. Code comments and KDoc are written in Portuguese.

## Commands

Run from the repo root (on Windows use `.\gradlew.bat` instead of `./gradlew`):

- Build debug APK: `./gradlew assembleDebug`
- Install on a device/emulator: `./gradlew installDebug`
- Unit tests: `./gradlew testDebugUnitTest`
- Single unit test: `./gradlew testDebugUnitTest --tests "br.com.thiago.tempero.ExampleUnitTest.addition_isCorrect"`
- Instrumented tests (device required): `./gradlew connectedDebugAndroidTest`
- Android lint: `./gradlew lint`

Dependencies and plugin versions live in the version catalog `gradle/libs.versions.toml`; reference them as `libs.*` in `app/build.gradle.kts`.

## Configuration

The TheMealDB API key is read from `local.properties` (`MEALDB_API_KEY=...`) into `BuildConfig.MEALDB_API_KEY`, defaulting to the public test key `"1"`. The key is embedded in the base URL path (`THE_MEAL_DB_API_BASE_URL` in `data/remote/api/TheMealDBAPI.kt`).

## Architecture

Clean-architecture layering with Hilt DI, MVI-style presentation:

- **domain/** — plain Kotlin: models (`Meal`, `MealCategory`), the `MealRepository` interface, and the `DataError` sealed exception hierarchy (`Network`, `Unavailable`, `InvalidResponse`, `Unknown`).
- **data/** — `MealRepositoryImpl` → `MealRemoteDataSource` (interface + impl) → Retrofit `TheMealDBAPI` (Moshi with KSP codegen). DTOs are converted to domain models via `toDomain()` extension functions in `data/remote/mapper/`. Every repository method is wrapped in `safeApiCall`, which maps Moshi/IO/HTTP exceptions to `DataError` and rethrows `CancellationException`. Callers (ViewModels) catch `DataError`, not raw exceptions.
- **di/** — Hilt modules. `DispatcherModule` provides `@IoDispatcher` / `@DefaultDispatcher` qualifiers; inject these instead of using `Dispatchers.*` directly (network on IO, list sorting/transformation on Default). Note the codebase uses `jakarta.inject.Inject`/`Singleton`.
- **presentation/** — `MainActivity` (`@AndroidEntryPoint`) hosts `TemperoTheme` (in `presentation/constants/`, along with Color/Shape/Type). Each screen follows this pattern:
  - `XxxRoute` — obtains the `@HiltViewModel` via `hiltViewModel()`, collects state, and passes plain values/lambdas to the stateless `XxxScreen`.
  - `XxxUiState` — sealed interface (`Loading` / `Error(DataError)` / `Success(...)`).
  - `XxxIntent` — sealed user actions, dispatched through a single `viewModel.onHandleIntent(intent)`.
  - ViewModels live in `presentation/viewmodel/`; screen-specific composables in `screen/<name>/components/`; shared composables in `presentation/components/`; preview fixtures in `presentation/preview/PreviewData.kt`.

### Explore screen data flow

TheMealDB has no paginated "list all meals" endpoint, so `ExploreViewModel` loads everything up front: it fetches categories, then `getAllMeals(categories)` calls `filter.php?c=<category>` for every category in parallel, flattens and sorts by name. The full list is held in memory, and paging is done locally by `MealPagingSource` (in `presentation/paging/`), which slices the in-memory list and filters by the selected category. A new `Pager` is created via `flatMapLatest` whenever the meal list or the selected category changes, and `.cachedIn(viewModelScope)`.

Debug builds enable `StrictMode` (thread + VM policies, log penalty) in `TemperoApplication`, and OkHttp logs full bodies only in debug.
