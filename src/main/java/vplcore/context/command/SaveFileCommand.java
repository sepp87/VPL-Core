package vplcore.context.command;

import java.io.File;
import vplcore.Config;
import vplcore.context.Command;
import vplcore.graph.io.GraphSaver;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.MarkSavedCommand;

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
