package jo.vpl.hub.parse;

import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
import java.io.File;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "File.ParseIFC",
        category = "Geometry",
        description = "View 3D geometry",
        tags = {"view", "3D"})
public class ParseIFC extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public ParseIFC(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("Read .ifc");

        addInPortToHub("file", File.class);
        addOutPortToHub("file", File.class);

        Label label = new Label(".ifc");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    @Override
    public void calculate() {
        //Get controls and data

        //Do action
        //Set data
    }

    @Override
    public Hub clone() {
        Hub hub = new ParseIFC(hostCanvas);
        return hub;
    }

}
