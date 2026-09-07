package edu.psu.se411;

import java.math.BigDecimal;
import edu.psu.se411.exceptions.InvalidAgeException;
import edu.psu.se411.exceptions.InsufficientFundsException;
import edu.psu.se411.model.BankAccount;
import edu.psu.se411.model.Wallet;

public class App {
    public static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age " + age + " is invalid; minimum age is 18");
        }
        System.out.println("Age valid message.");
    }

    public static void main(String[] args) {
        System.out.println("Exercise 1: Age validation");
        for (int age : new int[] {17, 18, 25}) {
            System.out.println("Checking age " + age);
            try {
                validateAge(age);
            } catch (InvalidAgeException exception) {
                System.out.println("Invalid age: " + exception.getMessage());
            }
        }

        System.out.println("\nExercise 2: Online wallet");
        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        BankAccount bankAccount = new BankAccount();
        for (String value : new String[] {"30.00", "80.00", "70.00"}) {
            BigDecimal amount = new BigDecimal(value);
            try {
                wallet.withdraw(amount, bankAccount);
                System.out.println("Transferred " + amount + " to bank account");
            } catch (InsufficientFundsException exception) {
                System.out.println("Withdrawal rejected: " + exception.getMessage());
            }
            System.out.println("Wallet balance: " + wallet.getBalance()
                    + "; bank balance: " + bankAccount.getBalance());
        }
    }
}
