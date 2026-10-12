package edu.psu.se411.lab11_threads.model;

import java.util.UUID;

import edu.psu.se411.lab11_threads.exceptions.InsufficientBalanceException;

public class Transaction {
	private String id;
	private CryptoAccount sender;
	private CryptoAccount receiver;
	private Double amount;

	public Transaction(CryptoAccount sender, CryptoAccount receiver, Double amount) {
		this(UUID.randomUUID().toString(), sender, receiver, amount);
	}

	public Transaction(String id, CryptoAccount sender, CryptoAccount receiver, Double amount) {
		super();
		if(id == null || sender == null || receiver == null || amount < 0) throw new IllegalArgumentException();
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}

	public void execute() throws InsufficientBalanceException {
		sender.retrieveFromBalance(amount);
		receiver.addToBalance(amount);
	}

	public static synchronized void executeSynchronized(Transaction transaction)
			throws InsufficientBalanceException {
		transaction.execute();
	}

	public CryptoAccount getSender() {
		return sender;
	}
	public void setSender(CryptoAccount sender) {
		this.sender = sender;
	}
	public CryptoAccount getReceiver() {
		return receiver;
	}
	public void setReceiver(CryptoAccount receiver) {
		this.receiver = receiver;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getId() {
		return id;
	}
}
