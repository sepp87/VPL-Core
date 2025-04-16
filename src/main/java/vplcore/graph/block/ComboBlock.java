package vplcore.graph.block;

import java.lang.reflect.Type;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javax.xml.namespace.QName;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;

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

    private final List list;
    private final ObjectProperty selected = new SimpleObjectProperty();

    private ComboBox<Object> comboBox;
    private Class<?> type;

    public ComboBlock(List<?> list, Class<?> type) {
        this.nameProperty().set("Template");
        addOutputPort("selected", type);
        this.list = list;
        this.type = type;
    }

    @Override
    protected void initialize() {
        // Event handlers, change listeners and bindings
        outputPorts.get(0).dataProperty().bind(selected);
    }

    @Override
    public Region getCustomization() {

        // Create a ComboBox with sample values
        comboBox = new ComboBox<>();
        comboBox.setPrefWidth(202);
        comboBox.setMaxWidth(202);
        comboBox.setPromptText("Select a value");
        comboBox.getItems().addAll(list);
        comboBox.setCellFactory(lv -> {
            ListCell<Object> cell = new ListCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item.toString());
                }
            };

            // Replace hover logic
            cell.setOnMouseEntered(e -> {
                if (!cell.isEmpty()) {
                    lv.getSelectionModel().select(cell.getIndex());
                }
            });

            return cell;
        });

        comboBox.showingProperty().addListener(hiddenListener);
        comboBox.valueProperty().addListener(upDownListener);

        // Layout
        VBox root = new VBox(comboBox);
        return root;
    }

    private final ChangeListener<Boolean> hiddenListener = this::onHiddenUpdateSelected;

    private void onHiddenUpdateSelected(Object b, boolean o, boolean isShown) {
        if (!isShown) {
            selected.set(comboBox.getValue());
        }
    }

    private final ChangeListener<Object> upDownListener = this::onUpDownUpdateSelected;

    private void onUpDownUpdateSelected(Object b, Object o, Object n) {
        if (!comboBox.isShowing()) {
            selected.set(comboBox.getValue());
        }
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
        ComboBlock block = new ComboBlock(list, type);
        //Specify further copy statements here
        return block;
    }

    @Override
    protected void onRemoved() {
        outputPorts.get(0).dataProperty().unbind();

        // Remove event handlers, change listeners and bindings
        if (comboBox == null) {
            return;
        }
        comboBox.showingProperty().removeListener(hiddenListener);
        comboBox.valueProperty().removeListener(upDownListener);

    }
}
