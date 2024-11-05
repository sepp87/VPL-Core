package vplcore.context.command;

import javafx.geometry.Point2D;
import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

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
    public void execute() {
        workspace.applyZoom(newScale, pivotPoint);
    }



}
