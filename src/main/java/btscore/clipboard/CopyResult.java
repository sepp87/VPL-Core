package btscore.clipboard;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class CopyResult {

    public WorkspaceController workspaceController;
    public Bounds boundingBox;

    public List<BlockModel> blockModels = new ArrayList<>();    
    public List<ConnectionModel> connectionModels = new ArrayList<>();

    public CopyResult() {

    }
}
