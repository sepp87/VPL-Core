package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vplcore.App;
import vplcore.graph.io.GraphLoader;
import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements Command {

    private final WorkspaceController workspaceController;

    public OpenFileCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {
        //Open File
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showOpenDialog(App.getStage());

        if (file == null) {
            return;
        }

        //Clear Layout
        workspaceController.reset();

        //Load file
        GraphLoader.deserialize(file, workspaceController, workspaceController.getModel());
    }


}
