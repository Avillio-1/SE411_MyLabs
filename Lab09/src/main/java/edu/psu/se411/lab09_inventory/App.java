package edu.psu.se411.lab09_inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		logger.info("Application is starting...");
		Inventory<Item> inventory = new Inventory<>();
		inventory.addItem(new Book(1, "Concurrency in Java", "Author One"));
		inventory.addItem(new Book(2, "Java Generics", "Author Two"));
		inventory.addItem(new ElectronicDevice(3, "Laptop", "Computers"));
		inventory.addItem(new ElectronicDevice(4, "Phone", "Mobile"));

		System.out.println("Book by name: "
				+ inventory.findItems(new SearchByName("Concurrency in Java")));
		System.out.println("Device by name: "
				+ inventory.findItems(new SearchByName("Laptop")));
		System.out.println("Item by ID: " + inventory.findItems(new SearchById(4)));
		System.out.println("Lambda search: "
				+ inventory.findItems(item -> item.getName().startsWith("Java")));

		displayInventory(inventory);

		logger.info("Application is closing...");
	}

	public static void displayInventory(Inventory<?> inventory) {
		for (Object item : inventory.getAllItems()) {
			System.out.println(item);
		}
	}

}
