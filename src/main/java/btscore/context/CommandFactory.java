package btscore.context;

import btscore.context.command.AlignBottomCommand;
import btscore.context.command.AlignHorizontallyCommand;
import btscore.context.command.AlignLeftCommand;
import btscore.context.command.AlignRightCommand;
import btscore.context.command.AlignTopCommand;
import btscore.context.command.AlignVerticallyCommand;
import btscore.context.command.CopyBlocksCommand;
import btscore.context.command.GroupBlocksCommand;
import btscore.context.command.NewFileCommand;
import btscore.context.command.OpenFileCommand;
import btscore.context.command.PasteBlocksCommand;
import btscore.context.command.ReloadPluginsCommand;
import btscore.context.command.RemoveSelectedBlocksCommand;
import btscore.context.command.SaveAsFileCommand;
import btscore.context.command.SaveFileCommand;
import btscore.context.command.ZoomInCommand;
import btscore.context.command.ZoomOutCommand;
import btscore.context.command.ZoomToFitCommand;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author joostmeulenkamp
 */
public class CommandFactory {

    private final WorkspaceModel workspaceModel;
    private final WorkspaceController workspaceController;

    public CommandFactory(WorkspaceModel workspaceModel, WorkspaceController workspaceController) {
        this.workspaceModel = workspaceModel;
        this.workspaceController = workspaceController;
    }

    public Command createCommand(String id) {
        switch (id) {
            case "NEW_FILE":
                return new NewFileCommand(workspaceModel);
            case "OPEN_FILE":
                return new OpenFileCommand(workspaceModel);
            case "SAVE_FILE":
                return new SaveFileCommand(workspaceModel);
            case "SAVE_AS_FILE":
                return new SaveAsFileCommand(workspaceModel);
            case "COPY_BLOCKS":
                return new CopyBlocksCommand(workspaceController);
            case "PASTE_BLOCKS":
                return new PasteBlocksCommand(workspaceController, workspaceModel);
            case "DELETE_SELECTED_BLOCKS":
                return new RemoveSelectedBlocksCommand(workspaceController);
            case "GROUP_BLOCKS":
                return new GroupBlocksCommand(workspaceController, workspaceModel);
            case "ALIGN_LEFT":
                return new AlignLeftCommand(workspaceController);
            case "ALIGN_VERTICALLY":
                return new AlignVerticallyCommand(workspaceController);
            case "ALIGN_RIGHT":
                return new AlignRightCommand(workspaceController);
            case "ALIGN_TOP":
                return new AlignTopCommand(workspaceController);
            case "ALIGN_HORIZONTALLY":
                return new AlignHorizontallyCommand(workspaceController);
            case "ALIGN_BOTTOM":
                return new AlignBottomCommand(workspaceController);
            case "ZOOM_TO_FIT":
                return new ZoomToFitCommand(workspaceController);
            case "ZOOM_IN":
                return new ZoomInCommand(workspaceController);
            case "ZOOM_OUT":
                return new ZoomOutCommand(workspaceController);
            case "RELOAD_PLUGINS":
                return new ReloadPluginsCommand();
        }
        return null;
    }
}
