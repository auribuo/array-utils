package com.github.auribuo;

// https://github.com/auribuo/array-utils
public class ArrayUtils {
    @FunctionalInterface
    public interface IntPredicate {
        boolean apply(int i);
    }

    @FunctionalInterface
    public interface Predicate<T> {
        boolean apply(T v);
    }

    @FunctionalInterface
    public interface Comparer<T> {
        int compare(T v1, T v2);
    }

    public static class Optional<T> {
        public static final class EmptyOptionalException extends RuntimeException {
            public EmptyOptionalException(String msg) {
                super(msg);
            }
        }

        private final T _value;
        private final boolean _hasValue;

        private Optional(T value, boolean ok) {
            _value = value;
            _hasValue = ok;
        }

        public static <T> Optional<T> some(T value) {
            return new Optional<>(value, true);
        }

        public static <T> Optional<T> none() {
            return new Optional<T>(null, false);
        }

        public T unwrap() throws EmptyOptionalException {
            if (_hasValue) {
                return _value;
            }
            throw new EmptyOptionalException("Unwrapping of result with no value");
        }

        public T orElse(T def) {
            if (_hasValue) {
                return _value;
            }
            return def;
        }
    }

    private static int combinedLengths(int[][] arrays) {
        var sum = 0;
        for (var arr : arrays) {
            sum += arr.length;
        }
        return sum;
    }

    /**
     * Checks if any of the elements in arr match the given predicate
     *
     * @param arr       The input array of elements to check
     * @param predicate The predicate to apply to the elements
     * @return True. if any of the elements match the predicate, else false
     */
    public static boolean anyMatch(int[] arr, IntPredicate predicate) {
        for (var i : arr) {
            if (predicate.apply(i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean allMatch(int[] arr, IntPredicate predicate) {
        for (var i : arr) {
            if (!predicate.apply(i)) {
                return false;
            }
        }
        return true;
    }

    public static <T> int count(T[] arr, Predicate<T> predicate) {
        int ctr = 0;
        for (var i : arr) {
            if (predicate.apply(i)) {
                ctr++;
            }
        }
        return ctr;
    }

    public static Optional<int[]> filter(int[] arr, IntPredicate predicate) {
        var matchArr = new Boolean[arr.length];
        for (int i = 0; i < arr.length; i++) {
            matchArr[i] = predicate.apply(arr[i]);
        }
        var matches = count(matchArr, Boolean::booleanValue);
        if (matches == 0) {
            return Optional.none();
        }
        var resArr = new int[matches];
        int writeIndex = 0;
        for (int j : arr) {
            if (predicate.apply(j)) {
                resArr[writeIndex++] = j;
            }
        }
        return Optional.some(resArr);
    }

    public static Optional<Integer> find(int[] arr, IntPredicate predicate) {
        for (var i : arr) {
            if (predicate.apply(i)) {
                return Optional.some(i);
            }
        }
        return Optional.none();
    }

    /**
     * Methode, die eine beliebige Zahl an Arrays (dargestellt als Array von Arrays) zu einem einzigen Array verbindet,
     * indem sie abwechselnd von jedem Array einen Eintrag nimmt, bis alle aufgebraucht sind.
     *
     * @param arrays Array von Integer-Arrays
     * @return Die Arrays in 'arrays' zusammengezipped
     */
    public static int[] zipMany(int[][] arrays) {
        int[] result = new int[combinedLengths(arrays)];
        int[] workIndices = new int[arrays.length];
        int writeIndex = 0;
        for (int i = 0; anyMatch(workIndices, index -> index >= 0); i++) {
            if (i == arrays.length) {
                i = 0;
            }
            if (workIndices[i] == -1) {
                continue;
            }
            if (workIndices[i] == arrays[i].length) {
                workIndices[i] = -1;
                continue;
            }
            result[writeIndex++] = arrays[i][workIndices[i]++];
        }
        return result;
    }

    /**
     * Rotiert das übergebene Array um die übergebene Anzahl an Schritten nach rechts.
     * Das Array wird In-Place rotiert. Es gibt keine Rückgabe.
     *
     * @param array  Ein beliebiges Integer-Array
     * @param amount Ein beliebiger Integer
     */
    public static void rotate(int[] array, int amount) {
        if (amount == 0 || array.length <= 1) {
            return;
        }

        int[] result = new int[array.length];
        int readIndex = ((array.length) - (amount % array.length)) % array.length;
        for (int writeIndex = 0; writeIndex < array.length; writeIndex++) {
            result[writeIndex] = array[readIndex];
            readIndex = (readIndex + 1) % (array.length);
        }
        System.arraycopy(result, 0, array, 0, result.length);
    }

    public static <T> Optional<T> min(T[] arr, Comparer<T> comparer) {
        if (arr.length == 0) {
            return Optional.none();
        }

        var min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (comparer.compare(arr[i], min) < 0) {
                min = arr[i];
            }
        }
        return Optional.some(min);
    }

    public static <T> Optional<T> max(T[] arr, Comparer<T> comparer) {
        if (arr.length == 0) {
            return Optional.none();
        }

        var max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (comparer.compare(arr[i], max) > 0) {
                max = arr[i];
            }
        }
        return Optional.some(max);
    }
}