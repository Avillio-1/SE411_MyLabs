package edu.psu.se411.lab09_inventory;

public class SearchByAuthor implements SearchStrategy<Book> {
    private final String authorName;

    public SearchByAuthor(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public boolean matches(Book book) {
        return authorName.equals(book.getAuthorName());
    }
}
