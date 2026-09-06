package edu.psu.se411.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StackTest {

    @Test
    void pushPushPopReturnsLatestElement() {
        Stack<String> stack = new Stack<>();
        stack.push("Z");
        stack.push("A");

        assertEquals("A", stack.pop());
    }

    @Test
    void popEmptyStackThrowsExactExceptionAndMessage() {
        Stack<String> stack = new Stack<>();

        NoSuchElementException thrown =
                assertThrowsExactly(NoSuchElementException.class, stack::pop);

        assertEquals("Stack is empty, cannot pop", thrown.getMessage());
    }

    @Test
    void elementsArePoppedInReverseOrder() {
        Stack<String> stack = new Stack<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");

        assertEquals("third", stack.pop());
        assertEquals("second", stack.pop());
        assertEquals("first", stack.pop());
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 0, -1, Integer.MIN_VALUE})
    void constructorCreatesEmptyGrowableStack(int capacity) {
        Stack<Integer> stack = new Stack<>(capacity);
        assertThrowsExactly(NoSuchElementException.class, stack::pop);

        // Cross both the explicit capacity 1 and the default/fallback capacity 10.
        for (int value = 0; value < 11; value++) {
            stack.push(value);
        }
        for (int expected = 10; expected >= 0; expected--) {
            assertEquals(Integer.valueOf(expected), stack.pop());
        }
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void defaultCapacityCanGrowBeyondTenElements() {
        Stack<Integer> stack = new Stack<>();
        for (int value = 0; value < 11; value++) {
            stack.push(value);
        }
        for (int expected = 10; expected >= 0; expected--) {
            assertEquals(Integer.valueOf(expected), stack.pop());
        }
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void nullAndEmptyStringAreStoredAsElements() {
        Stack<String> stack = new Stack<>();
        stack.push("base");
        stack.push(null);
        stack.push("");

        assertEquals("", stack.pop());
        assertNull(stack.pop());
        assertEquals("base", stack.pop());
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void duplicateElementsAreNotCollapsed() {
        Stack<String> stack = new Stack<>();
        stack.push("same");
        stack.push("same");

        assertEquals("same", stack.pop());
        assertEquals("same", stack.pop());
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void integerBoundaryValuesArePreserved() {
        Stack<Integer> stack = new Stack<>();
        stack.push(Integer.MIN_VALUE);
        stack.push(-1);
        stack.push(0);
        stack.push(Integer.MAX_VALUE);

        assertEquals(Integer.valueOf(Integer.MAX_VALUE), stack.pop());
        assertEquals(Integer.valueOf(0), stack.pop());
        assertEquals(Integer.valueOf(-1), stack.pop());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), stack.pop());
    }

    @Test
    void emptyCollectionRetainsItsObjectIdentity() {
        Stack<List<String>> stack = new Stack<>();
        List<String> element = new ArrayList<>();
        stack.push(element);

        assertSame(element, stack.pop());
        assertTrue(element.isEmpty());
    }

    @Test
    void interleavedOperationsPreserveRemainingElements() {
        Stack<String> stack = new Stack<>();
        stack.push("A");
        stack.push("B");
        assertEquals("B", stack.pop());

        stack.push("C");
        assertEquals("C", stack.pop());
        assertEquals("A", stack.pop());
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void stackCanBeReusedAfterDrainingAndFailedPops() {
        Stack<String> stack = new Stack<>();
        stack.push("old");
        assertEquals("old", stack.pop());
        for (int attempt = 0; attempt < 2; attempt++) {
            NoSuchElementException thrown =
                    assertThrowsExactly(NoSuchElementException.class, stack::pop);
            assertEquals("Stack is empty, cannot pop", thrown.getMessage());
        }

        stack.push("new");
        assertEquals("new", stack.pop());
        assertThrowsExactly(NoSuchElementException.class, stack::pop);
    }

    @Test
    void stackInstancesHaveIndependentState() {
        Stack<String> first = new Stack<>();
        Stack<String> second = new Stack<>();
        first.push("first");
        assertThrowsExactly(NoSuchElementException.class, second::pop);

        second.push("second");
        assertEquals("first", first.pop());
        assertEquals("second", second.pop());
    }
}
