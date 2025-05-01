package btslib.input;

import btslib.file.FileBlock;
import java.io.File;
import java.nio.file.NoSuchFileException;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.file",
        aliases = {"File.open"},
        category = "Input",
        description = "Open a file",
        tags = {"file", "open", "load"}
)
public class FileInputBlock extends FileBlock {

    @Override
    protected void customizeTextField(TextField textField) {
        textField.setPromptText("Open a file...");
    }

    @Override
    protected void handleOpenFile(ActionEvent event) {

        //Do Action
        FileChooser picker = new FileChooser();
        picker.setTitle("Choose a file...");
        Window window = button.getScene().getWindow();
        File file = picker.showOpenDialog(window);

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
        if (file.exists() && file.isFile()) {
            outputPorts.get(0).setData(file);
        } else {
            outputPorts.get(0).setData(null);
            throw new NoSuchFileException(path.get(), null, "File does not exist or is not a file.");
        }
    }

    @Override
    public BlockModel copy() {
        FileInputBlock block = new FileInputBlock();
        block.path.set(this.path.get());
        return block;
    }

}
