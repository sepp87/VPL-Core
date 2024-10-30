package vplcore.workspace.command;

import javafx.geometry.Point2D;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class ApplyZoomCommand implements Command {

    private final Workspace workspace;
    private final double newScale;
    private final Point2D pivotPoint;

    public ApplyZoomCommand(Workspace workspace, double newScale, Point2D pivotPoint) {
        this.workspace = workspace;
        this.newScale = newScale;
        this.pivotPoint = pivotPoint;
    }

    @Override
    public void execute() {
        workspace.applyZoom(newScale, pivotPoint);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
