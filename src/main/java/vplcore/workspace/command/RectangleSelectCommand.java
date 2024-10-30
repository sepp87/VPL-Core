package vplcore.workspace.command;

import javafx.geometry.Point2D;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class RectangleSelectCommand implements Command {

    private final Workspace workspace;
    private final Point2D selectionMin;
    private final Point2D selectionMax;

    public RectangleSelectCommand(Workspace workspace, Point2D selectionMin, Point2D selectionMax) {
        this.workspace = workspace;
        this.selectionMin = workspace.sceneToLocal(selectionMin);
        this.selectionMax = workspace.sceneToLocal(selectionMax);
    }

    @Override
    public void execute() {
        workspace.rectangleSelect(selectionMin, selectionMax);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
