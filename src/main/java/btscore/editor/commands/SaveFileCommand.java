package btscore.editor.commands;

import java.io.File;
import btscore.Config;
import btscore.editor.context.Command;
import btscore.graph.io.GraphSaver;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.MarkSavedCommand;

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
