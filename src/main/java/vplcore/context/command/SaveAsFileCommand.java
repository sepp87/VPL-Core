package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import vplcore.App;
import vplcore.Config;
import vplcore.graph.io.GraphSaver;
import vplcore.context.Command;
import vplcore.context.DisableSaveCommand;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements Command, DisableSaveCommand {

    private final WorkspaceModel workspaceModel;

    public SaveAsFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
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
            GraphSaver.serialize(file, workspaceModel);
        }
        return true;

    }

}
