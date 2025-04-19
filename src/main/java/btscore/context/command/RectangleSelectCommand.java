package btscore.context.command;

import javafx.geometry.Point2D;
import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class RectangleSelectCommand implements Command {

    private final WorkspaceController workspaceController;
    private final Point2D selectionMin;
    private final Point2D selectionMax;

    public RectangleSelectCommand(WorkspaceController workspaceController, Point2D selectionMin, Point2D selectionMax) {
        this.workspaceController = workspaceController;
        this.selectionMin = workspaceController.getView().sceneToLocal(selectionMin);
        this.selectionMax = workspaceController.getView().sceneToLocal(selectionMax);
    }

    @Override
    public boolean execute() {
        workspaceController.rectangleSelect(selectionMin, selectionMax);
        return true;

    }

}
