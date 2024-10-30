package vplcore.workspace.command;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vplcore.graph.io.GraphSaver;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements Command {

    private final Workspace workspace;

    public SaveFileCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            GraphSaver.serialize(file, workspace, workspace.getZoomModel());
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
