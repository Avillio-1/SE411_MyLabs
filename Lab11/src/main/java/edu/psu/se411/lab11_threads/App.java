package edu.psu.se411.lab11_threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab11_threads.model.CryptoAccount;
import edu.psu.se411.lab11_threads.model.Transaction;
import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;

/**
 * Build and run using Maven goal: clean package exec:java
 */
public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static Integer nbAccounts = 10;
	public static Double initialBalance = 1000.0;
	public static Integer nbTransactions = 10000;

	public static void main(String[] args) {
		logger.info("Application is starting...");
		try {
			runTransactions(false);
			runTransactions(true);
		} catch (InterruptedException e) {
			logger.error("Interrupted while waiting for transactions", e);
			Thread.currentThread().interrupt();
		} finally {
			logger.info("Application is closing...");
		}
	}

	private static void runTransactions(boolean useSynchronization) throws InterruptedException {
		CryptoAccount[] accounts = new CryptoAccount[nbAccounts];
		for (Integer i = 0; i < nbAccounts; i++) {
			accounts[i] = new CryptoAccount("acc_" + i.toString(), initialBalance);
		}

		Thread[] threads = new Thread[nbTransactions];
		for (int i = 0; i < nbTransactions; i++) {
			threads[i] = new Thread(() -> {
				CryptoAccount randomA = accounts[(int) (Math.random() * nbAccounts)];
				CryptoAccount randomB = accounts[(int) (Math.random() * nbAccounts)];
				Double randomAmount = Math.random() * 10 + 1;
				Transaction t = new Transaction(randomA, randomB, randomAmount);
				try {
					if (useSynchronization) {
						Transaction.executeSynchronized(t);
					} else {
						t.execute();
					}
				} catch (InsufficientBalanceException e) {
					logger.error("Transaction failed", e);
				}
			});
		}
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			thread.join();
		}
		System.out.printf("%s total: %.2f riyals%n",
				useSynchronization ? "Synchronized" : "Unsynchronized", calculateTotal(accounts));
	}

	public static double calculateTotal(CryptoAccount[] accounts) {
		double total = 0;
		for (CryptoAccount account : accounts) {
			total += account.getBalance();
		}
		return total;
	}

}
