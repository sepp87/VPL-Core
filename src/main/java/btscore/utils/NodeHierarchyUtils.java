package btscore.utils;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Joost
 */
public class NodeHierarchyUtils {

    public static boolean isPickedNodeOrParentOfType(MouseEvent event, Class<?> type) {
        Node node = event.getPickResult().getIntersectedNode();
        return isNodeOrParentOfType(node, type);
    }

    /**
     * Check if the node of the same type or if it is embedded in the type
     *
     * @param node the node to check
     * @param type the type of node to check against
     * @return
     */
    public static boolean isNodeOrParentOfType(Node node, Class<?> type) {
        if (node == null) {
            return false;
        }

        if (type.isAssignableFrom(node.getClass())) {
            return true;
        } else {
            Node parent = node.getParent();
            return isNodeOrParentOfType(parent, type);
        }
    }
}
