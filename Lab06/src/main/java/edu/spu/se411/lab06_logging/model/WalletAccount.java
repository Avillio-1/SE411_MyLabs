package edu.spu.se411.lab06_logging.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.spu.se411.lab06_logging.exceptions.InsufficientFundsException;

public class WalletAccount {
    private static final Logger logger = LoggerFactory.getLogger(WalletAccount.class);
    private double balance;

    public WalletAccount(double balance) {
        setBalance(balance);
        logger.debug("Wallet account created");
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        } else if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        } else {
            balance -= amount;
            logger.debug("Withdrawal successful: amount={}", amount);
        }
    }

    public void deposit(double amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Deposit amount cannot be negative");
        } else {
            balance += amount;
            logger.debug("Deposit successful: amount={}", amount);
        }
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }
}
