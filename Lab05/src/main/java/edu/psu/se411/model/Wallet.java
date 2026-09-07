package edu.psu.se411.model;

import java.math.BigDecimal;
import java.util.Objects;
import edu.psu.se411.exceptions.InsufficientFundsException;

/** A single-threaded wallet simulation using exact decimal amounts. */
public class Wallet {
    private BigDecimal balance;

    public Wallet(BigDecimal openingBalance) {
        Objects.requireNonNull(openingBalance, "Opening balance must not be null");
        if (openingBalance.signum() < 0) {
            throw new IllegalArgumentException("Opening balance must not be negative");
        }
        balance = openingBalance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void withdraw(BigDecimal amount, BankAccount bankAccount)
            throws InsufficientFundsException {
        Objects.requireNonNull(amount, "Amount must not be null");
        Objects.requireNonNull(bankAccount, "Bank account must not be null");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException(
                    "Cannot withdraw " + amount + "; wallet balance is " + balance);
        }
        bankAccount.deposit(amount);
        balance = balance.subtract(amount);
    }
}
