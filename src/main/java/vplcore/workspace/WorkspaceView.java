package vplcore.workspace;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Joost
 */
public class WorkspaceView extends AnchorPane {

    
    
    public WorkspaceView() {
        //Must set to (0,0) due to funky resize, otherwise messes up zoom in and out
        setMinSize(0, 0);
        setMaxSize(0, 0);

        this.setStyle("-fx-background-color: green;");
    }

 
}
