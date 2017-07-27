package jo.vpl.hub.math;

import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Math.Abs",
        category = "Math",
        description = "Get the absolute value of A",
        tags = {"math", "abs"})
public class Abs extends Hub {

    public Abs(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Abs");

        addInPortToHub("double : A", double.class);

        addOutPortToHub("double : Result", double.class);

        Label label = new Label("|A|");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    /**
     * Calculate |X|
     */
    @Override
    public void calculate() {

        //Get data
        Object raw = inPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).setData(null);
            return;
        }

        if (raw instanceof List) {
            //not yet supported
            outPorts.get(0).setData(null);
        } else {
            double result = Math.abs((double) raw);
            outPorts.get(0).setData(result);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new Abs(hostCanvas);
        return hub;
    }
}
