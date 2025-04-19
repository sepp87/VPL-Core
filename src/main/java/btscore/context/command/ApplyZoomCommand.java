package btscore.context.command;

import javafx.geometry.Point2D;
import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class ApplyZoomCommand implements Command {

    private final WorkspaceController workspace;
    private final double newScale;
    private final Point2D pivotPoint;

    public ApplyZoomCommand(WorkspaceController workspace, double newScale, Point2D pivotPoint) {
        this.workspace = workspace;
        this.newScale = newScale;
        this.pivotPoint = pivotPoint;
    }

    @Override
    public boolean execute() {
        workspace.applyZoom(newScale, pivotPoint);
        return true;
    }

}
