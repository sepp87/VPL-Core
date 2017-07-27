package jo.vpl.hub.check;

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
        name = "Check.Then",
        category = "Check",
        description = "Activate hub when condition is true",
        tags = {"check", "if", "condition"})
public class ThenCondition extends Hub {

    static String[] operators = {">", ">=", "==", "!=", "<=", "<"};

    public ThenCondition(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Then");

        addInPortToHub("Boolean : Condition", Boolean.class);
        addInPortToHub("Object", Object.class);

        addOutPortToHub("Object", Object.class);

        Label label = new Label("Then");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        int index = inPorts.indexOf(source);
        if (index == 1) {
            //Set data type corresponding to incoming
            outPorts.get(0).dataType = incoming.dataType;
            outPorts.get(0).setName(incoming.getName());
        }
    }

    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        int index = inPorts.indexOf(source);
        if (index == 1) {
            //Reset data type to initial state
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).setName("Object");
        }
    }

    @Override
    public void calculate() {

        //Get data
        Object raw0 = inPorts.get(0).getData();
        Object raw1 = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw0 == null || raw1 == null) {
            outPorts.get(0).setData(null);
            return;
        }

        if (raw0 instanceof List || raw1 instanceof List) {
            //not yet supported
            outPorts.get(0).setData(null);
        } else {
            //Does this 
            boolean isTrue = (Boolean) raw0;
            if (isTrue) {
                outPorts.get(0).setData(raw1);
            } else {
                outPorts.get(0).setData(null);
            }
        }
    }

    @Override
    public Hub clone() {
        Hub hub = new ThenCondition(hostCanvas);
        return hub;
    }
}
