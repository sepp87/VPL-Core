package vpllib.input;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javax.xml.namespace.QName;
import vplcore.IconType;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Core.comboBlock",
        category = "General",
        description = "A generic block used to convert static lists into combo boxes",
        tags = {"core", "combo", "block"})
public class ComboBlock extends BlockModel {

    private ComboBox<String> comboBox;
    
    public ComboBlock() {
        this.nameProperty().set("Template");
        addOutputPort("String", String.class);
    }

    @Override
    protected void initialize() {
        // Event handlers, change listeners and bindings
    }

    @Override
    public Region getCustomization() {

        // Create a ComboBox with sample values
        comboBox = new ComboBox<>();
        comboBox.getItems().addAll("Apple", "Banana", "Cherry", "Date", "Elderberry");

        // Label to display the selected item
        Label selectedLabel = new Label("Select a fruit");

        // Update label when a selection is made
        comboBox.setOnAction(e -> {
            String selected = comboBox.getValue();
            selectedLabel.setText("You selected: " + selected);
        });

        // Layout
        VBox root = new VBox(10, comboBox, selectedLabel);
        return root;
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnComboBox;
    }

    private void focusOnComboBox(MouseEvent event) {
        comboBox.requestFocus();
    }

    /**
     * process function is called whenever new data is incoming
     */
    @Override
    public void process() {
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("key"), "value");
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        //Specify further initialization statements here
        this.processSafely();
    }

    @Override
    public BlockModel copy() {
        ComboBlock block = new ComboBlock();
        //Specify further copy statements here
        return block;
    }

    @Override
    protected void onRemoved() {
        // Remove event handlers, change listeners and bindings
    }
}
