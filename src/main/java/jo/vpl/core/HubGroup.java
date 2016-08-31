package jo.vpl.core;

import java.beans.PropertyChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 *
 * @author JoostMeulenkamp
 */
public class HubGroup extends VPLElement {

    public int id;

    private static int counter;
    public ObservableSet<Hub> childHubs;

    public HubGroup(VPLControl vplControl) {
        super(vplControl);

        getStyleClass().add("hub-group");

        id = counter++;

        childHubs = FXCollections.observableSet();
        setOnMousePressed(this::handle_MousePress);
        setOnMouseReleased(this::handle_MouseRelease);

//        CaptionLabel.Width = 200;
        setName("Name group here...");

        hostCanvas.hubGroupSet.add(this);
        hostCanvas.getChildren().add(1, this);
//        SetZIndex(this, 0);
//        SetZIndex(Border, 0);
    }

    public void setChildHubs(ObservableSet<Hub> hubSet) {
        childHubs.addAll(hubSet);
//        childHubs = hubSet;
        childHubs.addListener(this::handle_CollectionChange);
        observeAllChildHub();
        calculateSize();
    }

    private void handle_MousePress(MouseEvent e) {
        for (Hub hub : childHubs) {

            hub.setOnMouseDragged(hub::handle_MouseDrag);

//            HostCanvas.MouseUp += node.Node_MouseUp; FOR RESIZE EVENT
            hub.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());

            hub.setSelected(true);
            hostCanvas.selectedHubSet.add(hub);
        }
        hostCanvas.mouseMode = MouseMode.GROUP_SELECT;
    }

    private void handle_MouseRelease(MouseEvent e) {
        hostCanvas.mouseMode = MouseMode.NOTHING;
        e.consume();
    }

    @Override
    public void binButton_MouseClick(MouseEvent e) {
        deleteGroup();
    }

    private void deleteGroup() {
        unObserveAllChildHub();
        hostCanvas.hubGroupSet.remove(this);
        super.delete();
    }

    private void handle_CollectionChange(SetChangeListener.Change change) {

        if (change.wasAdded()) {
            Hub hub = (Hub) change.getElementAdded();
            hub.eventBlaster.add("deleted", this::hub_DeletedInHubSet);
            hub.eventBlaster.add(this::hub_PropertyChanged);
        } else {
            Hub hub = (Hub) change.getElementRemoved();
            hub.eventBlaster.remove("deleted", this::hub_DeletedInHubSet);
            hub.eventBlaster.remove(this::hub_PropertyChanged);
        }

        if (childHubs.size() < 2) {
//            binButton_Click(null, null);
            deleteGroup();
        } else {
            calculateSize();
        }
    }

    private void observeAllChildHub() {
        for (Hub hub : childHubs) {
            hub.eventBlaster.add("deleted", this::hub_DeletedInHubSet);
            hub.eventBlaster.add(this::hub_PropertyChanged);
        }
    }

    private void unObserveAllChildHub() {
        for (Hub hub : childHubs) {
            hub.eventBlaster.remove("deleted", this::hub_DeletedInHubSet);
            hub.eventBlaster.remove(this::hub_PropertyChanged);
        }
    }

    private void hub_DeletedInHubSet(PropertyChangeEvent e) {
        Hub hub = (Hub) e.getSource();
        if (hub == null) {
            return;
        }
        childHubs.remove(hub);
    }

    private void hub_PropertyChanged(PropertyChangeEvent e) {
        calculateSize();
    }

    private void calculateSize() {
        if (childHubs.isEmpty()) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Hub hub : childHubs) {

            if (hub.getLayoutX() < minX) {
                minX = hub.getLayoutX();
            }
            if (hub.getLayoutY() < minY) {
                minY = hub.getLayoutY();
            }
            if ((hub.getLayoutX() + hub.getWidth()) > maxX) {
                maxX = hub.getLayoutX() + hub.getWidth();
            }
            if ((hub.getLayoutY() + hub.getHeight()) > maxY) {
                maxY = hub.getLayoutY() + hub.getHeight();
            }
        }

        relocate(minX, minY);
        setPrefSize(maxX - minX, maxY - minY);

//        OnPropertyChanged("BorderSize");
    }

    private void bindStyle() {
        //Hub Passive Style
        Insets hubGroupBackgroundInsets = new Insets(4);
        CornerRadii hubGroupBackgroundRadius = new CornerRadii(8);
        Color hubGroupBackgroundColor = Color.web("#d35f5f");
        BackgroundFill hubGroupBackgroundFill = new BackgroundFill(
                hubGroupBackgroundColor,
                hubGroupBackgroundRadius,
                hubGroupBackgroundInsets);
        Background hubGroupBackground = new Background(hubGroupBackgroundFill);

        BorderWidths hubGroupBorderWidth = new BorderWidths(1);
        CornerRadii hubGroupBorderRadius = new CornerRadii(12);
        Color hubGroupBorderColor = Color.LIGHTGREY;
        BorderStroke hubBorderStroke = new BorderStroke(
                hubGroupBorderColor,
                BorderStrokeStyle.SOLID,
                hubGroupBorderRadius,
                hubGroupBorderWidth);
        Border hubBorder = new Border(hubBorderStroke);
        Insets hubPadding = new Insets(10);
    }
}
