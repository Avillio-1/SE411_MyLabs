# Lab 06: Logging with SLF4J

Completed from `lab 06 logging with slf4j.docx` and the supplied
`lab06_logging` starter project. The original package names are preserved,
including `edu.spu.se411.lab06_logging`.

## Run

Requires JDK 21 with `JAVA_HOME` set. From this folder in PowerShell:

```powershell
.\mvnw.cmd clean package exec:java
Get-Content .\logs\App\log4j\log.out
```

With Maven installed, use `mvn clean package exec:java`. On macOS/Linux,
use `sh mvnw clean package exec:java`. The wrapper downloads Maven 3.9.9
on first use; the initial build requires internet access for dependencies.

In Eclipse, import this folder as an existing Maven project, select JDK 21,
and use **Maven > Update Project** after changing the POM. Create a Maven
run configuration with this project as its base directory and goals
`clean package exec:java`.

The final configuration writes application logs to
`logs/App/log4j/log.out`, relative to the directory where Maven runs.
Application messages are in the file; Maven still prints build progress
to the console. Logs append across runs, and `clean` only clears `target`.

## Dependencies and provider exercise

The final POM contains `slf4j-api:2.0.16` and the handout's
`slf4j-log4j12:2.0.16`, plus JUnit for tests. Logback is removed from the
final project. The handout's `exec-maven-plugin:3.5.0` runs `App`.

Maven reports that `slf4j-log4j12:2.0.16` is relocated to
`slf4j-reload4j:2.0.16`. This is expected: SLF4J documents this
[Maven relocation](https://www.slf4j.org/manual.html). The requested
`log4j.properties` and `org.apache.log4j` appender classes work with it.

The following intermediate stages were built and executed separately on
2026-09-16, using a minimal `App` that logs the startup INFO message:

| Stage | Dependency/configuration | Observed result |
| --- | --- | --- |
| API only | `slf4j-api:2.0.16` | `No SLF4J providers were found.`; no application log output |
| Logback | API plus `ch.qos.logback:logback-classic:1.5.15`, as shown in the handout | Startup INFO message printed to the console |
| File logging, ERROR | API plus `slf4j-log4j12:2.0.16`; root level ERROR | Empty log file; startup INFO suppressed |
| File logging, INFO | Same provider; root level INFO | Startup INFO written to the log file with the specified pattern |

To repeat the provider experiment, use a separate copy of the project,
replace `App.main` with only the startup log, and change the dependencies
as shown above. Keep only one provider on the runtime classpath. The
final tests specifically use Reload4j to inspect events, so omit those
tests from the intermediate API-only and Logback copies.

## Required events

| Event | Level | Location |
| --- | --- | --- |
| Application starts and ends | INFO | `App.main`; end message in `finally` |
| Wallet account created | DEBUG | `WalletAccount` constructor, after validation |
| Successful deposit and withdrawal | DEBUG | `WalletAccount`, after balance mutation |
| `InsufficientFundsException` object created | WARN | Exception constructor |
| Thrown exception handled | ERROR | `App` catch blocks, including the exception stack trace |

The demo creates a wallet with 1000, deposits 200, withdraws 200, attempts
to withdraw 1500, and attempts to deposit -100. Both invalid operations
are caught and logged. The valid operations make the required DEBUG
events visible when DEBUG is enabled.

## Change the log level

Edit the first setting in `src/main/resources/log4j.properties`:

```properties
log4j.rootLogger=DEBUG, LOG1
```

Try ERROR, WARN, INFO, and DEBUG, running `clean package exec:java` after
each change so Maven copies the updated resource. The final setting is
INFO. Counts below describe one run, excluding stack-trace continuation
lines and any older appended output:

| Minimum level | DEBUG events | INFO events | WARN events | ERROR events | Total |
| --- | ---: | ---: | ---: | ---: | ---: |
| ERROR | 0 | 0 | 0 | 2 | 2 |
| WARN | 0 | 0 | 1 | 2 | 3 |
| INFO | 0 | 2 | 1 | 2 | 5 |
| DEBUG | 3 | 2 | 1 | 2 | 8 |

Unlike the earlier startup-only exercise, the completed app deliberately
causes failures, so ERROR now produces two events.

The handout's `FileAppender`, file path, and format are retained:

```properties
log4j.appender.LOG1.layout.conversionPattern=%d %p [%t] %C - %m%n
```

This includes timestamp, priority, thread, caller class, message, and a
newline. Exception stack traces follow the associated ERROR entry.

## AI evaluation and applied recommendations

Codex evaluated the logging strategy and applied these improvements:

| Finding | Applied improvement |
| --- | --- |
| `System.out.println` messages cannot be filtered by log level | Replaced wallet status and caught-exception printing with SLF4J; each class has a `private static final Logger` |
| Building log messages with concatenation adds unnecessary work | Used SLF4J `{}` placeholders for transaction amounts at DEBUG |
| Logging a success before validation can misrepresent a rejected operation | Success messages occur only after the balance changes |
| Logging only `e.getMessage()` loses diagnostic context | Passed the exception to `logger.error` to retain its type and stack trace |
| Logging and rethrowing at every layer repeats stack traces | Logged each failure's stack trace once at the application's catch boundary |
| The starter's exception and status messages expose balances | Removed balances from logged messages and exception text; transaction amounts appear only at DEBUG |
| The final lifecycle event could be skipped by an exception | Put the ending INFO message in `finally` |
| Platform-default log encoding is inconsistent | Explicitly configured UTF-8 |

The exception constructor keeps its WARN event because the handout
explicitly requires it. It records creation without duplicating the
ERROR stack trace. In a general application, constructing an exception
does not necessarily mean it was thrown; logging at the handling boundary
would usually suffice. Likewise, this lab requires ERROR for the two
handled failures, though expected validation failures may merit WARN in
another application.

The handout's file appender and `%C` pattern are retained for fidelity.
For a longer-running application, a rolling appender would bound disk
usage, and `%c` could identify the logger without caller-location lookup.
Those changes are recommendations beyond this exercise. Wallet arithmetic
and the starter's public API are otherwise preserved; this exercise focuses
on logging.

## Verification

Executed on 2026-09-16 with Microsoft OpenJDK 21 and Maven 3.9.9:

```text
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The tests cover lifecycle events, exception types and stack traces,
all four filtering thresholds, successful operations, and unchanged
balances after rejected operations. Separate executions verified actual
file output at ERROR, WARN, INFO, and DEBUG against the counts above,
as well as every intermediate provider stage.

Run only the tests with `.\mvnw.cmd test`. Reports are in
`target/surefire-reports/`. The DOCX is the instruction source and does
not require a filled-in Word response; the deliverables are this Java
project and the recorded logging evaluation.
