package edu.psu.se411.lab12_threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.psu.se411.lab12_threads.model.CryptoAccount;
import edu.psu.se411.lab12_threads.model.Transaction;

/**
 * Build and run using Maven goal: clean package exec:java
 */
public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static Integer nbAccounts = 10;
	public static Double initialBalance = 10000.0;
	public static Integer nbTransactions = 1000000;

	public static void main(String[] args) {
		run(args, false);
	}

	static void run(String[] args, boolean cached) {
		if (args.length > 0) nbAccounts = Integer.parseInt(args[0]);
		if (nbAccounts < 2) throw new IllegalArgumentException("At least two accounts are required");
		logger.info("Application is starting...");

		// 1- Create accounts
		CryptoAccount[] accounts = new CryptoAccount[nbAccounts];
		for (Integer i = 0; i < nbAccounts; i++) {
			accounts[i] = new CryptoAccount("acc_" + i.toString(), initialBalance);
		}

		System.out.printf("Initial money volume in system: %f%n%n", sumTotalMoneyInAccounts(accounts));

		int workers = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = cached ? Executors.newCachedThreadPool()
				: Executors.newFixedThreadPool(workers);
		System.out.printf("Pool: %s; accounts: %d; transactions: %d; processors: %d%n",
				cached ? "cached" : "fixed", nbAccounts, nbTransactions, workers);
		Random random = new Random(411);

		long startTime = System.nanoTime();


		// 2- choose sender and receiver accounts randomly
		for(int i = 0; i < nbTransactions; i++) {
			int sender_index = random.nextInt(nbAccounts);
			int receiver_index = random.nextInt(nbAccounts);
			// make sure indexes are different
			if(sender_index == receiver_index) {
				sender_index = (sender_index + 1) % nbAccounts;
			}
			assert(sender_index != receiver_index); // verify assumption at dev time
			CryptoAccount sender = accounts[sender_index];
			CryptoAccount receiver = accounts[receiver_index];

			// 3- execute transaction
			Double amount = random.nextDouble() * 100;
			Transaction t = new Transaction("id_", sender, receiver, amount);
			executor.execute(t);
		}

		// 4- wait for all transactions to complete before verifying total money in system again
		executor.shutdown();
		while(!executor.isTerminated()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Interrupted while waiting for transactions", e);
			}
		}

		long endTime = System.nanoTime();
		// 5- check total money in system after all transactions
		System.out.printf("Final money volume in system: %f%n%n", sumTotalMoneyInAccounts(accounts));

	    System.out.println("\nTotal time for multi-threading using thread pool: "
	      + (endTime - startTime) / 1_000_000.0 + " milliseconds");

		logger.info("Application is closing...");
	}

	private static Double sumTotalMoneyInAccounts(CryptoAccount[] accounts) {
		Double total = 0.0;
		for(CryptoAccount c: accounts) {
			total += c.getBalance();
		}
		return total;
	}

}
