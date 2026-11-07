# Tempero

[![CI](https://github.com/thiago-fullenbach/tempero/actions/workflows/ci.yml/badge.svg)](https://github.com/thiago-fullenbach/tempero/actions/workflows/ci.yml)

App Android para explorar receitas do [TheMealDB](https://www.themealdb.com/api.php), feito em **Kotlin** e **Jetpack Compose**, com arquitetura limpa, apresentação no estilo MVI e organização em vários módulos.

## Funcionalidades

- **Explorar**: todas as receitas do TheMealDB numa lista paginada, com filtro por categoria.
- **Buscar**: busca por nome, com sugestões e contagem de resultados.
- **Detalhes**: ingredientes (com marcação de progresso) e modo de preparo passo a passo.
- **Favoritos**: receitas salvas localmente (Room), disponíveis mesmo sem internet, com opção de desfazer a remoção.
- **Modo offline**: aviso quando o aparelho perde a conexão.

## Stack

| Área | Tecnologias |
|---|---|
| UI | Jetpack Compose, Material 3, Navigation Compose (rotas tipadas com `kotlinx.serialization`), Coil, Paging 3 |
| DI | Hilt |
| Rede | Retrofit, OkHttp, Moshi (codegen com KSP) |
| Persistência | Room |
| Assíncrono | Kotlin Coroutines e Flow |
| Observabilidade | Firebase Analytics e Crashlytics, Timber |
| Testes | JUnit 4, Turbine, Robolectric, fakes compartilhados (`:core:testing` e `testFixtures` de `:core:domain`) |
| Qualidade | Detekt, Android Lint, GitHub Actions |

minSdk 28 · targetSdk/compileSdk 36 · JVM 11. As versões ficam no catálogo [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Como rodar

### Pré-requisitos

- Android Studio recente (ou Android SDK com API 36).
- **JDK 17 ou 21**: o Gradle 8.13 não roda com JDK 25+. Se preciso, aponte o `JAVA_HOME` para o `jbr` do Android Studio.

### Configuração (opcional)

Em `local.properties`, na raiz do projeto:

```properties
# Chave da API do TheMealDB (padrão: chave pública de teste "1").
# Também pode vir da variável de ambiente MEALDB_API_KEY.
MEALDB_API_KEY=sua_chave

# Ativa o Firebase Analytics em builds debug (desativado por padrão).
ANALYTICS_IN_DEBUG=true
```

### Comandos

No Windows, use `.\gradlew.bat` no lugar de `./gradlew`.

```bash
./gradlew assembleDebug                 # gera o APK debug
./gradlew installDebug                  # instala em um aparelho/emulador
./gradlew testDebugUnitTest             # testes unitários de todos os módulos
./gradlew :feature:explore:testDebugUnitTest   # testes de um módulo
./gradlew connectedDebugAndroidTest     # testes instrumentados (precisa de aparelho)
./gradlew detekt lintDebug              # análise estática
```

## Arquitetura

O projeto segue camadas de clean architecture (domínio, dados e apresentação), com Hilt para injeção de dependências.

- **Domínio** (`:core:model`, `:core:domain`): Kotlin puro, com modelos, interfaces de repositório e casos de uso. Os erros chegam como a hierarquia selada `DataError` (`Network`, `Unavailable`, `InvalidResponse`, `Storage`, `Unknown`).
- **Dados** (`:core:network`, `:core:database`, `:core:data`): implementações dos repositórios sobre Retrofit e Room. Cada chamada passa por `safeApiCall`, que converte exceções em `DataError`.
- **Apresentação** (`:feature:*`): cada tela tem `Entry` (obtém o ViewModel), `Screen` stateless, `UiState` selado (`Loading` / `Error` / `Success`), `Intent` (ações do usuário, tratadas por `onHandleIntent`) e `Event` (eventos de navegação). O `TemperoCoordinator`, em `:app`, é o único lugar que decide para onde navegar.

### Módulos

```
:app ──► :feature:{explore,search,favorites,detail} ──► :core:ui ──► :core:designsystem
  │                          └───────────────────────► :core:domain ──► :core:model
  ├──► :core:data ──► :core:network | :core:database | :core:common  (e :core:domain)
  └──► :core:analytics ──► :core:domain
:core:testing (só testes) ──► fixtures do domínio, :core:network, :core:database
```

| Módulo | Responsabilidade |
|---|---|
| `:app` | `Application`, `MainActivity`, navegação (`TemperoNavHost`, `TemperoCoordinator`, barra inferior), Firebase e assinatura |
| `:feature:explore` | Lista de receitas com filtro por categoria e paginação local |
| `:feature:search` | Busca por nome |
| `:feature:detail` | Detalhes da receita |
| `:feature:favorites` | Receitas favoritas |
| `:core:model` | Modelos de domínio e `DataError` |
| `:core:domain` | Repositórios (interfaces), casos de uso, analytics e conectividade |
| `:core:common` | Qualificadores de dispatcher (`@IoDispatcher`, `@DefaultDispatcher`) |
| `:core:network` | API Retrofit, DTOs e data source remoto |
| `:core:database` | Banco Room, DAO, entidades e data source local |
| `:core:data` | Implementações dos repositórios, mappers e observador de conectividade |
| `:core:analytics` | Implementação do tracker com Firebase |
| `:core:designsystem` | Tema, cores, tipografia e ícones |
| `:core:ui` | Componentes e strings compartilhados entre as features |
| `:core:testing` | Fakes e builders para testes |

**Regras de dependência:** uma feature nunca depende de outra feature e só enxerga `core:ui`, `core:designsystem`, `core:domain` e `core:model`. Só o `:app` conhece as implementações e as liga pelo Hilt.

A configuração de build é compartilhada por convention plugins em [`build-logic/`](build-logic/) (`tempero.android.feature`, `tempero.android.library`, `tempero.hilt`, `tempero.android.room` etc.). Um módulo de feature novo precisa só de `alias(libs.plugins.tempero.android.feature)`, um `namespace` e um `include(...)` em `settings.gradle.kts`.

### Por que a paginação é local

O TheMealDB não tem um endpoint paginado que liste todas as receitas. Por isso, a tela Explorar busca as categorias e chama `filter.php?c=<categoria>` para todas elas em paralelo, junta e ordena o resultado. A lista completa fica em memória, e o `MealPagingSource` fatia essa lista (já filtrada pela categoria escolhida) para o Paging 3.

## CI

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) roda em PRs e pushes para `develop` e `main`:

```bash
./gradlew detekt lintDebug testDebugUnitTest assembleDebug
```

Cada módulo tem o próprio `detekt-baseline.xml` e `lint-baseline.xml`. Os branches seguem um Gitflow informal: `feat/*` → `develop` → `main`.
