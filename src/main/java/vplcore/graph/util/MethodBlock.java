package vplcore.graph.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import jo.vpl.xml.BlockTag;
import vplcore.IconType;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
import vplcore.workspace.WorkspaceModel;
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
    public String identifier;
    public String category;
    public String description;
    public String[] tags;
    public Method method;

    public MethodBlock(WorkspaceModel workspace, Method method) {
        super(workspace);

        this.info = method.getAnnotation(BlockMetadata.class);
        this.identifier = info.identifier();
        this.category = info.category();
        this.description = info.description();
        this.tags = info.tags();
        this.method = method;
    }

    @Override
    protected void initialize() {

    }

    @Override
    public Region getCustomization() {
        Label label;
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
        return label;
    }

    public boolean isListOperator = false;
    public boolean isListReturnType = false;
    private final Deque<Integer> traversalLog = new ArrayDeque<>(); // keep track which index of the list is currently being processed

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void process() {

        traversalLog.clear();
        Object result = null;
        int count = inputPorts.size();
        try {

            if (count == 1) {
                Object a = inputPorts.get(0).getData();
//                result = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs1(a);
                result = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs3(a);

            } else if (count == 2) {
                Object a = inputPorts.get(0).getData();
                Object b = inputPorts.get(1).getData();
//                result = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs2(a, b);
                result = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs3(a, b);

            } else if (count == 3) {
                Object a = inputPorts.get(0).getData();
                Object b = inputPorts.get(1).getData();
                Object c = inputPorts.get(2).getData();
                result = invokeMethodArgs3(a, b, c);
                // ToDo

            }
        } catch (Exception e) {
            Throwable throwable = e;
            if (e.getCause() != null) {
                throwable = e.getCause();
            }
            BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(), ExceptionPanel.Severity.ERROR, throwable);
            exceptions.add(exception);
        }

        if (isListReturnType && result != null) {
            List<?> list = (List) result;
            determineOutPortDataTypeFromList(list);
        }

        outputPorts.get(0).setData(result);
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

    private Object invokeMethodArgs3(Object... parameters) {

        int listCount = getListCount(parameters);
//        System.out.println(listCount + " " + inputPorts.size());

        if (listCount == 0) { // none are list - invoke method
            try {
                Object result = method.invoke(null, parameters);
                return result;
            } catch (Exception e) {

                Throwable throwable = e;
                if (e.getCause() != null) {
                    throwable = e.getCause();
                }
                BlockException exception = new ExceptionPanel.BlockException(getExceptionIndex(), ExceptionPanel.Severity.ERROR, throwable);
                exceptions.add(exception);

                System.out.println("EXCEPTION CLASS " + e.getClass().toString());
                if (e.getCause() != null) {
                    System.out.println("EXCEPTION CLASS " + e.getCause().toString());
                    System.out.println();

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
                Object result = invokeMethodArgs3(array);
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
            Object result = invokeMethodArgs3(parameters);
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
        MethodBlock block = BlockFactory.createBlockFromMethod(method, workspace);
        return block;
    }

    @Override
    public BlockMetadata getMetadata() {
        BlockMetadata metadata = method.getAnnotation(BlockMetadata.class);
        return metadata;
    }

    @Override
    protected void onRemoved() {

    }

    enum LacingMode {
        SHORTEST,
        LONGEST,
        CROSS_PRODUCT
    }

}

//    private Object invokeMethodArgs1(Object a) {
//
//        // object a is a single value
//        if (!isList(a)) {
//            try {
//                return method.invoke(null, a);
//            } catch (IllegalAccessException | InvocationTargetException ex) {
//
////                Logger.getLogger(ReflectionBlock.class.getName()).log(Level.SEVERE, null, ex);
////                System.out.println("TEST ARGS 1");
//                return null;
//            }
//        }
//
//        // object a is a list
//        List<?> aList = (List<?>) a;
//        List<Object> list = new ArrayList<>();
//
//        for (Object ai : aList) {
//            Object result = invokeMethodArgs1(ai);
//            list.add(result);
//        }
//        return list;
//    }
//
////    Class returnType = null;
////    Set<Class<?>> actualReturnType = new HashSet<>();
//    private Object invokeMethodArgs2(Object a, Object b) {
//
//        // both objects are single values
//        if (!isList(a) && !isList(b)) {
//            try {
//                Object result = method.invoke(null, a, b);
////                actualReturnType.add(result.getClass());
//                return result;
//            } catch (IllegalAccessException | InvocationTargetException ex) {
////                Logger.getLogger(ReflectionBlock.class.getName()).log(Level.SEVERE, null, ex);
////                System.out.println("TEST ARGS 2");
//                return null;
//            }
//        }
//
//        // only object b is a list
//        if (!isList(a)) {
//            return laceArgs2(a, (List<?>) b);
//        }
//
//        // only object a is a list
//        if (!isList(b)) {
//            return laceArgs2((List<?>) a, b);
//        }
//
//        // both objects are lists
//        List<?> aList = (List<?>) a;
//        List<?> bList = (List<?>) b;
//
//        int size = aList.size() < bList.size() ? aList.size() : bList.size();
//        List<Object> list = new ArrayList<>();
//
//        for (int i = 0; i < size; i++) {
//            Object ai = aList.get(i);
//            Object bi = bList.get(i);
//            Object result = invokeMethodArgs2(ai, bi);
//            list.add(result);
//        }
//        return list;
//    }
//
//    private Object laceArgs2(Object a, List<?> bList) {
//        List<Object> list = new ArrayList<>();
//        for (Object b : bList) {
//            Object result = invokeMethodArgs2(a, b);
//            list.add(result);
//        }
//        return list;
//    }
//
//    private Object laceArgs2(List<?> aList, Object b) {
//        List<Object> list = new ArrayList<>();
//        for (Object a : aList) {
//            Object result = invokeMethodArgs2(a, b);
//            list.add(result);
//        }
//        return list;
//    }
