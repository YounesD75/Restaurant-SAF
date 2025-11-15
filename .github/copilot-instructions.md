## Repository quick notes for code-generating agents

This repository contains multiple small Java/Maven services and libraries used for a restaurant/S.A.F. project. Keep guidance concise and always reference concrete files.

- Primary modules to inspect:
  - `InventoryService/` — Spring Boot microservice (Java 21). Key files:
    - `InventoryService/pom.xml` (Spring Boot 3.5.7, Java 21, Flyway + PostgreSQL)
    - `InventoryService/src/main/resources/application.yml` (datasource + Flyway + server.port)
    - `InventoryService/src/main/resources/db/migration/` (Flyway SQL migrations, e.g. `V1__init_inventory_tables.sql`)
    - Java packages under `InventoryService/src/main/java/com/InventoryService/InventoryService/` (controllers, service, repo, entity, dto)
  - `saf-core1/` — small library / sample actor code (Java 17). Key files:
    - `saf-core1/pom.xml` (Java 17)
    - `saf-core1/src/test/java/com/saf/core1/` contains actor tests and examples to follow.

## Build & test rules (concrete commands)
- Use the repository-local Maven wrapper inside each module. Do not assume a global mvn: prefer the wrapper shipped in the module.
  - Build InventoryService: `cd InventoryService && ./mvnw clean package`
  - Run tests in a module: `cd <module> && ./mvnw test`

- Java compatibility: InventoryService targets Java 21 (see its `pom.xml`), while `saf-core1` uses Java 17. Ensure the correct JDK when compiling each module.

## Runtime / integration notes
- InventoryService expects PostgreSQL and relies on Flyway migrations packed under `src/main/resources/db/migration`.
  - application YAML contains default localhost credentials (for development): `jdbc:postgresql://localhost:5432/inventory_db`.
  - Hibernate `ddl-auto` is set to `validate` — do not modify generated entity mappings without updating DB schema/migrations.

## Coding conventions and gotchas for generated code
- Package naming / casing: existing packages use `com.InventoryService.InventoryService` (note the capitalization). Match exact package names when adding new classes to avoid classpath/package mismatches.
- Lombok is used (annotation processing configured in `pom.xml`). When generating DTOs/entities, maintain Lombok annotations (e.g., `@Data`, `@Builder`) to stay consistent with current code.
- When changing data model (entities/columns), also update Flyway SQL in `InventoryService/src/main/resources/db/migration` and keep migrations forward-only.

## Where to add new components
- Controllers: `InventoryService/src/main/java/.../controller`
- Services: `.../service` and implementations under `.../service/impl`
- Repositories: `.../repository` (Spring Data JPA interfaces)
- DTOs and Entities: use the existing `dto/` and `entity/` packages and preserve Lombok patterns.

## Tests and examples to follow
- Look at `saf-core1/src/test/java/com/saf/core1` for example unit-test structure and style.
- InventoryService includes tests under `src/test/java` — follow their Spring Boot testing pattern when adding new integration tests.

## Safety and scope
- Only modify files within the appropriate module (`InventoryService` vs `saf-core1`). Do not attempt to unify Java versions or global POM changes without a developer’s request.

## Quick reference (useful paths)
- `InventoryService/pom.xml` — dependency & Java version
- `InventoryService/src/main/resources/application.yml` — DB and server config
- `InventoryService/src/main/resources/db/migration/` — Flyway migration SQL
- `InventoryService/src/main/java/com/InventoryService/InventoryService/` — primary code packages
- `saf-core1/pom.xml` and `saf-core1/src/test/java/com/saf/core1/` — actor examples/tests

If something in these files is unclear or you want more specific rules (e.g. preferred annotation styles, logging format, or code-generation examples), tell me which area to expand and I will iterate.
