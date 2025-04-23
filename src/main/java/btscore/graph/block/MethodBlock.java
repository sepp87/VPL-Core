package btscore.graph.block;

import btscore.icons.FontAwesomeSolid;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import btsxml.BlockTag;
import btscore.graph.port.PortModel;
import btscore.graph.block.ExceptionPanel.BlockException;
import btscore.utils.ListUtils;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Core.methodBlock",
        category = "Core",
        description = "A generic block used to convert static methods and fields to blocks",
        tags = {"core", "method", "block"})
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

        if (!info.icon().equals(FontAwesomeSolid.NULL)) {
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
    private final List<Thread> threads = new ArrayList<>();

    @Override
    public void processSafely() {

//        for (Thread thread : threads) {
//            thread.interrupt();
//        }
//        threads.clear();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                process();
                return null;
            }
        };

        if (container != null && label.getWidth() != 0.0) {
            task.setOnSucceeded(event -> {
//            task.setOnSucceeded(event -> Platform.runLater(() -> {
                container.getChildren().clear();
                container.getChildren().add(label);
//            }));
            });
        }

        if (container != null && label.getWidth() != 0.0) {
            spinner.setMinWidth(label.getWidth());
            container.getChildren().clear();
            container.getChildren().add(spinner);
        }

        // Run the task in a separate thread
        Thread thread = new Thread(task);
//        threads.add(thread);
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void process() {

        final InvocationResult[] result = {null}; // Use an array instead of AtomicReference

        Deque<Integer> traversalLog = new ArrayDeque<>(); // keep track which index of the list is currently being processed
        try {
            int count = inputPorts.size();
            switch (count) {
                case 0 -> {
                    result[0] = invokeMethodArgs(traversalLog);
                }
                case 1 -> {
                    Object a = inputPorts.get(0).getData();
                    result[0] = isListOperator ? invokeListMethodArgs(traversalLog, a) : invokeMethodArgs(traversalLog, a);
                }
                case 2 -> {
                    Object a = inputPorts.get(0).getData();
                    Object b = inputPorts.get(1).getData();
                    result[0] = isListOperator ? invokeListMethodArgs2(traversalLog, a, b) : invokeMethodArgs(traversalLog, a, b);
                }
                case 3 -> {
                    Object a = inputPorts.get(0).getData();
                    Object b = inputPorts.get(1).getData();
                    Object c = inputPorts.get(2).getData();
                    result[0] = invokeMethodArgs(traversalLog, a, b, c);
                    // ToDo
                }
                default -> { // Show an error when there are more than 3 ports
                    InvocationResult fallback = new InvocationResult();
                    BlockException exception = new ExceptionPanel.BlockException(null, ExceptionPanel.Severity.ERROR, new IndexOutOfBoundsException("No more than 3 input ports are supported for the moment."));
                    fallback.exceptions().add(exception);
                    result[0] = fallback;
                }
            }
        } catch (Exception e) {
            Logger.getLogger(MethodBlock.class.getName()).log(Level.SEVERE, null, e);
        }

        if (isListReturnType && result[0].data().get() != null) {
            List<?> list = (List<?>) result[0].data().get();
            determineOutPortDataTypeFromList(list);
        }

        Platform.runLater(() -> {
            int size = exceptions.size();
            exceptions.addAll(result[0].exceptions);
            exceptions.remove(0, size);
            if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
                exceptions.clear();
            }
            Object data = result[0].data().get();
            outputPorts.get(0).setData(data);
            if((data != null) && !List.class.isAssignableFrom(data.getClass())) {
                outputPorts.get(0).dataTypeProperty().set(data.getClass());
            }
         
        });

    }

    private void determineOutPortDataTypeFromList(List<?> list) {
        Set<Class<?>> classes = new HashSet<>();
        for (Object i : list) {
            if (i != null) {
                classes.add(i.getClass());
            }
        }
        if (classes.size() == 1) {
            Platform.runLater(() -> {
                PortModel port = this.outputPorts.get(0);
                Class<?> type = classes.iterator().next();
                port.dataTypeProperty().set(type);
                port.nameProperty().set(type.getSimpleName());
            });
        }
    }

    private InvocationResult invokeListMethodArgs2(Deque<Integer> traversalLog, Object a, Object b) {

        // both objects are single values
        if (!ListUtils.isList(b)) {
            return invokeListMethodArgs(traversalLog, a, b);

        } else { // object b is a list
            List<?> bList = (List<?>) b;
            List<Object> list = new ArrayList<>();
            InvocationResult invocationResult = new InvocationResult();
            invocationResult.data().set(list);

            int i = 0;
            for (Object bItem : bList) {
                traversalLog.add(i);
                InvocationResult result = invokeListMethodArgs2(traversalLog, a, bItem);
                list.add(result.data().get());
                invocationResult.exceptions().addAll(result.exceptions());
                traversalLog.pop();
            }

            return invocationResult;
        }
    }

    private InvocationResult invokeListMethodArgs(Deque<Integer> traversalLog, Object... parameters) {
        InvocationResult invocationResult = new InvocationResult();
        try {
            Object result = method.invoke(null, parameters);
            invocationResult.data().set(result);

        } catch (Exception e) {
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(traversalLog), ExceptionPanel.Severity.ERROR, throwable);
            invocationResult.exceptions.add(exception);
        }
        return invocationResult;
    }

    private InvocationResult invokeMethodArgs(Deque<Integer> traversalLog, Object... parameters) {
        int listCount = getListCount(parameters);

        if (listCount == 0) { // none are list - invoke method
            InvocationResult invocationResult = new InvocationResult();

            try {
                Object result = method.invoke(null, parameters);
                invocationResult.data().set(result);

            } catch (Exception e) {
                Throwable throwable = e;
                if (e.getCause() != null) {
                    throwable = e.getCause();
                }
                BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(traversalLog), ExceptionPanel.Severity.ERROR, throwable);
                invocationResult.exceptions.add(exception);
            }
            return invocationResult;

        } else if (listCount == inputPorts.size()) { // all are lists - loop and recurse

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
                list.add(subResult.data);

                traversalLog.pop();
            }
            return invocationResult;

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

    public record InvocationResult(
            ObjectProperty data,
            List<BlockException> exceptions) {

        public InvocationResult() {
            this(new SimpleObjectProperty(), new ArrayList<>());
        }
    }

}
