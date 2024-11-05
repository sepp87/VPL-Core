package vplcore.context.command;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vplcore.App;
import vplcore.graph.io.GraphSaver;
import vplcore.workspace.Command;
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
    public void execute() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(App.getStage());

        if (file != null) {
            GraphSaver.serialize(file, workspaceController, workspaceController.getModel());
        }
    }


}
