package btslib.input;

import btscore.graph.base.BaseButton;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.icons.FontAwesomeSolid;
import btsxml.BlockTag;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javax.xml.namespace.QName;

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
public class DirectoryInputBlock extends BlockModel {

    private final StringProperty path = new SimpleStringProperty();

    private BaseButton button;
    private TextField textField;

    public DirectoryInputBlock( ) {        
        this.nameProperty().set("Directory");
        addOutputPort("directory", File.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        path.addListener(pathListener);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setPromptText("Open a directory...");
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
        if (file.exists() && file.isDirectory()) {
            outputPorts.get(0).setData(file);
        } else {
            outputPorts.get(0).setData(null);
            throw new NoSuchFileException(path.get(), null, "File does not exist or is not a directory.");
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
        DirectoryInputBlock block = new DirectoryInputBlock();
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
