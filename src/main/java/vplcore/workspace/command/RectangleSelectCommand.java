package vplcore.workspace.command;

import javafx.geometry.Point2D;
import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

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
    public void execute() {
        workspaceController.rectangleSelect(selectionMin, selectionMax);
    }


}
