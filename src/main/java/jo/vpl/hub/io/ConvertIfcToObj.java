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
        name = "IO.ConvertIfcToCsv",
        category = "io",
        description = "Convert Ifc to Obj",
        tags = {"io", "ifc", "obj", "convert", "read", "parse"})
public class ConvertIfcToObj extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public ConvertIfcToObj(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Ifc to Obj");

        addInPortToHub("file", File.class);
        addOutPortToHub("file", File.class);

        Label label = new Label("Ifc>Obj");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    @Override
    public void calculate() {
        //Get controls and data
        File file = (File) inPorts.get(0).getData();
        File obj = null;

        //Do action
        if (file != null && file.exists() && file.isFile() && file.getPath().endsWith(".ifc")) {

        }

        //Set data
        outPorts.get(0).setData(obj);

    }

    @Override
    public Hub clone() {
        Hub hub = new ConvertIfcToObj(hostCanvas);
        return hub;
    }

}
