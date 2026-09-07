# Lab 02: Introduction to Java Generics

Solutions to all four exercises in the SE411 Fall 2026–2027 Lab 02 handout,
including the wildcard exercise assigned for home practice.

## Run

Requires JDK 21. No Maven or external dependencies are needed. From the repository
root, run these commands in PowerShell (also supported by common Unix shells):

```text
javac -Xlint:all -Werror -d Lab02/out Lab02/src/*.java Lab02/test/*.java
java -cp Lab02/out Main
java -cp Lab02/out Lab02Test
```

## Exercise 1: Generic list

[`PrintableList<T>`](src/PrintableList.java) stores a `List<T>`. Its constructor
copies the supplied array using `Arrays.asList` and `ArrayList`, so later changes
to the source array do not change the stored list. `printItems()` prints each
item. [`Main`](src/Main.java) demonstrates a `PrintableList<String>`.

## Exercise 2: Bounded type parameters

[`NumberBox<T extends Number>`](src/NumberBox.java) stores a non-null number and
provides `setItem` and `getItem`. The main program demonstrates both `Integer`
and `Double` boxes. `add(Number)` adds a number to the stored value without
modifying it, while `sumNumbers(List<? extends Number>)` totals a list.

Both arithmetic methods return `double`: Java cannot apply `+` directly to a
generic `T`, and a sum cannot safely be cast back to an arbitrary Number subtype.
This uses `doubleValue()` and is intended for the lab's ordinary numeric examples;
large integers or high-precision decimal values can lose precision. An empty list
sums to zero.

## Exercise 3: Reusable pipeline

[`Transformer<T, R>`](src/Transformer.java) defines `R transform(T input)`.
[`PipeLine<T, R>`](src/PipeLine.java) starts with an identity transformation using
`PipeLine.<T>start()`. Each `add` returns a new pipeline with the original input
type and the new output type. `execute` applies the transformations in order.
The composed transformers retain the sequence without raw types or unchecked casts.

The handout uses both `PipeLine<T, R>` and `Pipeline<T>`; this solution follows
its explicit two-parameter class declaration so both endpoint types remain known.
Same-type transformations are supported as well as type-changing transformations.
Adding a step leaves the earlier pipeline reusable.

The main example is:

```text
String -> trimmed String -> Integer length -> doubled Integer -> String description
```

For `"  Java  "`, it prints `Doubled length: 8`. Reusing the same pipeline with
`"  Generics  "` prints `Doubled length: 16`.

## Exercise 4: Wildcards

`Main.printList(List<?>)` prints lists of any element type. The wildcard is safe
because the method only reads elements as `Object`.
`Main.sumNumbers(List<? extends Number>)` accepts integer, double, or mixed-number
lists and delegates to the number-summing method from Exercise 2. The upper bound
allows reading every element as a `Number`.

## Verification

[`Lab02Test`](test/Lab02Test.java) checks printed contents, the array copy,
integer/double storage and arithmetic, empty and mixed numeric lists, pipeline
order, type-changing steps, repeated execution, reuse of earlier pipelines, and
both wildcard methods. Checks throw on failure without requiring `-ea`.

Verified with JDK 21 on 2026-09-07: compilation with all lint warnings enabled and
treated as errors, successful execution of `Main`, and all 30 checks passing.
