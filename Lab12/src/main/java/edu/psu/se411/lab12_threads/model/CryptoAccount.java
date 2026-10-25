package edu.psu.se411.lab12_threads.model;

import edu.psu.se411.lab12_threads.exceptions.InsufficientBalanceException;
import java.util.concurrent.locks.ReentrantLock;

public class CryptoAccount {
	private final String id;
	private final ReentrantLock lock = new ReentrantLock();
	private Double balance;

	public CryptoAccount(String id, Double balance) {
		if(balance < 0) throw new IllegalArgumentException("Account balance cannot be negative");
		this.id = id;
		this.balance = balance;
	}

	public void addToBalance(Double amount) {
		lock.lock();
		try {
			if(amount < 0) throw new IllegalArgumentException("Amount added to balance cannot be negative");
			balance += amount;
		} finally {
			lock.unlock();
		}
	}

	public void retrieveFromBalance(Double amount) throws InsufficientBalanceException {
		lock.lock();
		try {
			if(amount > balance) throw new InsufficientBalanceException(id);
			balance -= amount;
		} finally {
			lock.unlock();
		}
	}

	public String getId() {
		return id;
	}
	public Double getBalance() {
		lock.lock();
		try {
			return balance;
		} finally {
			lock.unlock();
		}
	}

	public ReentrantLock getLock() {
		return lock;
	}

}
