package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import vplcore.App;
import vplcore.Config;
import vplcore.graph.io.GraphSaver;
import vplcore.context.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements Command {

    private final WorkspaceController workspaceController;

    public SaveFileCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public boolean execute() {

        FileChooser chooser = new FileChooser();
        File lastOpenedDirectory = Config.getLastOpenedDirectory();
        if (lastOpenedDirectory != null) {
            chooser.setInitialDirectory(lastOpenedDirectory);
        }
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(App.getStage());

        if (file != null) {
            Config.setLastOpenedDirectory(file);
            GraphSaver.serialize(file, workspaceController, workspaceController.getModel());
        }
        return true;

    }

}
