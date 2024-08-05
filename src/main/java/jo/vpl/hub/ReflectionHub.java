package jo.vpl.hub;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jo.vpl.core.Hub;
import jo.vpl.core.Workspace;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Core.ReflectionHub",
        category = "Core",
        description = "A generic hub used to convert static methods and fields to hubs",
        tags = {"core", "reflection", "hub"})
public class ReflectionHub extends Hub {

    public final String name;
    public final String category;
    public final String description;
    public final String[] tags;

    public ReflectionHub(Workspace hostCanvas, String name, String category, String description, String[] tags) {
        super(hostCanvas);
        this.name = name;
        this.category = category;
        this.description = description;
        this.tags = tags;
        setName(name);
    }

    public static ReflectionHub create(Field field, Workspace hostCanvas) {

        String category = field.getDeclaringClass().getSimpleName();
        String name = category + "." + field.getName();
        String description = "Variable " + field.getName();
        String[] tags = {category, field.getName(), "variable"};

        ReflectionHub hub = new ReflectionHub(hostCanvas, name, category, description, tags);

        try {
            hub.addOutPortToHub(field.getType().getSimpleName(), field.getType());
            hub.outPorts.get(0).setData(field.get(null));
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ReflectionHub.class.getName()).log(Level.SEVERE, null, ex);
        }

        hub.setName(field.getName());
        Label label = new Label(field.getName());
        hub.addControlToHub(label);

        return hub;
    }

    public static ReflectionHub create(Method method, Workspace hostCanvas) {

        String category = method.getDeclaringClass().getSimpleName();
        String name = category + "." + method.getName();
        String description = method.getName();
        String[] tags = {category, method.getName()};

        ReflectionHub hub = new ReflectionHub(hostCanvas, name, category, description, tags);

        for (Parameter p : method.getParameters()) {
            hub.addInPortToHub(p.getName(), p.getClass());
        }
        hub.addOutPortToHub(method.getReturnType().getSimpleName(), method.getReturnType());

        hub.setName(method.getName());
        Label label = new Label(method.getName());
        hub.addControlToHub(label);

        return hub;
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

        //Get incoming data
        Object raw = inPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).setData(null);
            return;
        }

        //Process incoming data
        if (raw instanceof List) {
            List<Object> nodes = (List<Object>) raw;

            //Example code to handle collections
            List<String> strList = nodes.stream()
                    .map(e -> e.toString())
                    .collect(Collectors.toCollection(ArrayList<String>::new));

            //Set outgoing data
            outPorts.get(0).setData(strList);

        } else {
            //Example code to handle a single object instance
            String str = ((Object) raw).toString();

            //Set outgoing data
            outPorts.get(0).setData(str);
        }
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
