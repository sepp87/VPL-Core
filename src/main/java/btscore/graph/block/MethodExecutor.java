package btscore.graph.block;

import btscore.utils.ListUtils;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author joostmeulenkamp
 */
public class MethodExecutor {

    private final Method method;

    public MethodExecutor(Method method) {
        this.method = method;
    }

    public InvocationResult invoke(Object... parameters) {
        Deque<Integer> traversalLog = new ArrayDeque<>(); // keep track which index of the list is currently being processed
        return invokeMethodArgs(traversalLog, parameters);
    }

    private InvocationResult invokeMethodArgs(Deque<Integer> traversalLog, Object... parameters) {
        int listCount = getListCount(parameters);

        System.out.println("listCount " + listCount + "; method.getParameters().length " + method.getParameters().length);
        if (listCount == 0) { // none are list - invoke method
            InvocationResult invocationResult = new InvocationResult();

            try {
                Object result = method.invoke(null, parameters);
                invocationResult.data().set(result);

            } catch (Exception e) {
                e.printStackTrace();
                Throwable throwable = e;
                if (e.getCause() != null) {
                    throwable = e.getCause();
                }
                ExceptionPanel.BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(traversalLog), ExceptionPanel.Severity.ERROR, throwable);
                invocationResult.exceptions.add(exception);
            }
            return invocationResult;

        } else if (listCount == method.getParameters().length) { // all are lists - loop and recurse

            List<Object> list = new ArrayList<>();
            InvocationResult invocationResult = new InvocationResult();
            invocationResult.data().set(list);

            long shortestListSize = getShortestListSize(parameters);
            for (int i = 0; i < shortestListSize; i++) {
                traversalLog.add(i);

                Object[] array = new Object[parameters.length];
                for (int j = 0; j < parameters.length; j++) {
                    List<?> p = (List<?>) parameters[j];
                    Object item = p.get(i);
                    array[j] = item;
                }

                InvocationResult subResult = invokeMethodArgs(traversalLog, array);
                invocationResult.exceptions().addAll(subResult.exceptions);
                list.add(subResult.data.get());

                traversalLog.pop();
            }
            return invocationResult;

        } else { // some are list, some are not - make all lists and recurse
            System.out.println("some are list, some are not - make all lists and recurse");
            long shortestListSize = getShortestListSize(parameters);
            if (shortestListSize == 0) {
                return null;
            }

            for (int i = 0; i < method.getParameters().length; i++) {
                Object p = parameters[i];
                if (ListUtils.isList(p)) {
                    System.out.println("Parameter " + i + " is list ");
                    continue;
                }
                System.out.println("Parameter " + i + " to list ");
                List<Object> list = new ArrayList<>();
                for (int j = 0; j < shortestListSize; j++) {
                    list.add(p);
                }
                parameters[i] = list;
            }
            InvocationResult invocationResult = invokeMethodArgs(traversalLog, parameters);
            return invocationResult;
        }
    }

    private String getExceptionIndex(Deque<Integer> traversalLog) {
        if (traversalLog.isEmpty()) {
            return null;
        }
        String result = "";
        for (Integer index : traversalLog.reversed()) {
            result += "[" + index + "]";
        }
        return result;
    }

    private int getListCount(Object... parameters) {
        int result = 0;
        int i = 0;
        for (Object p : parameters) {
            System.out.println("parameter " + i + " is " + p.getClass().getSimpleName());
            if (ListUtils.isList(p)) {
                result++;
            }
            i++;
        }
        return result;
    }

    private int getShortestListSize(Object... parameters) {
        List<?> first = getFirstList(parameters);
        int result = -1;
        if (first == null) {
            return result;
        }
        result = first.size();
        for (Object p : parameters) {
            if (ListUtils.isList(p)) {
                List<?> list = (List<?>) p;
                result = list.size() < result ? list.size() : result;
            }
        }
        return result;
    }

    private List<?> getFirstList(Object... parameters) {
        for (Object p : parameters) {
            if (ListUtils.isList(p)) {
                return (List<?>) p;
            }
        }
        return null;
    }

    public record InvocationResult(
            ObjectProperty data,
            List<ExceptionPanel.BlockException> exceptions) {

        public InvocationResult() {
            this(new SimpleObjectProperty(), new ArrayList<>());
        }
    }

}
