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
	name = "Math.Abs",  
        category = "Math",
        description = "Get the absolute value of X",
	tags = {"math","abs"})
public class Abs extends Hub {

    public Abs(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("Abs");

        addInPortToHub("Value1", double.class);

        addOutPortToHub("Value", double.class);

        Label label = new Label("|X|");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    /**
     * Calculate |X|
     */
    @Override
    public void calculate() {

        if (inPorts.get(0).getData() != null) {
            Double in = Double.parseDouble(inPorts.get(0).getData().toString());
            Double out = Math.abs(in);
            
            outPorts.get(0).setData(out);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new Abs(hostCanvas);
        return hub;
    }
}
