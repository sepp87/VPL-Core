package vplcore.graph.util;

import vplcore.graph.model.BlockInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import jo.vpl.xml.BlockTag;
import vplcore.IconType;
import vplcore.workspace.BlockModel;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockInfo(
        identifier = "Core.reflectionBlock",
        category = "Core",
        description = "A generic block used to convert static methods and fields to blocks",
        tags = {"core", "reflection", "block"})
public class MethodBlockModel extends BlockModel {

    private final BlockInfo info;
    public String identifier;
    public String category;
    public String description;
    public String[] tags;
    public Method method;

    public MethodBlockModel(WorkspaceModel workspace, Method method) {
        super(workspace);

        this.info = method.getAnnotation(BlockInfo.class);
        this.identifier = info.identifier();
        this.category = info.category();
        this.description = info.description();
        this.tags = info.tags();
        this.method = method;
    }

    @Override
    public Region getCustomization() {
        Label label;
        if (!info.icon().equals(IconType.NULL)) {
            label = getAwesomeIcon(info.icon());
            
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

    public static Label getAwesomeIcon(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.getStyleClass().add("block-awesome-icon");
        return label;
    }

    public boolean isListOperator = false;
    public boolean isListReturnType = false;

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void process() {

        Object result = null;
        int count = inputPorts.size();
        try {

            if (count == 1) {
                Object a = inputPorts.get(0).getData();
                result = isListOperator ? invokeListMethodArgs1(a) : invokeMethodArgs1(a);

            } else if (count == 2) {
                Object a = inputPorts.get(0).getData();
                Object b = inputPorts.get(1).getData();
                result = isListOperator ? invokeListMethodArgs2(a, b) : invokeMethodArgs2(a, b);

            } else if (count == 3) {
                // ToDo

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (isListReturnType) {
            List<?> list = (List) result;
            determineOutPortDataTypeFromList(list);
        }

        outputPorts.get(0).setData(result);
    }

    private void determineOutPortDataTypeFromList(List<?> list) {
        Set<Class<?>> classes = new HashSet<>();
        for (Object i : list) {
            classes.add(i.getClass());
        }
        if (classes.size() == 1) {
            PortModel port = this.outputPorts.get(0);
            Class<?> type = classes.iterator().next();
            port.dataType = type;
            port.setName(type.getSimpleName());
        }
    }

    private Object invokeListMethodArgs1(Object a) throws Exception {
        return method.invoke(null, a);
    }

    private Object invokeListMethodArgs2(Object a, Object b) throws Exception {
        // both objects are single values
        if (!isList(b)) {
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

    private Object invokeMethodArgs1(Object a) {

        // object a is a single value
        if (!isList(a)) {
            try {
                return method.invoke(null, a);
            } catch (IllegalAccessException | InvocationTargetException ex) {

//                Logger.getLogger(ReflectionBlock.class.getName()).log(Level.SEVERE, null, ex);
//                System.out.println("TEST ARGS 1");
                return null;
            }
        }

        // object a is a list
        List<?> aList = (List<?>) a;
        List<Object> list = new ArrayList<>();

        for (Object ai : aList) {
            Object result = invokeMethodArgs1(ai);
            list.add(result);
        }
        return list;
    }

    public boolean isList(Object o) {
        if (o == null) {
            return false;
        } else {
            return List.class.isAssignableFrom(o.getClass());
        }
    }

//    Class returnType = null;
//    Set<Class<?>> actualReturnType = new HashSet<>();
    private Object invokeMethodArgs2(Object a, Object b) {

        // both objects are single values
        if (!isList(a) && !isList(b)) {
            try {
                Object result = method.invoke(null, a, b);
//                actualReturnType.add(result.getClass());
                return result;
            } catch (IllegalAccessException | InvocationTargetException ex) {
//                Logger.getLogger(ReflectionBlock.class.getName()).log(Level.SEVERE, null, ex);
//                System.out.println("TEST ARGS 2");
                return null;
            }
        }

        // only object b is a list
        if (!isList(a)) {
            return laceArgs2(a, (List<?>) b);
        }

        // only object a is a list
        if (!isList(b)) {
            return laceArgs2(b, (List<?>) a);
        }

        // both objects are lists
        List<?> aList = (List<?>) a;
        List<?> bList = (List<?>) b;

        int size = aList.size() < bList.size() ? aList.size() : bList.size();
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Object ai = aList.get(i);
            Object bi = bList.get(i);
            Object result = invokeMethodArgs2(ai, bi);
            list.add(result);
        }
        return list;
    }

    private Object laceArgs2(Object a, List<?> bList) {
        List<Object> list = new ArrayList<>();
        for (Object b : bList) {
            Object result = invokeMethodArgs2(a, b);
            list.add(result);
        }
        return list;
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.setType(method.getAnnotation(BlockInfo.class).identifier());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
    }

    @Override
    public BlockModel copy() {
        MethodBlockModel block = BlockModelFactory.createBlockFromMethod(method, workspace);
        return block;
    }

    enum LacingMode {
        SHORTEST,
        LONGEST,
        CROSS_PRODUCT
    }

}
