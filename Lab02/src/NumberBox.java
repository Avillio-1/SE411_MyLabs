import java.util.List;
import java.util.Objects;

/** Exercise 2: a wrapper restricted to Number subclasses. */
public class NumberBox<T extends Number> {
    private T item;

    public NumberBox(T item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public void setItem(T item) {
        this.item = Objects.requireNonNull(item, "item");
    }

    public T getItem() {
        return item;
    }

    /** Returns a sum without changing the wrapped item or casting a result to T. */
    public double add(Number other) {
        return item.doubleValue() + other.doubleValue();
    }

    public static double sumNumbers(List<? extends Number> numbers) {
        double sum = 0.0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }
}
