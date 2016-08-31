package jo.vpl.hub.parse;

import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
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
        name = "File.ParseOBJ",
        category = "read",
        description = "Parse a Wavefront .obj file",
        tags = {"read", "obj", "parse"}
)
public class ParseOBJ extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public ParseOBJ(VPLControl hostCanvas) {
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
        Hub hub = new ParseOBJ(hostCanvas);
        return hub;
    }

}
