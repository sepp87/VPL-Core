package vplcore.util;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import vplcore.editor.EditorView;
import vplcore.graph.group.BlockGroupView;
import vplcore.workspace.WorkspaceView;

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
