package jo.vpl.hub.math;

import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Math.Subtract",
        category = "Math",
        description = "Subtract Y from X",
        tags = {"math", "subtract", "min"})
public class Subtract extends Hub {

    public Subtract(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("X-Y");

        addInPortToHub("Value1", double.class);
        addInPortToHub("Value2", double.class);

        addOutPortToHub("Value", double.class);

        Label label = getAwesomeIcon(IconType.FA_MINUS);

        addControlToHub(label);
    }

    /**
     * Calculate X-Y
     */
    @Override
    public void calculate() {

        if (inPorts.get(0).getData() != null && inPorts.get(1).getData() != null) {
            Double d = Double.parseDouble(inPorts.get(0).getData().toString())
                    - Double.parseDouble(inPorts.get(1).getData().toString());

            outPorts.get(0).setData(d);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new Subtract(hostCanvas);
        return hub;
    }
}
