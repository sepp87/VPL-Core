package btscore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import btscore.App;
import btscore.Config;
import btsxml.io.GraphSaver;
import btscore.context.Command;
import btscore.workspace.WorkspaceModel;
import btscore.context.MarkSavedCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class SaveAsFileCommand implements Command, MarkSavedCommand {

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
        chooser.setTitle("Save as ." + Config.XML_FILE_EXTENSION + " file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Config.XML_FILE_EXTENSION, "*." + Config.XML_FILE_EXTENSION));
        File file = chooser.showSaveDialog(App.getStage());

        if (file != null) {
            Config.setLastOpenedDirectory(file);
            GraphSaver.serialize(file, workspaceModel);
        }
        return true;

    }

}
