package jo.vpl.hub;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jo.vpl.core.Hub;
import jo.vpl.core.Workspace;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        identifier = "Core.ReflectionHub",
        category = "Core",
        description = "A generic hub used to convert static methods and fields to hubs",
        tags = {"core", "reflection", "hub"})
public class ReflectionHub extends Hub {

    public final String category;
    public final String description;
    public final String[] tags;
    public final Method method;

    public ReflectionHub(Workspace hostCanvas, String name, String category, String description, String[] tags) {
        this(hostCanvas, name, category, description, tags, null);
    }

    public ReflectionHub(Workspace hostCanvas, String name, String category, String description, String[] tags, Method method) {
        super(hostCanvas);
        setName(name);
        this.category = category;
        this.description = description;
        this.tags = tags;
        this.method = method;
    }

    /**
     * Function to handle data when a connection is added and before calculate
     * is called
     */
    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        //Sample code for handling just specific ports
        int index = inPorts.indexOf(source);
        if (index == 0) {

        }
    }

    /**
     * Function to handle data when a connection is removed
     */
    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        //Sample code for handling just specific ports
        int index = inPorts.indexOf(source);
        if (index == 0) {

        }
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        Object result = null;

        int count = inPorts.size();
        if (count == 1) {
            Object a = inPorts.get(0).getData();
            result = invokeMethodArgs1(a);

        } else if (count == 2) {
            Object a = inPorts.get(0).getData();
            Object b = inPorts.get(1).getData();
            result = invokeMethodArgs2(a, b);

        } else if (count == 3) {
            // ToDo
            
        }
        outPorts.get(0).setData(result);
    }

    private Object invokeMethodArgs1(Object a) {

        // object a is a single value
        if (!List.class.isAssignableFrom(a.getClass())) {
            try {
                return method.invoke(null, a);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(ReflectionHub.class.getName()).log(Level.SEVERE, null, ex);
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

    private Object invokeMethodArgs2(Object a, Object b) {

        // both objects are single values
        if (!List.class.isAssignableFrom(a.getClass()) && !List.class.isAssignableFrom(b.getClass())) {
            try {
                return method.invoke(null, a, b);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(ReflectionHub.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // only object b is a list
        if (!List.class.isAssignableFrom(a.getClass())) {
            return laceArgs2(a, (List<?>) b);
        }

        // only object a is a list
        if (!List.class.isAssignableFrom(b.getClass())) {
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
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("key"), "value");
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        //Specify further initialization statements here
        this.calculate();
    }

    @Override
    public Hub clone() {
        TemplateHub hub = new TemplateHub(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

enum LacingMode {
    SHORTEST,
    LONGEST,
    CROSS_PRODUCT
}
