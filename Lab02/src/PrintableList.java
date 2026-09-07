import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Exercise 1: stores and prints items of any one type. */
public class PrintableList<T> {
    private final List<T> items;

    public PrintableList(T[] items) {
        this.items = new ArrayList<>(Arrays.asList(items));
    }

    public void printItems() {
        for (T item : items) {
            System.out.println(item);
        }
    }
}
