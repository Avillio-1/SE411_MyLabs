import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Exercise 1: PrintableList<String>");
        PrintableList<String> names = new PrintableList<>(new String[]{"Java", "Generics", "SE411"});
        names.printItems();

        System.out.println("\nExercise 2: NumberBox");
        NumberBox<Integer> integerBox = new NumberBox<>(10);
        integerBox.setItem(20);
        System.out.println("Integer item: " + integerBox.getItem());
        System.out.println("Integer item + 5: " + integerBox.add(5));
        NumberBox<Double> doubleBox = new NumberBox<>(2.5);
        doubleBox.setItem(3.5);
        System.out.println("Double item: " + doubleBox.getItem());
        System.out.println("Double item + 1.25: " + doubleBox.add(1.25));
        System.out.println("Sum of integers: " + NumberBox.sumNumbers(List.of(1, 2, 3)));
        System.out.println("Sum of doubles: " + NumberBox.sumNumbers(List.of(1.5, 2.5, 3.5)));

        System.out.println("\nExercise 3: PipeLine");
        PipeLine<String, String> trimmed = PipeLine.<String>start().add(String::trim);
        PipeLine<String, Integer> lengths = trimmed.add(String::length);
        PipeLine<String, String> described = lengths
                .add(length -> length * 2)
                .add(length -> "Doubled length: " + length);
        System.out.println(described.execute("  Java  "));
        System.out.println(described.execute("  Generics  "));
        System.out.println("Original pipeline: " + trimmed.execute("  SE411  "));

        System.out.println("\nExercise 4: Wildcards");
        printList(List.of("One", "Two"));
        printList(List.of(1, 2, 3));
        System.out.println("Integer sum: " + sumNumbers(List.of(1, 2, 3)));
        System.out.println("Double sum: " + sumNumbers(List.of(1.5, 2.5)));
        System.out.println("Mixed number sum: " + sumNumbers(List.<Number>of(1, 2.5, 3L)));
    }

    public static void printList(List<?> items) {
        for (Object item : items) {
            System.out.println(item);
        }
    }

    public static double sumNumbers(List<? extends Number> numbers) {
        return NumberBox.sumNumbers(numbers);
    }
}
