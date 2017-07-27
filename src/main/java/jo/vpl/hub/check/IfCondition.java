package jo.vpl.hub.check;

import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Check.If",
        category = "Check",
        description = "A boolean statement",
        tags = {"check", "if", "condition"})
public class IfCondition extends Hub {

    static String[] operators = {">",">=","==", "!=", "<=", "<"};
    
    public IfCondition(VplControl hostCanvas) {
        super(hostCanvas);

        setName(">");

        addInPortToHub("Number : A", Number.class);
        addInPortToHub("Number : B", Number.class);

        addOutPortToHub("Boolean : Result", Boolean.class);

        Label label = new Label("A > B");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    /**
     * Calculate X+Y
     */
    @Override
    public void calculate() {

        //Get data
        Object raw1 = inPorts.get(0).getData();
        Object raw2 = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw1 == null || raw2 == null) {
            outPorts.get(0).setData(null);
            return;
        }

        if (raw1 instanceof List || raw2 instanceof List) {
            //not yet supported
            outPorts.get(0).setData(null);
            
        } else {
            //Does this 
            boolean result = ((Number) raw1).doubleValue() > ((Number) raw2).doubleValue();
            outPorts.get(0).setData(result);
        }
    }

    @Override
    public Hub clone() {
        Hub hub = new IfCondition(hostCanvas);
        return hub;
    }
}
