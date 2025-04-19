package btslib.input;

import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.util.ListViewHoverSelectBehaviour;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Date.temporalUnit",
        description = "A standard set of date periods units.",
        category = "Core")
public class ChronoUnitBlock extends BlockModel {
    
    private final Map<String, Object> unitsMap;
    private final ObjectProperty value = new SimpleObjectProperty();
    
    private ComboBox<String> comboBox;
    private ListView<String> listView;
    
    public ChronoUnitBlock() {
        this.nameProperty().set("Temporal Unit");
        addOutputPort("selected", TemporalUnit.class);
        this.unitsMap = getChronoUnits();
        initialize();
    }
    
    private Map<String, Object> getChronoUnits() {
        Class<?> clazz = java.time.temporal.ChronoUnit.class;
        Map<String, Object> result = new HashMap<>();
        for (Object constant : clazz.getEnumConstants()) {
            result.put(constant.toString(), constant);
        }
        return result;
    }
    
    @Override
    protected final void initialize() {
        // Event handlers, change listeners and bindings
        outputPorts.get(0).dataProperty().bind(value);
    }
    
    @Override
    public Region getCustomization() {

        // Create a ComboBox with sample values
        comboBox = new ComboBox<>();
        comboBox.setPrefWidth(202);
        comboBox.setMaxWidth(202);
        comboBox.setPromptText("Select a value");
        comboBox.getItems().addAll(unitsMap.keySet());
//        comboBox.setCellFactory(lv -> {
//            ListCell<String> cell = new ListCell<>() {
//                @Override
//                protected void updateItem(String item, boolean empty) {
//                    super.updateItem(item, empty);
//                    setText(item);
////                    setText(item == null || empty ? null : item.toString());
//                }
//            };
//
//            // Replace hover logic
//            cell.setOnMouseEntered(e -> {
//                if (!cell.isEmpty()) {
//                    lv.getSelectionModel().select(cell.getIndex());
//                }
//            });
//
//            return cell;
//        });
        comboBox.setOnShown(event -> {
            ComboBoxListViewSkin<String> skin = (ComboBoxListViewSkin<String>) comboBox.getSkin();
            this.listView = (ListView<String>) skin.getPopupContent();
            new ListViewHoverSelectBehaviour(listView);
        });

        if (value.get() != null) {
            comboBox.getSelectionModel().select(value.get().toString());
        }
        comboBox.showingProperty().addListener(hiddenListener);
        comboBox.valueProperty().addListener(upDownListener);

        // Layout
        VBox root = new VBox(comboBox);
        return root;
    }
    
    private final ChangeListener<Boolean> hiddenListener = this::onHiddenUpdateSelected;
    
    private void onHiddenUpdateSelected(Object b, boolean o, boolean isShowing) {
        if (!isShowing) {
            value.set(unitsMap.get(comboBox.getValue()));
        }
    }
    
    private final ChangeListener<Object> upDownListener = this::onUpDownUpdateSelected;
    
    private void onUpDownUpdateSelected(Object b, Object o, Object n) {
        if (!comboBox.isShowing()) {
            value.set(unitsMap.get(comboBox.getValue()));
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
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), value.get().toString());
    }
    
    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String key = xmlTag.getOtherAttributes().get(QName.valueOf("value"));
        value.set(unitsMap.get(key));
        if (comboBox != null) {
            comboBox.setValue(key);
        }
    }
    
    @Override
    public BlockModel copy() {
        ChronoUnitBlock block = new ChronoUnitBlock();
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
        
        if (listView == null) {
            return;
        }
        listView.setOnMouseMoved(null);
        
    }
}
