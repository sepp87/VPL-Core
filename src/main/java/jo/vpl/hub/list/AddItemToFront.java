package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.AddItemToFront",
        category = "List",
        description = "Add an item to the front of a list",
        tags = {"list", "add"})
public class AddItemToFront extends Hub {

    public AddItemToFront(VplControl hostCanvas) {
        super(hostCanvas);

        setName("+");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("Object : List", Object.class);
        addInPortToHub("Object : Item", Object.class);

        addOutPortToHub("Object : List", Object.class);

        Label label = new Label("+...");
        label.getStyleClass().add("hub-text");
        addControlToHub(label);
    }

    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Set data type corresponding to incoming
            outPorts.get(0).dataType = incoming.dataType;
            outPorts.get(0).setName(incoming.getName());
        }
    }

    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Reset data type to initial state
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).setName("Object : List");
        }
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {
        //Get incoming data
        Object raw = inPorts.get(0).getData();
        Object item = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).setData(null);
            return;
        }

        //Check if all incoming data is in the correct format
        boolean hasError = false;
        if (!(raw instanceof List)) {
            System.out.println("This is not a list");
            hasError = true;
        }
        if (item instanceof List) {
            System.out.println("Just one item can be added at a time");
            hasError = true;
        }
        if (inPorts.get(1).connectedConnections.size() > 0
                && inPorts.get(0).connectedConnections.size() > 0) {
            Class listDataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            Class itemDataType = inPorts.get(1).connectedConnections.get(0).getStartPort().dataType;
            if (itemDataType.isAssignableFrom(listDataType)) {
                System.out.println("Element is not of same type as the list's");
                hasError = true;
            }
        }
        if (hasError) {
            return;
        }

        //Add item to end
        List source = (List) raw;

        //Create a new list when autoCheckBox is checked, otherwise add to existing 
        //HACK new list is created that is identical because otherwise data is not forwarded
        //When isClicked, check changes to empty circle
        if (this.autoCheckBox.isClicked()) {
            source.add(item);
            List target = new ArrayList();
            target.addAll(source);

            //Set outgoing data
            outPorts.get(0).setData(target);

        } else {
            List target = new ArrayList();
            target.addAll(source);
            target.add(item);

            //Set outgoing data
            outPorts.get(0).setData(target);
        }
    }

    @Override
    public Hub clone() {
        AddItemToFront hub = new AddItemToFront(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
