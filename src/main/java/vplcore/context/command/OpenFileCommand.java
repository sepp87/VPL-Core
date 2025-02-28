package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import vplcore.App;
import vplcore.Config;
import vplcore.graph.io.GraphLoader;
import vplcore.context.Command;
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
        File lastOpenedDirectory = Config.getLastOpenedDirectory();
        if (lastOpenedDirectory != null) {
            chooser.setInitialDirectory(lastOpenedDirectory);
        }
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));

        File file = chooser.showOpenDialog(App.getStage());

        if (file == null) {
            return;
        }
        
        Config.setLastOpenedDirectory(file);

        //Clear Layout
        workspaceController.reset();

        //Load file
        GraphLoader.deserialize(file, workspaceController, workspaceController.getModel());
    }

}
