package vplcore.workspace;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Joost
 */
public class WorkspaceView extends AnchorPane {

    private final Group groupLayer;
    private final Group connectionLayer;
    private final Group blockLayer;
    private final Group infoLayer;

    public WorkspaceView() {
        //Must set to (0,0) due to funky resize, otherwise messes up zoom in and out
        setMinSize(0, 0);
        setMaxSize(0, 0);

        blockLayer = new Group();
        connectionLayer = new Group();
        infoLayer = new Group();
        groupLayer = new Group();

        this.getChildren().addAll(groupLayer, connectionLayer, blockLayer, infoLayer);

        this.setStyle("-fx-background-color: green;");
    }

    public Group getGroupLayer() {
        return groupLayer;
    }

    public Group getConnectionLayer() {
        return connectionLayer;
    }

    public Group getBlockLayer() {
        return blockLayer;
    }

    public Group getInfoLayer() {
        return infoLayer;
    }

}
