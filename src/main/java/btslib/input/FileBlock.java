package btslib.input;

import btscore.icons.FontAwesomeSolid;
import java.io.File;
import java.nio.file.NoSuchFileException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.namespace.QName;
import btscore.graph.base.BaseButton;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.file",
        category = "Input",
        description = "Open a file",
        tags = {"file", "open", "load"}
)
public class FileBlock extends BlockModel {

    private final StringProperty path = new SimpleStringProperty();

    private BaseButton button;
    private TextField textField;

    public FileBlock( ) {        
        this.nameProperty().set("File");
        addOutputPort("file", File.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        path.addListener(pathListener);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setPromptText("Open a file...");
        textField.setFocusTraversable(false);

        button = new BaseButton(FontAwesomeSolid.FOLDER_OPEN);
        button.setOnAction(this::handleOpenFile);

        HBox box = new HBox(5);
        box.getChildren().addAll(textField, button);

        textField.setOnMouseEntered(this::focusOnTextField);
        textField.textProperty().bindBidirectional(path);
        return box;
    }

    ChangeListener<String> pathListener = this::onPathChanged;

    private void onPathChanged(Object b, String o, String n) {
        processSafely();
    }

    private void focusOnTextField(MouseEvent event) {
        textField.requestFocus();
    }

    private void handleOpenFile(ActionEvent event) {

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

    public StringProperty pathProperty() {
        return path;
    }

    @Override
    public void process() throws NoSuchFileException {

        if (path.get() == null || path.get().isEmpty()) {
            outputPorts.get(0).setData(null);
            return;
        }

        File file = new File(path.get());
        if (file.exists() && file.isFile()) {
            outputPorts.get(0).setData(file);
        } else {
            outputPorts.get(0).setData(null);
            throw new NoSuchFileException(path.get(), null, "File does not exist or is not a file.");
        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        String filePath = path.get() != null ? path.get() : "";
        xmlTag.getOtherAttributes().put(QName.valueOf("path"), filePath);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String filePath = xmlTag.getOtherAttributes().get(QName.valueOf("path"));
        filePath = !filePath.isEmpty() ? filePath : null;
        this.path.set(filePath);
    }

    @Override
    public BlockModel copy() {
        FileBlock block = new FileBlock();
        block.path.set(this.path.get());
        return block;
    }

    @Override
    public void onRemoved() {
        path.removeListener(pathListener);

        if (textField != null) {
            textField.setOnMouseEntered(null);
            textField.textProperty().unbindBidirectional(path);
        }

        if (button != null) {
            button.setOnAction(null);
        }
    }

}
