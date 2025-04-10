package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import vplcore.App;
import vplcore.Config;
import vplcore.context.Command;
import vplcore.graph.io.GraphLoader;
import vplcore.context.ResetHistoryCommand;
import vplcore.context.MarkSavedCommand;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class OpenFileCommand implements Command, ResetHistoryCommand, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;

    public OpenFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {
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
            return false;
        }

        Config.setLastOpenedDirectory(file);

        //Clear the workspace
        workspaceModel.reset();

        //Load file
        GraphLoader.deserialize(file, workspaceModel);
        return true;

    }

}
