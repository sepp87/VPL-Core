package btslib.method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import btscore.graph.block.BlockMetadata;
import btscore.util.ListUtils;

/**
 *
 * @author Joost
 */
public class ListMethods {

    @BlockMetadata(
            identifier = "List.create",
            category = "Core",
            description = "")
    public static List<Object> create() {
        return new ArrayList<>();
    }

    @BlockMetadata(
            identifier = "List.clear",
            category = "Core",
            description = "Removes all of the elements from this list. The list will be empty after this call returns.")
    public static <T> List<T> clear(List<T> list) {
        return new ArrayList<>();
    }

    @BlockMetadata(
            identifier = "List.get",
            aliases = {"List.getIndex"},
            category = "Core",
            description = "Returns the element at the specified position in this list.")
    public static <T> T get(List<T> list, Integer index) {
        return list.get(index);
    }

    @BlockMetadata(
            identifier = "List.getFirst",
            category = "Core",
            description = "Gets the first element of this collection.")
    public static <T> T getFirst(List<T> list) {
        return list.getFirst();
    }

    @BlockMetadata(
            identifier = "List.getLast",
            category = "Core",
            description = "Gets the last element of this collection.")
    public static <T> T getLast(List<T> list) {
        return list.getLast();
    }

    @BlockMetadata(
            identifier = "List.isEmpty",
            category = "Core",
            description = "Returns true if this list contains no elements.")
    public static <T> boolean isEmpty(List<T> list) {
        return list.isEmpty();
    }

    @BlockMetadata(
            identifier = "List.reversed",
            category = "Core",
            description = "Returns a reverse-ordered view of this collection.")
    public static <T> List<T> reversed(List<T> list) {
        return list.reversed();
    }

    @BlockMetadata(
            identifier = "List.size",
            category = "Core",
            description = "Returns the number of elements in this list.")
    public static int size(List<?> list) {
        return list.size();
    }

    // TODO return the removed item
    @BlockMetadata(
            identifier = "List.remove",
            category = "Core",
            description = "Removes the element at the specified position in this list. ")
    public static <T> List<T> remove(List<T> list, int index) {
        List<T> result = new ArrayList<>(list);
        result.remove(index);
        return result;
    }

    // TODO return the removed item
    @BlockMetadata(
            identifier = "List.removeFirst",
            category = "Core",
            description = "Removes the first element of this collection.")
    public static <T> List<T> removeFirst(List<T> list) {
        List<T> result = new ArrayList<>(list);
        result.removeFirst();
        return result;
    }

    // TODO return the removed item
    @BlockMetadata(
            identifier = "List.removeLast",
            category = "Core",
            description = "Removes the last element of this collection.")
    public static <T> List<T> removeLast(List<T> list) {
        List<T> result = new ArrayList<>(list);
        result.removeLast();
        return result;
    }

    // TODO return the replaced item
    @BlockMetadata(
            identifier = "List.set",
            aliases = {"List.replace"},
            category = "Core",
            description = "Replaces the element at the specified position in this list with the specified element.")
    public static <T> List<T> set(List<T> list, int index, T t) {
        List<T> result = new ArrayList<>(list);
        result.set(index, t);
        return result;
    }

    @BlockMetadata(
            identifier = "List.flatten",
            category = "Core",
            description = "Removes all nested lists and adds all leaf items to the root list.")
    public static List<?> flatten(List<?> list) {
        List<Object> result = new ArrayList<>();
        return flattenRecursively(list, result);
    }

    private static List<?> flattenRecursively(List<?> list, List<Object> result) {
        for (Object o : list) {
            if (List.class.isAssignableFrom(o.getClass())) {
                List<?> oList = (List) o;
                flattenRecursively(oList, result);
            } else {
                result.add(o);
            }
        }
        return result;
    }

    @BlockMetadata(
            identifier = "List.add",
            category = "Core",
            description = "Appends the specified element to the end of this list, if no index is provided.")
    public static <T> List<T> add(List<T> list, T t, Integer index) {
        List<T> result = new ArrayList<>(list);
        if (index == null) {
            result.add(t);
        } else {
            result.add(index, t);
        }
        return result;
    }

    @BlockMetadata(
            identifier = "List.addFirst",
            category = "Core",
            description = "Adds an element as the first element of this collection.")
    public static <T> List<T> addFirst(List<T> list, T t) {
        List<T> result = new ArrayList<>(list);
        result.addFirst(t);
        return result;
    }

    @BlockMetadata(
            identifier = "List.addLast",
            category = "Core",
            description = "Adds an element as the last element of this collection.")
    public static <T> List<T> addLast(List<T> list, T t) {
        List<T> result = new ArrayList<>(list);
        result.addLast(t);
        return result;
    }

    @BlockMetadata(
            identifier = "List.addAll",
            category = "Core",
            description = "Inserts all of the elements in the specified collection into this list at the specified position. If no index is specified, all items are appended to the end of the list.")
    public static <T> List<T> addAll(List<T> a, List<T> b, Integer index) {
        List<T> result = new ArrayList<>(a);
        if (index == null) {
            result.addAll(b);
        } else {
            result.addAll(index, b);
        }
        return result;
    }

    @BlockMetadata(
            identifier = "List.shuffle",
            category = "Core",
            description = "Randomly permutes the specified list using a default source of randomness. All permutations occur with approximately equal likelihood.")
    public static <T> List<T> shuffle(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.shuffle(result);
        return result;
    }

    @BlockMetadata(
            identifier = "List.rotate",
            category = "Core",
            description = "Rotates the elements in the specified list by the specified distance. After calling this method, the element at index i will be the element previously at index (i - distance) mod list.size(), for all values of i between 0 and list.size()-1, inclusive. (This method has no effect on the size of the list.)")
    public static <T> List<T> rotate(List<T> list, Integer index) {
        index = index == null ? 0 : index;
        List<T> result = new ArrayList<>(list);
        Collections.rotate(result, index);
        return result;
    }

    @BlockMetadata(
            identifier = "List.sort",
            category = "Core",
            description = "Sorts the specified list into ascending order, according to the natural ordering of its elements. All elements in the list must implement the Comparable interface. Furthermore, all elements in the list must be mutually comparable (that is, e1.compareTo(e2) must not throw a ClassCastException for any elements e1 and e2 in the list).")
    public static <T extends Comparable<? super T>> List<T> sort(List<T> list, Integer index) {
        List<T> result = new ArrayList<>(list);
        Collections.sort(result);
        return result;
    }

    @BlockMetadata(
            identifier = "List.transpose",
            category = "Core",
            description = "Swap rows and columns, so that the first row becomes the first column, the second row becomes the second column, and so on.")
    public static <T> List<List<T>> transpose(List<List<T>> matrix) {
        if (matrix == null || matrix.isEmpty()) {
            return new ArrayList<>();
        }

        int rowCount = matrix.size();
        int colCount = matrix.get(0).size(); // Assuming all rows have the same length

        List<List<T>> transposed = new ArrayList<>();
        for (int col = 0; col < colCount; col++) {
            List<T> newRow = new ArrayList<>();
            for (int row = 0; row < rowCount; row++) {
                if (col < matrix.get(row).size()) { // Prevent IndexOutOfBounds
                    newRow.add(matrix.get(row).get(col));
                }
            }
            transposed.add(newRow);
        }
        return transposed;
    }

    @BlockMetadata(
            identifier = "List.indexOf",
            category = "Core",
            description = "Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element.")
    public static <T> int indexOf(List<T> list, Object o) {
        return list.indexOf(o);
    }

    private static boolean allLists(List list) {
        for (Object o : list) {
            if (!ListUtils.isList(o)) {
                return false;
            }
        }
        return true;
    }

    private static int countNestedLists(List list) {
        int result = 0;
        for (Object p : list) {
            if (ListUtils.isList(p)) {
                result++;
            }
        }
        return result;
    }

    private static int sizeOfLongestNestedList(List list) {
        int result = -1;
        for (Object p : list) {
            if (ListUtils.isList(p)) {
                List<?> nestedList = (List<?>) p;
                result = nestedList.size() > result ? nestedList.size() : result;
            }
        }
        return result;
    }

}
