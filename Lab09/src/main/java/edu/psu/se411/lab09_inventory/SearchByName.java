package edu.psu.se411.lab09_inventory;

public class SearchByName implements SearchStrategy<Item> {
    private final String name;

    public SearchByName(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(Item item) {
        return name.equals(item.getName());
    }
}
