package vplcore.context;

import vplcore.context.command.AlignBottomCommand;
import vplcore.context.command.AlignHorizontallyCommand;
import vplcore.context.command.AlignLeftCommand;
import vplcore.context.command.AlignRightCommand;
import vplcore.context.command.AlignTopCommand;
import vplcore.context.command.AlignVerticallyCommand;
import vplcore.context.command.CopyBlocksCommand;
import vplcore.context.command.GroupBlocksCommand;
import vplcore.context.command.NewFileCommand;
import vplcore.context.command.OpenFileCommand;
import vplcore.context.command.PasteBlocksCommand;
import vplcore.context.command.ReloadPluginsCommand;
import vplcore.context.command.RemoveSelectedBlocksCommand;
import vplcore.context.command.SaveAsFileCommand;
import vplcore.context.command.SaveFileCommand;
import vplcore.context.command.ZoomInCommand;
import vplcore.context.command.ZoomOutCommand;
import vplcore.context.command.ZoomToFitCommand;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

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
