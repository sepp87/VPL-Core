package btscore.editor.context;

import btscore.editor.commands.AlignBottomCommand;
import btscore.editor.commands.AlignHorizontallyCommand;
import btscore.editor.commands.AlignLeftCommand;
import btscore.editor.commands.AlignRightCommand;
import btscore.editor.commands.AlignTopCommand;
import btscore.editor.commands.AlignVerticallyCommand;
import btscore.editor.commands.CopyBlocksCommand;
import btscore.editor.commands.GroupBlocksCommand;
import btscore.editor.commands.HelpCommand;
import btscore.editor.commands.NewFileCommand;
import btscore.editor.commands.OpenFileCommand;
import btscore.editor.commands.PasteBlocksCommand;
import btscore.editor.commands.ReloadPluginsCommand;
import btscore.editor.commands.RemoveSelectedBlocksCommand;
import btscore.editor.commands.SaveAsFileCommand;
import btscore.editor.commands.SaveFileCommand;
import btscore.editor.commands.ZoomInCommand;
import btscore.editor.commands.ZoomOutCommand;
import btscore.editor.commands.ZoomToFitCommand;
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
            case "HELP":
                return new HelpCommand();
        }
        return null;
    }
}
