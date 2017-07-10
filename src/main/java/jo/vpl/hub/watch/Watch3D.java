package jo.vpl.hub.watch;

import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import jo.vpl.watch3D.ObjViewer;
import java.util.List;
import javafx.scene.Group;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Util.Watch3D",
        category = "View",
        description = "View 3D geometry",
        tags = {"view", "3D"})
public class Watch3D extends Hub {

    /**
     * A hub that embeds an external Obj viewer class
     *
     * @param hostCanvas
     */
    public Watch3D(VplControl hostCanvas) {
        super(hostCanvas);
        setResizable(true);

        setName("Watch3D");

        mainContentGrid.setPrefWidth(320);
        mainContentGrid.setPrefHeight(240);
        addInPortToHub("group", Group.class);
        addOutPortToHub("group", Group.class);

        ObjViewer viewer = new ObjViewer(mainContentGrid);

        addControlToHub(viewer);
    }

    @Override
    public void calculate() {
        //Get controls and data
        ObjViewer viewer = (ObjViewer) controls.get(0);
        Object geomData = inPorts.get(0).getData();
        List<Group> groups = null;

        //Process geomData
        if (geomData != null && geomData instanceof List) {
            List list = (List) geomData;
            if (list.get(0) instanceof Group) {
                groups = (List<Group>) list;
            }
        }

        //Do action add geometry
        viewer.clearGeometry();
        if (groups != null) {
            for (Group group : groups) {
                viewer.addGeometry(group);
            }
        }

        //Set data
        outPorts.get(0).setData(groups);
    }

    @Override
    public Hub clone() {
        Hub hub = new Watch3D(hostCanvas);
        return hub;
    }

}
