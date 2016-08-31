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
        name = "Math.Add",
        category = "Math",
        description = "Add Y to X",
        tags = {"math", "add", "plus"})
public class Add extends Hub {

    public Add(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("Add");

        addInPortToHub("Value1", double.class);
        addInPortToHub("Value2", double.class);

        addOutPortToHub("Value", double.class);

        Label label = getAwesomeIcon(IconType.FA_PLUS);
        addControlToHub(label);
    }

    /**
     * Calculate X+Y
     */
    @Override
    public void calculate() {

        /**
         * @TODO EMPTY STRINGS ARE FATAL
         */
        if (inPorts.get(0).getData() != null && inPorts.get(1).getData() != null) {
            Double d = Double.parseDouble(inPorts.get(0).getData().toString())
                    + Double.parseDouble(inPorts.get(1).getData().toString());

            outPorts.get(0).setData(d);
        } else {
            outPorts.get(0).setData(null);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new Add(hostCanvas);
        return hub;
    }
}
