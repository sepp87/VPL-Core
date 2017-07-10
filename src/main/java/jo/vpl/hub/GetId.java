package jo.vpl.hub;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.Node;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Util.GetId",
        category = "General",
        description = "Get the Id of this object.",
        tags = {"util", "id", "general"})
public class GetId extends Hub {

    public GetId(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Id");

        addInPortToHub("node", Node.class);

        addOutPortToHub("str", String.class);

        Label label = getAwesomeIcon(IconType.FA_BARCODE);
        addControlToHub(label);
    }

    /**
     * Get the Id's
     */
    @Override
    public void calculate() {

        //Get controls and data
        Object raw = inPorts.get(0).getData();

        if (raw == null) {
            return;
        }

        //Process geomData
        if (raw instanceof List) {
            List list = (List) raw;
            List<Node> nodes = (List<Node>) list;

            List<String> ids = nodes.stream()
                    .map(e -> e.getId())
                    .collect(Collectors.toCollection(ArrayList<String>::new));

            outPorts.get(0).setData(ids);

        } else {
            String id = ((Node) raw).getId();
            outPorts.get(0).setData(id);
        }

    }

    @Override
    public Hub clone() {
        Hub hub = new GetId(hostCanvas);
        return hub;
    }
}
