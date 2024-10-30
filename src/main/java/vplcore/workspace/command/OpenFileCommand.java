package vplcore.workspace.command;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vplcore.graph.io.GraphLoader;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements Command {

    private final Workspace workspace;

    public OpenFileCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        //Open File
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        //Clear Layout
        workspace.reset();

        //Load file
        GraphLoader.deserialize(file, workspace, workspace.getZoomModel());
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
