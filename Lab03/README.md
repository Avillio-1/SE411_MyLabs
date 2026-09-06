# Lab 03: JUnit introduction

The supplied `Stack<E>` implementation is unchanged. Its tests are in
`src/test/java/edu/psu/se411/model/StackTest.java`, in the same package as `Stack`.

## Run the tests

Requires JDK 21 (with `JAVA_HOME` set). The Maven wrapper downloads Maven 3.9.9
on its first run; dependencies also require internet access the first time.
From this directory in PowerShell:

```powershell
.\mvnw.cmd test
```

On macOS/Linux, use `sh mvnw test`. If Maven is already installed, `mvn test`
also works. The POM retains JUnit 5.11.0 and adds the Jupiter engine, an explicit
Java release, and compiler/Surefire plugins so Maven discovers and runs the tests.
See the [official Surefire JUnit Platform documentation](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html).

## Test results

Verified on 2026-09-06 using Microsoft OpenJDK 21 and Maven 3.9.9:

```text
Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

There are 12 test methods, including one parameterized test with five capacities.
Surefire reports are generated under `target/surefire-reports/`.

The handout's intentional failure was also executed: after pushing `"Z"` and
`"A"`, the first test temporarily expected `"Z"`. Maven reported:

```text
StackTest.pushPushPopReturnsLatestElement:21 expected: <Z> but was: <A>
Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
BUILD FAILURE
```

The assertion was restored to `"A"`, then the complete suite passed again.
To repeat the demonstration, change only the assertion in
`pushPushPopReturnsLatestElement` and run:

```powershell
.\mvnw.cmd '-Dtest=StackTest#pushPushPopReturnsLatestElement' test
```

Restore the assertion afterward so the final submission stays green.

## Coverage review

| Public API / behavior | Test coverage |
| --- | --- |
| `Stack()` | Empty initial state, normal push/pop, growth past default capacity 10 |
| `Stack(int)`, positive branch | Capacities 1 and 10, initially empty, growth beyond capacity |
| `Stack(int)`, fallback branch | Capacities 0, -1, and `Integer.MIN_VALUE`; remains usable and growable |
| `push(E)` | Strings, null, empty string, duplicates, empty collection reference, integer minimum/maximum/zero/negative values |
| `pop()`, nonempty branch | Latest element, full reverse order, single-element-to-empty boundary |
| `pop()`, empty branch | Exact `NoSuchElementException` type and message; fresh and drained stacks |
| State across calls | Interleaved push/pop, reuse after repeated exceptions, independent stack instances |

The exception message in the starter code is **`Stack is empty, cannot pop`**.
The handout's example uses different punctuation and wording. The tests assert
the starter code's exact message rather than changing the implementation.

To reproduce the JaCoCo measurement:

```powershell
.\mvnw.cmd clean org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.12:report
```

Open `target/site/jacoco/index.html`. The measured result for **Stack only** is:

| Metric | Covered | Missed |
| --- | ---: | ---: |
| Methods (including constructors) | 4 | 0 |
| Branch outcomes | 4 | 0 |
| Lines | 11 | 0 |
| Instructions | 43 | 0 |

No `Stack` public method or source branch is uncovered. This is structural
coverage, not proof that every possible input has been tested.

Intentionally omitted cases:

- `new Stack<>(Integer.MAX_VALUE)` requests an enormous backing array. It tests
  JVM memory limits and may fail with `OutOfMemoryError`; ordinary positive
  capacities exercise the same source branch safely. `Integer.MAX_VALUE` is
  covered as a pushed element.
- The starter `App.main` is an empty placeholder, and its implicit constructor
  has no lab behavior to verify. JaCoCo therefore reports those two methods as
  uncovered outside the `Stack` class.
- ArrayList internals and private implementation details are not tested directly.

## AI assistance and IDE workflow

The additional edge-case tests were generated and reviewed with Codex. The
Eclipse-specific steps from the handout (importing the Maven project, installing
GitHub Copilot, and using Copilot agent mode) were not performed in this workspace.
Those remain separate GUI steps if the instructor requires the named IDE/tool.
