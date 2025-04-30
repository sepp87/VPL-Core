package btslib.file;

import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * TODO should NOT have a text field to NOT create unwanted files upon typing
 *
 * @author joostmeulenkamp
 */
@BlockMetadata(
        identifier = "Output.file",
        category = "Output",
        description = "Save to file",
        tags = {"file", "save"}
)
public class FileOutputBlock extends FileBlock {

    @Override
    protected void customizeTextField(TextField textField) {
        textField.setPromptText("Save to file...");
        textField.setEditable(false);
    }

    @Override
    protected void handleOpenFile(ActionEvent event) {

        //Do Action
        FileChooser picker = new FileChooser();
        picker.setTitle("Save as...");
        Window window = button.getScene().getWindow();
        File file = picker.showSaveDialog(window);

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
    public void processFile(File file) throws NoSuchFileException, IOException {
        file.createNewFile();
        outputPorts.get(0).setData(file);
    }

    @Override
    public BlockModel copy() {
        FileOutputBlock block = new FileOutputBlock();
        block.path.set(this.path.get());
        return block;
    }

}
