package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        name = "List.Sort",
        category = "List",
        description = "Sort the list",
        tags = {"list", "sort"})
public class Sort extends Hub {

    public Sort(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("list", Object.class);

        addOutPortToHub("obj", Object.class);

        Label label = getAwesomeIcon(IconType.FA_SORT_NUMERIC_ASC);
        
        addControlToHub(label);
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        //Get incoming data
        Object raw = inPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).name = "obj";
            return;
        }

        //Process incoming data
        if (raw instanceof List) {

            List source = (List) raw;

            //Example code to handle collections
            List target = new ArrayList();
            target.addAll(source);
            Collections.sort(target);

            //Set outgoing data
            outPorts.get(0).setData(target);

            //Set data type corresponding to source
            outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            outPorts.get(0).name = inPorts.get(0).connectedConnections.get(0).getStartPort().name;
        }
    }

    @Override
    public Hub clone() {
        Sort hub = new Sort(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
