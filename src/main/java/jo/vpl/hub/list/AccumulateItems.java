package jo.vpl.hub.list;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
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
        name = "List.AccumulateItems",
        category = "List",
        description = "Accumulate incoming items to a list",
        tags = {"list", "add", "accumulate"})
public class AccumulateItems extends Hub {

    public AccumulateItems(VplControl hostCanvas) {
        super(hostCanvas);

        setName("i++");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("Object : Item", Object.class);
        addOutPortToHub("Object : List", Object.class);

        Label label = new Label("i++");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);

        List list = jo.vpl.core.Util.getList(Object.class);
        outPorts.get(0).setData(list);
    }

    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Set data type corresponding to incoming
            List list = jo.vpl.core.Util.getList(incoming.dataType);
            outPorts.get(0).setData(list);
            outPorts.get(0).dataType = incoming.dataType;
            outPorts.get(0).setName(incoming.dataType.getSimpleName() + " : List");
        }
    }

    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Reset data type to initial state
            List list = jo.vpl.core.Util.getList(Object.class);
            outPorts.get(0).setData(list);
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).setName("Object : List");
        }
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        if (inPorts.get(0).connectedConnections.isEmpty()) {
            return;
        }

        //Get incoming data
        Object item = inPorts.get(0).getData();
        List source = (List) outPorts.get(0).getData();

        //Return if the data type is not List but the incoming item is
        if (!List.class.isAssignableFrom(outPorts.get(0).dataType) && item instanceof List) {
            return;
        }

        //Note: Also null values are added to the list     
        List target = jo.vpl.core.Util.getList(outPorts.get(0).dataType);
        target.addAll(source);
        target.add(item);
        outPorts.get(0).setData(target);

    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outPorts.get(0).dataType.getName());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String className = xmlTag.getOtherAttributes().get(QName.valueOf("outDataType"));
        try {
            Class type;
            switch (className) {
                case "long":
                    type = long.class;
                    break;
                case "int":
                    type = int.class;
                    break;
                case "double":
                    type = double.class;
                    break;
                case "char":
                    type = char.class;
                    break;
                case "byte":
                    type = byte.class;
                    break;
                default:
                    type = Class.forName(className);

            }
            List list = jo.vpl.core.Util.getList(type);
            outPorts.get(0).setData(list);
            outPorts.get(0).dataType = type;
            outPorts.get(0).setName(type.getSimpleName() + " : List");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AccumulateItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Hub clone() {
        AccumulateItems hub = new AccumulateItems(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
