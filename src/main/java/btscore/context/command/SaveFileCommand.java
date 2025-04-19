package btscore.context.command;

import java.io.File;
import btscore.Config;
import btscore.context.Command;
import btsxml.io.GraphSaver;
import btscore.workspace.WorkspaceModel;
import btscore.context.MarkSavedCommand;

/**
 *
 * @author Joost
 */
public class SaveFileCommand implements Command, MarkSavedCommand {

    private final WorkspaceModel workspaceModel;

    public SaveFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        File file = workspaceModel.fileProperty().get();

        if (file != null) {
            Config.setLastOpenedDirectory(file);
            GraphSaver.serialize(file, workspaceModel);
        }
        return true;

    }

}
