# Lab 05: Exception handling

This Maven project completes the two programming exercises in the Lab 05
handout: age validation and a simulated online wallet transfer.

## Run

Requires JDK 21 with `JAVA_HOME` set. From `Lab05` in PowerShell:

```powershell
.\mvnw.cmd clean package exec:java
```

On macOS/Linux: `sh mvnw clean package exec:java`. With Maven installed:
`mvn clean package exec:java`. The wrapper downloads Maven 3.9.9 on first use;
the initial build requires internet access for dependencies.

The POM includes the handout's `exec-maven-plugin` version `3.5.0`, configured
to run `edu.psu.se411.App`.

## Exercise 1

`App.validateAge(int)` declares and throws the checked `InvalidAgeException`
for any age below 18. It prints `Age valid message.` for ages 18 and above.
The main method catches the exception and demonstrates ages 17, 18, and 25.
Both custom exception classes are in the dedicated `edu.psu.se411.exceptions`
package, which contains only exception classes.

## Exercise 2

`Wallet.withdraw(BigDecimal, BankAccount)` transfers an amount to a simulated
bank account. If the amount exceeds the wallet balance, it throws the checked
`InsufficientFundsException`, and neither balance changes. `App.main` catches
that exception and displays its message, then continues with the next transfer.

Amounts use `BigDecimal` constructed from strings to preserve exact decimal
values. Zero/negative transfers and negative opening balances are rejected with
`IllegalArgumentException`; null arguments are rejected with
`NullPointerException`. A zero opening balance is allowed. The simulation is
single-threaded, keeps balances in memory, and assumes a common currency.

## Verified results

Executed on 2026-09-07 using Microsoft OpenJDK 21 and Maven 3.9.9:

```text
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The main program produced:

```text
Exercise 1: Age validation
Checking age 17
Invalid age: Age 17 is invalid; minimum age is 18
Checking age 18
Age valid message.
Checking age 25
Age valid message.

Exercise 2: Online wallet
Transferred 30.00 to bank account
Wallet balance: 70.00; bank balance: 30.00
Withdrawal rejected: Cannot withdraw 80.00; wallet balance is 70.00
Wallet balance: 70.00; bank balance: 30.00
Transferred 70.00 to bank account
Wallet balance: 0.00; bank balance: 100.00
```

Run just the tests with `.\mvnw.cmd test`. JUnit reports are generated in
`target/surefire-reports/`.

Tests cover the age-18 boundary, extreme ages, transfers to an account with
an existing balance, exact-balance withdrawal with different decimal scales,
insufficient funds and retry, an empty wallet, exact decimal arithmetic, and
invalid inputs without unwanted balance changes.

## Exception handling review

The implementation uses specific checked exceptions for the two lab scenarios.
Exceptions retain meaningful messages via `super(message)`. The main method
catches the expected exception types and reports failures without terminating
the demonstration. Validation occurs before balance mutation. Tests verify
that rejected requests preserve state and a later valid request still works.

The exception handling review was completed with Codex. GitHub Copilot was
unavailable, and the user waived that tool-specific step on 2026-09-07.
