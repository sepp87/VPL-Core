package jo.vpl.hub.math;

import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
	name = "Math.Multiply",  
        category = "Math",
        description = "Multiply X by Y",
	tags = {"math","multiply"})
public class Multiply extends Hub {

    public Multiply(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("Mul");
        
        addInPortToHub("Value1", double.class);
        addInPortToHub("Value2", double.class);

        addOutPortToHub("Value", double.class);

        Label label = new Label("X");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    /**
     * Calculate X*Y
     */
    @Override
    public void calculate() {


        if (inPorts.get(0).getData() != null && inPorts.get(1).getData() != null) {
            Double d = Double.parseDouble(inPorts.get(0).getData().toString())
                    * Double.parseDouble(inPorts.get(1).getData().toString());

            outPorts.get(0).setData(d);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new Multiply(hostCanvas);
        return hub;
    }
}
