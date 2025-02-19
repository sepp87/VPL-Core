package vplcore.context;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.ConnectionModel;
import vplcore.workspace.WorkspaceController;

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
