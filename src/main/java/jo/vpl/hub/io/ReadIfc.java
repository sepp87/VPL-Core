package jo.vpl.hub.io;

import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import java.io.File;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "IO.ReadIfc",
        category = "io",
        description = "Read an Ifc file",
        tags = {"io","view", "3D"})
public class ReadIfc extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public ReadIfc(VplControl hostCanvas) {
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
        Hub hub = new ReadIfc(hostCanvas);
        return hub;
    }

}
