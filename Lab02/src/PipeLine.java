import java.util.Objects;

/** A reusable sequence represented by composed, type-safe transformers. */
public final class PipeLine<T, R> {
    private final Transformer<T, R> transformations;

    private PipeLine(Transformer<T, R> transformations) {
        this.transformations = transformations;
    }

    public static <T> PipeLine<T, T> start() {
        return new PipeLine<>(input -> input);
    }

    /** Keeps the original input type and returns a new pipeline ending in V. */
    public <V> PipeLine<T, V> add(Transformer<? super R, ? extends V> next) {
        Objects.requireNonNull(next, "next");
        return new PipeLine<>(input -> next.transform(transformations.transform(input)));
    }

    public R execute(T input) {
        return transformations.transform(input);
    }
}
