import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Dependency-free checks; failures throw even when Java assertions are disabled. */
public class Lab02Test {
    private static int checks;

    public static void main(String[] args) {
        String[] source = {"Java", "Generics"};
        PrintableList<String> printable = new PrintableList<>(source);
        source[0] = "Changed";
        check("Java\nGenerics\n", capture(printable::printItems));
        check("1\n2\n", capture(() -> new PrintableList<>(new Integer[]{1, 2}).printItems()));
        check("", capture(() -> new PrintableList<>(new String[]{}).printItems()));

        NumberBox<Integer> integerBox = new NumberBox<>(10);
        check(10, integerBox.getItem());
        integerBox.setItem(-2);
        check(-2, integerBox.getItem());
        check(3.0, integerBox.add(5));
        check(-2, integerBox.getItem());
        NumberBox<Double> doubleBox = new NumberBox<>(2.5);
        doubleBox.setItem(3.5);
        check(3.5, doubleBox.getItem());
        check(4.75, doubleBox.add(1.25));
        check(5.5, doubleBox.add(2));
        check(0.0, NumberBox.sumNumbers(List.of()));
        check(6.0, NumberBox.sumNumbers(List.of(1, 2, 3)));
        check(4.0, NumberBox.sumNumbers(List.of(1.5, 2.5)));
        check(1.5, NumberBox.sumNumbers(List.<Number>of(-2, 0.5, 3L)));

        PipeLine<String, String> identity = PipeLine.start();
        check("  Java  ", identity.execute("  Java  "));
        PipeLine<String, String> trimmed = identity.add(String::trim);
        PipeLine<String, Integer> lengths = trimmed.add(String::length);
        PipeLine<String, String> description = lengths.add(n -> n * 2).add(n -> "Length: " + n);
        check("Length: 8", description.execute("  Java  "));
        check("Length: 16", description.execute("  Generics  "));
        check("Java", trimmed.execute("  Java  "));
        check(4, lengths.execute("  Java  "));
        check("  Java  ", identity.execute("  Java  "));
        List<Integer> order = new ArrayList<>();
        PipeLine<Integer, Integer> ordered = PipeLine.<Integer>start()
                .add(n -> { order.add(1); return n + 1; })
                .add(n -> { order.add(2); return n * 3; });
        check(9, ordered.execute(2));
        check(List.of(1, 2), order);
        Transformer<Number, String> format = n -> "Value: " + n;
        check("Value: 4", lengths.add(format).execute("Java"));

        check("a\nb\n", capture(() -> Main.printList(List.of("a", "b"))));
        check("1\n2\n", capture(() -> Main.printList(List.of(1, 2))));
        check("", capture(() -> Main.printList(List.of())));
        check(6.0, Main.sumNumbers(List.of(1, 2, 3)));
        check(4.0, Main.sumNumbers(List.of(1.5, 2.5)));
        check(6.5, Main.sumNumbers(List.<Number>of(1, 2.5, 3L)));
        check(0.0, Main.sumNumbers(List.of()));
        System.out.println("All " + checks + " checks passed.");
    }

    private static void check(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
        checks++;
    }

    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
