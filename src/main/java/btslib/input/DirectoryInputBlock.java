package btslib.input;

import btscore.graph.base.BaseButton;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btslib.file.FileBlock;
import java.io.File;
import java.nio.file.NoSuchFileException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

/**
 *
 * @author joostmeulenkamp
 */
@BlockMetadata(
        identifier = "Input.directory",
        category = "Input",
        description = "Open a directory",
        tags = {"directory", "open", "load"}
)
public class DirectoryInputBlock extends FileBlock {

    public DirectoryInputBlock() {
        this.nameProperty().set("Directory");
        output.nameProperty().set("directory");
        initialize();
    }

    @Override
    protected void customizeTextField(TextField textField) {
        textField.setPromptText("Open a directory...");
    }

    @Override
    protected void handleOpenFile(ActionEvent event) {

        //Do Action
        DirectoryChooser picker = new DirectoryChooser();
        picker.setTitle("Choose a directory...");
        Window window = button.getScene().getWindow();
        File file = picker.showDialog(window);

        //Set Data
        if (file != null) {
            String filePath = file.getPath();
            path.set(filePath);
            // when path changes, process is triggered
        } else {
            path.set(null);
            // when path changes, process is triggered
        }
    }

    @Override
    public void processFile(File file) throws NoSuchFileException {
        if (file.exists() && file.isDirectory()) {
            outputPorts.get(0).setData(file);
        } else {
            outputPorts.get(0).setData(null);
            throw new NoSuchFileException(path.get(), null, "File does not exist or is not a directory.");
        }
    }

    @Override
    public BlockModel copy() {
        DirectoryInputBlock block = new DirectoryInputBlock();
        block.path.set(this.path.get());
        return block;
    }
}
