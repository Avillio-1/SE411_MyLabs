package edu.spu.se411.lab06_logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.spu.se411.lab06_logging.exceptions.InsufficientFundsException;
import edu.spu.se411.lab06_logging.model.WalletAccount;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Application is starting...");
        try {
            WalletAccount account = new WalletAccount(1000);
            try {
                account.deposit(200);
                account.withdraw(200);
                account.withdraw(1500);
            } catch (InsufficientFundsException e) {
                // Log each failure with its stack trace once, where it is handled.
                logger.error("Withdrawal failed: insufficient funds", e);
            }

            try {
                account.deposit(-100);
            } catch (IllegalArgumentException e) {
                logger.error("Deposit failed: invalid amount", e);
            }
        } finally {
            logger.info("Application is ending...");
        }
    }
}
