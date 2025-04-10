package vplcore.graph.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import jo.vpl.xml.BlockTag;
import vplcore.IconType;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockView;
import vplcore.graph.block.ExceptionPanel;
import vplcore.graph.block.ExceptionPanel.BlockException;
import vplcore.util.ListUtils;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Core.reflectionBlock",
        category = "Core",
        description = "A generic block used to convert static methods and fields to blocks",
        tags = {"core", "reflection", "block"})
public class MethodBlock extends BlockModel {

    private final BlockMetadata info;
    private String identifier;
    private String category;
    private String description;
    private String[] tags;
    private Method method;

    private StackPane container;
    private ProgressIndicator spinner;
    private Label label;

//    public MethodBlock(WorkspaceModel workspace, Method method) {
//        super(workspace);
    public MethodBlock(Method method) {
        this.info = method.getAnnotation(BlockMetadata.class);
        this.identifier = info.identifier();
        this.category = info.category();
        this.description = info.description();
        this.tags = info.tags();
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    protected void initialize() {

    }

    @Override
    public Region getCustomization() {

        spinner = new ProgressIndicator();

        if (!info.icon().equals(IconType.NULL)) {
            label = BlockView.getAwesomeIcon(info.icon());

        } else if (!info.name().equals("")) {
            label = new Label(info.name());
            label.getStyleClass().add("block-text");

        } else {
            String shortName = info.identifier().split("\\.")[1];
            label = new Label(shortName);
            label.getStyleClass().add("block-text");
        }
        spinner.prefWidthProperty().bind(label.widthProperty());
        container = new StackPane(label);
        return container;
    }

    public boolean isListOperator = false;
    public boolean isListReturnType = false;
    private final Deque<Integer> traversalLog = new ArrayDeque<>(); // keep track which index of the list is currently being processed

    /**
     * process function is called whenever new data is incoming
     */
    @Override
    public final void process() {
        Set<BlockException> previousExceptions = new HashSet<>(exceptions);

        Task<Result> task = new Task<>() {
            @Override
            protected Result call() {
                Object result = processLater();
                if (result instanceof Result r) {
                    Platform.runLater(() -> {
                        int size = exceptions.size();
                        exceptions.addAll(r.exs);
                        exceptions.remove(0, size);

                        System.out.println("EXCEPTIONS were SET " + previousExceptions.size());
                        System.out.println("EXCEPTIONS TO BE SET " + r.exs.size());
                        System.out.println("EXCEPTIONS ARE SET " + exceptions.size());
                        if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
                            exceptions.clear();

                        }

                    });
                }
                return new Result();
            }
        };

        if (container != null && label.getWidth() != 0.0) {
//            task.setOnSucceeded(event -> {
            task.setOnSucceeded(event -> Platform.runLater(() -> {
                container.getChildren().clear();
                container.getChildren().add(label);
            }));
//            });
        }

        if (container != null && label.getWidth() != 0.0) {
            spinner.setMinWidth(label.getWidth());
            container.getChildren().clear();
            container.getChildren().add(spinner);
        }

        // Run the task in a separate thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public int currentprocess = 0;

    public Object processLater() {
//    @Override
//    public void process() {
        currentprocess++;
//        System.out.println(" process later called " + method.getName());

        System.out.println(currentprocess + " process later called " + method.getName());
        final Object[] resultRef = {null}; // Use an array instead of AtomicReference
        final Throwable[] exceptionRef = {null};

        traversalLog.clear();
//        Object result = null;
        int count = inputPorts.size();
        try {

            if (count == 1) {
                Object a = inputPorts.get(0).getData();
//                result = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs3(a);
//                resultRef[0] = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs(a);
                resultRef[0] = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs3(a);

            } else if (count == 2) {
                Object a = inputPorts.get(0).getData();
                Object b = inputPorts.get(1).getData();
//                result = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs3(a, b);
//                resultRef[0] = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs(a, b);
                resultRef[0] = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs3(a, b);

            } else if (count == 3) {
                Object a = inputPorts.get(0).getData();
                Object b = inputPorts.get(1).getData();
                Object c = inputPorts.get(2).getData();
//                result = invokeMethodArgs3(a, b, c);
//                resultRef[0] = invokeMethodArgs(a, b, c);
                resultRef[0] = invokeMethodArgs3(a, b, c);
                // ToDo

            }
        } catch (Exception e) {
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            exceptionRef[0] = (e.getCause() != null) ? e.getCause() : e;
            Platform.runLater(() -> {
                BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(),
                        ExceptionPanel.Severity.ERROR, exceptionRef[0]);
                exceptions.add(exception);
            });

//            BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(), ExceptionPanel.Severity.ERROR, throwable);
//            exceptions.add(exception);
        }

        if (isListReturnType && resultRef[0] != null) {
            List<?> list = (List<?>) resultRef[0];
            determineOutPortDataTypeFromList(list);
        }

        if (resultRef[0] instanceof Result r) {
            Platform.runLater(() -> outputPorts.get(0).setData(r.result));

        } else {
            Platform.runLater(() -> outputPorts.get(0).setData(resultRef[0]));

        }

        return resultRef[0];

        // Process list return type if needed
//        if (isListReturnType && result != null) {
//            List<?> list = (List) result;
//            determineOutPortDataTypeFromList(list);
//        }
//        outputPorts.get(0).setData(result);
    }

    private void determineOutPortDataTypeFromList(List<?> list) {
        Set<Class<?>> classes = new HashSet<>();
        for (Object i : list) {
            if (i != null) {
                classes.add(i.getClass());
            }
        }
        if (classes.size() == 1) {
            PortModel port = this.outputPorts.get(0);
            Class<?> type = classes.iterator().next();
            port.dataTypeProperty().set(type);
            port.nameProperty().set(type.getSimpleName());
        }
    }

    private Object invokeListMethodArgs1(Object a) throws Exception {
        return method.invoke(null, a);
    }

    private Object invokeListMethodArgs2(Object a, Object b) throws Exception {
        // both objects are single values
        if (!ListUtils.isList(b)) {
            try {
                Object result = method.invoke(null, a, b);
                return result;
            } catch (IllegalAccessException | InvocationTargetException ex) {
                return null;
            }
        }

        // object b is a list
        return laceListArgs2(a, (List<?>) b);
    }

    private Object laceListArgs2(Object a, List<?> bList) throws Exception {
        List<Object> list = new ArrayList<>();
        for (Object b : bList) {
            Object result = invokeListMethodArgs2(a, b);
            list.add(result);
        }
        return list;
    }

    private Result invokeMethodArgs3(Object... parameters) {

        int listCount = getListCount(parameters);
//        System.out.println(listCount + " " + inputPorts.size());

        if (listCount == 0) { // none are list - invoke method
            Result r = new Result();

            try {
                r.result = method.invoke(null, parameters);
                return r;

            } catch (Exception e) {

                Throwable throwable = e;
                if (e.getCause() != null) {
                    System.out.println("invokeMethodArgs3 EXCEPTION CLASS " + e.getCause().toString());
                    throwable = e.getCause();
                }

                BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(), ExceptionPanel.Severity.ERROR, throwable);
                r.exs.add(exception);
                return r;
            }

        } else if (listCount == inputPorts.size()) { // all are lists - loop and recurse
//        } else if (listCount == parameters.length) { // all are lists - loop and recurse
            long shortestListSize = getShortestListSize(parameters);
            Result r = new Result();
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < shortestListSize; i++) {
                traversalLog.add(i);
                Object[] array = new Object[parameters.length];
                for (int j = 0; j < parameters.length; j++) {
                    List<?> p = (List<?>) parameters[j];
                    Object item = p.get(i);
                    array[j] = item;
                }
                Result result = invokeMethodArgs3(array);
                list.add(result.result);
                r.exs.addAll(result.exs);
                r.result = list;
                traversalLog.pop();
            }
            return r;

        } else { // some are list, some are not - make all lists and recurse
            long shortestListSize = getShortestListSize(parameters);
            if (shortestListSize == 0) {
                return null;
            }

            for (int i = 0; i < shortestListSize; i++) {
                Object p = parameters[i];
                if (ListUtils.isList(p)) {
                    continue;
                }
                List<Object> list = new ArrayList<>();
                for (int j = 0; j < shortestListSize; j++) {
                    list.add(p);
                }
                parameters[i] = list;
            }
            Result result = invokeMethodArgs3(parameters);
            return result;
        }
    }

    private Object invokeMethodArgs(Object... parameters) {

        int listCount = getListCount(parameters);
//        System.out.println(listCount + " " + inputPorts.size());

        if (listCount == 0) { // none are list - invoke method
            try {
                Object result = method.invoke(null, parameters);
                return result;
            } catch (Exception e) {

//                Throwable throwable = e;
//                if (e.getCause() != null) {
//                    throwable = e.getCause();
//                }
//
//                BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(), ExceptionPanel.Severity.ERROR, throwable);
//                exceptions.add(exception);
                final Throwable[] exceptionRef = {null};
                exceptionRef[0] = (e.getCause() != null) ? e.getCause() : e;
                Platform.runLater(() -> {
                    BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(),
                            ExceptionPanel.Severity.ERROR, exceptionRef[0]);
                    exceptions.add(exception);
                });

//                System.out.println("EXCEPTION CLASS " + e.getClass().toString());
                if (e.getCause() != null) {
                    System.out.println("invokeMethodArgs3 EXCEPTION CLASS " + e.getCause().toString());

//                    System.out.println(currentprocess + " invokeMethodArgs3 EXCEPTION CLASS " + e.getCause().toString());
//                    System.out.println();
                }

                return null;
            }

        } else if (listCount == inputPorts.size()) { // all are lists - loop and recurse
//        } else if (listCount == parameters.length) { // all are lists - loop and recurse
            long shortestListSize = getShortestListSize(parameters);
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < shortestListSize; i++) {
                traversalLog.add(i);
                Object[] array = new Object[parameters.length];
                for (int j = 0; j < parameters.length; j++) {
                    List<?> p = (List<?>) parameters[j];
                    Object item = p.get(i);
                    array[j] = item;
                }
                Object result = invokeMethodArgs(array);
                list.add(result);
                traversalLog.pop();
            }
            return list;

        } else { // some are list, some are not - make all lists and recurse
            long shortestListSize = getShortestListSize(parameters);
            if (shortestListSize == 0) {
                return null;
            }

            for (int i = 0; i < shortestListSize; i++) {
                Object p = parameters[i];
                if (ListUtils.isList(p)) {
                    continue;
                }
                List<Object> list = new ArrayList<>();
                for (int j = 0; j < shortestListSize; j++) {
                    list.add(p);
                }
                parameters[i] = list;
            }
            Object result = invokeMethodArgs(parameters);
            return result;
        }
    }

    private String getExceptionIndex() {
        if (traversalLog.isEmpty()) {
            return null;
        }
        String result = "";
        for (Integer index : traversalLog) {
            result += "[" + index + "]";
        }
        return result;
    }

    private int getListCount(Object... parameters) {
        int result = 0;
        for (Object p : parameters) {
            if (ListUtils.isList(p)) {
                result++;
            }
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

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.setType(method.getAnnotation(BlockMetadata.class).identifier());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
    }

    @Override
    public BlockModel copy() {
        MethodBlock block = BlockFactory.createBlockFromMethod(method);
        return block;
    }

    @Override
    public BlockMetadata getMetadata() {
        BlockMetadata metadata = method.getAnnotation(BlockMetadata.class);
        return metadata;
    }

    @Override
    protected void onRemoved() {
        if (container != null) {
            container.prefWidthProperty().unbind();
        }
    }

    enum LacingMode {
        SHORTEST,
        LONGEST,
        CROSS_PRODUCT
    }

    private class Result {

        public Object result = null;
        public List<BlockException> exs = new ArrayList<>();
    }

}
