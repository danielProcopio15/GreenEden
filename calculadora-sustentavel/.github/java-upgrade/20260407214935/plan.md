# Upgrade Plan: calculadora-sustentavel (20260407214935)

- **Generated**: 2026-04-07 21:49:35
- **HEAD Branch**: fEAT/ajustandoHTML
- **HEAD Commit ID**: 60314d3d955850d7d978d016d222e734f2eeecad

## Available Tools

**JDKs**
- JDK 17.0.12: `C:\Program Files\Java\jdk-17\bin` (current project JDK, used by step 2)
- JDK 25: **<TO_BE_INSTALLED>** (target LTS version, required by steps 3-4)

**Build Tools**
- Maven Wrapper: 3.9.12 → **<TO_BE_UPGRADED>** to 4.0.x (Maven 3.9.x does not support Java 25; update `distributionUrl` in `.mvn/wrapper/maven-wrapper.properties`)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred. <!-- this note is for users, NEVER remove it -->

## Options

- Working branch: appmod/java-upgrade-20260407214935
- Run tests before and after the upgrade: true <!-- user specified, NEVER remove it -->

## Upgrade Goals

- Upgrade Java from 17 to 25 (latest LTS as of April 2026)

### Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --------------------- | ------- | -------------- | ---------------- |
| Java | 17 | 25 | User requested — upgrade to latest LTS |
| Spring Boot | 4.0.3 | 4.0.3 | Already compatible with Java 25; no change needed |
| Lombok | managed by Spring Boot BOM | managed | Already compatible; BOM-managed |
| Maven Wrapper | 3.9.12 | 4.0.0 | Maven 3.9.x does not support Java 25 as compiler target |
| maven-compiler-plugin | managed by Spring Boot BOM | managed | Spring Boot 4.x BOM provides a compatible version |

### Derived Upgrades

- Upgrade Maven Wrapper from 3.9.12 to 4.0.x (Java 25 requires Maven 4.0+ per build tool compatibility rules; update `distributionUrl` in `.mvn/wrapper/maven-wrapper.properties`)
- Set `<java.version>` to 25 in `pom.xml` (drives compiler source/target/release settings via Spring Boot parent BOM)

## Upgrade Steps

- **Step 1: Setup Environment**
  - **Rationale**: Install JDK 25 (the target Java LTS version), which is not currently installed on the system.
  - **Changes to Make**:
    - [ ] Install JDK 25 via `#appmod-install-jdk`
    - [ ] Verify JDK 25 is available after installation
  - **Verification**:
    - Command: `#appmod-list-jdks` to confirm JDK 25 installation
    - Expected: JDK 25 available at a valid path on this system

---

- **Step 2: Setup Baseline**
  - **Rationale**: Establish pre-upgrade compile and test results using Java 17 to form acceptance criteria for the upgrade.
  - **Changes to Make**:
    - [ ] Run baseline compilation with Java 17
    - [ ] Run baseline tests with Java 17
  - **Verification**:
    - Command: `.\mvnw.cmd clean test-compile -q` then `.\mvnw.cmd clean test`
    - JDK: `C:\Program Files\Java\jdk-17\bin`
    - Expected: Document SUCCESS/FAILURE and test pass count (forms acceptance criteria)

---

- **Step 3: Upgrade Java to 25 and Maven Wrapper to 4.0.x**
  - **Rationale**: Core upgrade — update `<java.version>` to 25 in `pom.xml` and upgrade Maven Wrapper to 4.0.x (required for Java 25 support). Both changes are applied together as they are tightly coupled.
  - **Changes to Make**:
    - [ ] Set `<java.version>25</java.version>` in `pom.xml`
    - [ ] Update `distributionUrl` in `.mvn/wrapper/maven-wrapper.properties` to Maven 4.0.x release URL
    - [ ] Fix any compilation errors caused by removed/deprecated APIs between Java 17 and 25
  - **Verification**:
    - Command: `.\mvnw.cmd clean test-compile -q`
    - JDK: JDK 25 (installed in step 1)
    - Expected: Compilation SUCCESS (tests may fail — will be addressed in Final Validation)

---

- **Step 4: Final Validation**
  - **Rationale**: Verify all upgrade goals are met: Java 25 compilation succeeds and test pass rate ≥ baseline (100% required).
  - **Changes to Make**:
    - [ ] Verify `<java.version>25</java.version>` in `pom.xml`
    - [ ] Resolve any TODOs or temporary workarounds from previous steps
    - [ ] Fix any remaining compilation or test failures (iterative fix loop until 100% pass)
  - **Verification**:
    - Command: `.\mvnw.cmd clean test`
    - JDK: JDK 25 (installed in step 1)
    - Expected: Compilation SUCCESS + 100% tests pass (≥ baseline from step 2)

- **Maven Wrapper Compatibility**
  - **Challenge**: Maven Wrapper 3.9.12 does not support Java 25 as a compiler target.
  - **Strategy**: Update `distributionUrl` in `.mvn/wrapper/maven-wrapper.properties` to Maven 4.0.x. One-line change, no code impact.

- **Java 17 → 25 API Changes**
  - **Challenge**: Eight major versions span between 17 and 25; newer releases may have removed preview features or deprecated APIs.
  - **Strategy**: The project source code is simple (Spring MVC controllers, a model class, and a service). No internal JDK APIs or complex reflection are used. Compile with Java 25 and fix any errors that arise.

## Plan Review

- Upgrade path is straightforward: Java 17 → 25 directly, no intermediate version needed.
- Source code uses only standard Spring annotations and plain Java — no APIs removed between Java 17 and 25 are expected to affect this codebase.
- No EOL dependencies detected.
- No javax→jakarta migration needed (Spring Boot 4.0.x already uses Jakarta EE namespace).
- Maven Wrapper upgrade is the only build tool change required.
- Limitations: None anticipated.
