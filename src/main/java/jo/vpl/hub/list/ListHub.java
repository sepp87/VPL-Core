package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.NewList",
        category = "List",
        description = "Create a list from a single value",
        tags = {"list", "new"})
public class ListHub extends Hub {

    private List list = null;

    public ListHub(VplControl hostCanvas) {
        super(hostCanvas);

        setName("List");
        addOutPortToHub("List", Object.class);

        Label label = new Label("List");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
        
        calculate();
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {
        //Create a new empty list
        List list = new ArrayList<>();

        //Set outgoing data
        outPorts.get(0).setData(list);
    }

    @Override
    public Hub clone() {
        AddItemToEnd hub = new AddItemToEnd(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
