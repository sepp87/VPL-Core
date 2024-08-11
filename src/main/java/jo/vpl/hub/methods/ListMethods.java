package jo.vpl.hub.methods;

import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.HubInfo;

/**
 *
 * @author Joost
 */
public class ListMethods {

    @HubInfo(
            identifier = "List.create",
            category = "Core",
            description = "")
    public static <T> List<?> create(T t) {
        if (t == null) {
            return new ArrayList<>();
        }
        return new ArrayList<T>();
    }

    @HubInfo(
            identifier = "List.clear",
            category = "Core",
            description = "Removes all of the elements from this list. The list will be empty after this call returns.")
    public static <T> List<T> clear(List<T> list) {
        return new ArrayList<>();
    }

    @HubInfo(
            identifier = "List.get",
            category = "Core",
            description = "Returns the element at the specified position in this list.")
    public static <T> T get(List<T> list, int index) {
        return list.get(index);
    }

    @HubInfo(
            identifier = "List.getFirst",
            category = "Core",
            description = "Gets the first element of this collection.")
    public static <T> T getFirst(List<T> list) {
        return list.getFirst();
    }

    @HubInfo(
            identifier = "List.getLast",
            category = "Core",
            description = "Gets the last element of this collection.")
    public static <T> T getLast(List<T> list) {
        return list.getLast();
    }

    @HubInfo(
            identifier = "List.isEmpty",
            category = "Core",
            description = "Returns true if this list contains no elements.")
    public static <T> boolean isEmpty(List<T> list) {
        return list.isEmpty();
    }

    @HubInfo(
            identifier = "List.reversed",
            category = "Core",
            description = "Returns a reverse-ordered view of this collection.")
    public static <T> List<T> reversed(List<T> list) {
        return list.reversed();
    }

    @HubInfo(
            identifier = "List.size",
            category = "Core",
            description = "Returns the number of elements in this list.")
    public static int size(List<?> list) {
        return list.size();
    }

    // TODO return the removed item
    @HubInfo(
            identifier = "List.remove",
            category = "Core",
            description = "Removes the element at the specified position in this list. ")
    public static <T> List<T> remove(List<T> list, int index) {
        List<T> result = new ArrayList<>(list);
        result.remove(index);
        return result;
    }

    // TODO return the removed item
    @HubInfo(
            identifier = "List.removeFirst",
            category = "Core",
            description = "Removes the first element of this collection.")
    public static <T> List<T> removeFirst(List<T> list) {
        List<T> result = new ArrayList<>(list);
        result.removeFirst();
        return result;
    }

    // TODO return the removed item
    @HubInfo(
            identifier = "List.removeLast",
            category = "Core",
            description = "Removes the last element of this collection.")
    public static <T> List<T> removeLast(List<T> list) {
        List<T> result = new ArrayList<>(list);
        result.removeLast();
        return result;
    }

    @HubInfo(
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
                List oList = (List) o;
                flattenRecursively(oList, result);
            } else {
                result.add(o);
            }
        }
        return result;
    }

    @HubInfo(
            identifier = "List.add",
            category = "Core",
            description = "Appends the specified element to the end of this list.")
    public static <T> List<T> add(List<T> list, T t, Integer index) {
        List<T> result = new ArrayList<>(list);
        if (index == null) {
            result.add(t);
        } else {
            result.add(index, t);
        }
        return result;
    }

    @HubInfo(
            identifier = "List.addFirst",
            category = "Core",
            description = "Adds an element as the first element of this collection.")
    public static <T> List<T> addFirst(List<T> list, T t) {
        List<T> result = new ArrayList<>(list);
        result.addFirst(t);
        return result;
    }

    @HubInfo(
            identifier = "List.addLast",
            category = "Core",
            description = "Adds an element as the last element of this collection.")
    public static <T> List<T> addLast(List<T> list, T t) {
        List<T> result = new ArrayList<>(list);
        result.addLast(t);
        return result;
    }

    @HubInfo(
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

}
