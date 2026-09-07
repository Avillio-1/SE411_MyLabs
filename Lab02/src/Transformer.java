/** Exercise 3: a transformation with independent input and output types. */
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input);
}
