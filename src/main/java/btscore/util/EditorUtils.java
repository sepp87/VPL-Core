package btscore.util;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import btscore.editor.EditorView;
import btscore.graph.group.BlockGroupView;
import btscore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class EditorUtils {

    public static boolean onFreeSpace(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        return intersectedNode instanceof EditorView || intersectedNode instanceof WorkspaceView || intersectedNode instanceof BlockGroupView;
    }
}
