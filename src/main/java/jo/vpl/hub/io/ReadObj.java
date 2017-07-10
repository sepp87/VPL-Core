package jo.vpl.hub.io;

import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import jo.vpl.watch3D.ObjParser;
import java.io.File;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "IO.ReadObj",
        category = "io",
        description = "Read a Wavefront .obj file",
        tags = {"read", "obj", "parse"}
)
public class ReadObj extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public ReadObj(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Obj");

        addInPortToHub("file", File.class);
        addOutPortToHub("group", Group.class);

        Label label = getAwesomeIcon(IconType.FA_COGS);

        addControlToHub(label);
    }

    @Override
    public void calculate() {
        //Get controls and data
        File file = (File) inPorts.get(0).getData();
        List<Group> groups = null;


        //Do action
        if (file != null && file.exists() && file.isFile() && file.getPath().endsWith(".obj")) {
            groups = ObjParser.parseObj(file);
        }

        //Set data
        outPorts.get(0).setData(groups);
    }

    @Override
    public Hub clone() {
        Hub hub = new ReadObj(hostCanvas);
        return hub;
    }

}
