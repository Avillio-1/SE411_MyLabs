package edu.psu.se411.model;

import java.math.BigDecimal;
import java.util.Objects;

/** A simulated bank account; no connection to a real bank. */
public class BankAccount {
    private BigDecimal balance = BigDecimal.ZERO;

    public BigDecimal getBalance() {
        return balance;
    }

    public void deposit(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount must not be null");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance = balance.add(amount);
    }
}
