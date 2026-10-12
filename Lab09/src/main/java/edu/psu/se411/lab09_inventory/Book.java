package edu.psu.se411.lab09_inventory;

public class Book extends Item {
    private final String authorName;

    public Book(int id, String name, String authorName) {
        super(id, name);
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String toString() {
        return "Book{id=" + getId() + ", name='" + getName()
                + "', author='" + authorName + "'}";
    }
}
