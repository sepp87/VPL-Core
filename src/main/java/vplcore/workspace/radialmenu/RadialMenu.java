package vplcore.workspace.radialmenu;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenu extends Group {

    private final double INNER_RADIUS = 50;
    private final double OUTER_RADIUS = 120;

    private final List<RadialMenuItem<?>> items;
    private final Label radialMenuLabel;
    private final String RADIAL_MENU_ITEM_LABEL_TEXT = "Exit\nmenu";
    private final Shape radialSubMenuCircle;

    private RadialSubMenu activeRadialSubMenu;

    public void show(double x, double y) {
        this.setVisible(true);
        this.setTranslateX(x);
        this.setTranslateY(y);
        returnToMain();
    }

    public RadialMenu(List<RadialMenuItem<?>> items) {

        this.items = items;

        Circle outer = new Circle(0, 0, OUTER_RADIUS);
        Circle inner = new Circle(0, 0, INNER_RADIUS);
        Shape radialMenuCircle = Shape.subtract(outer, inner);
        radialMenuCircle.getStyleClass().add("new-radial-menu");

        radialSubMenuCircle = Shape.subtract(outer, inner);
        radialSubMenuCircle.getStyleClass().add("new-radial-sub-menu");

        radialMenuLabel = new Label(RADIAL_MENU_ITEM_LABEL_TEXT);
        radialMenuLabel.getStyleClass().add("new-radial-menu-label");
        radialMenuLabel.layoutXProperty().bind(radialMenuLabel.widthProperty().divide(2).negate());
        radialMenuLabel.layoutYProperty().bind(radialMenuLabel.heightProperty().divide(2).negate());
        radialMenuLabel.setOnMouseClicked((MouseEvent mouseEvent) -> {
            RadialMenu.this.setVisible(false);
        });

        this.getChildren().addAll(radialMenuCircle, radialMenuLabel);
        this.getProperties().put("label", radialMenuLabel);

        int count = items.size();
        double length = Math.PI * 2 / items.size();
        for (int i = 0; i < count; i++) {
            RadialMenuItem item = items.get(i);
            item.index = i;
            item.length = length;
            addRadialMenuItem(item, false);
            this.getChildren().add(item);
        }
    }

    public List<RadialMenuItem<?>> addSubMenu(RadialSubMenu subMenu, List<RadialMenuItem<?>> subItems) {

        // build items
        int count = subItems.size();
        int remainder = count % 2;
        int start = subMenu.index - (count - remainder) / 2;
        int limit = start + count + 1;
        int lastIndexOfParent = items.size() - 1;
        List<Integer> indeces = new ArrayList<>();
        for (int i = start; i < limit; i++) {
            int index = i;
            // omit exit sub menu button index
            if (index == subMenu.index) {
                continue;
            }
            // shift the index to the valid range from 0 to last index of parent
            if (index < 0) {
                index = items.size() + index;
            } else if (index > lastIndexOfParent) {
                index = index - lastIndexOfParent;
            }
            indeces.add(index);
        }

        for (int i = 0; i < count; i++) {
            RadialMenuItem<?> subItem = subItems.get(i);
            subItem.index = indeces.get(i);
            subItem.length = subMenu.length;
            addRadialMenuItem(subItem, true);
        }

        // build exit button
        RadialMenuItem<?> exit = new RadialMenuItem<>(null, subMenu.icon, "Return\nto main");
        exit.index = subMenu.index;
        exit.length = subMenu.length;
        addRadialMenuItem(exit, true);

        // set event handlers       
        subMenu.setOnMouseClicked(handle_openSubMenuClicked);
        exit.setOnMouseClicked(handle_returnToMainClicked);

        subItems.add(exit);
        subMenu.items.addAll(subItems);
        return subItems;
    }

    private RadialMenuItem<?> addRadialMenuItem(RadialMenuItem<?> item, boolean isSubMenu) {
        String sub = isSubMenu ? "-sub" : "";

        Path path = new Path();
        path.getStyleClass().add("new-radial" + sub + "-menu-item");
//        path.getProperties().put("index", index);
//        path.setFill(Color.RED);

        double length = item.length;
        int index = item.index;
        int next = index + 1;

        // negate the length so the menu items are added clockwise
        boolean clockwise = true;
        length = clockwise ? -length : length;

        // set offset so the first menu items is placed on the top center
        double offset = Math.PI - length / 2;
        offset = clockwise ? offset : -offset - length;

        // calculate coordinates for arcs
        double innerStartX = INNER_RADIUS * Math.sin(length * index + offset);
        double innerStartY = INNER_RADIUS * Math.cos(length * index + offset);
        double innerNextX = INNER_RADIUS * Math.sin(length * next + offset);
        double innerNextY = INNER_RADIUS * Math.cos(length * next + offset);

        double outerStartX = OUTER_RADIUS * Math.sin(length * index + offset);
        double outerStartY = OUTER_RADIUS * Math.cos(length * index + offset);
        double outerNextX = OUTER_RADIUS * Math.sin(length * next + offset);
        double outerNextY = OUTER_RADIUS * Math.cos(length * next + offset);

        // generate arc by path
        MoveTo outerStart = new MoveTo();
        outerStart.setX(outerStartX);
        outerStart.setY(outerStartY);

        ArcTo outerNext = new ArcTo();
        outerNext.setX(outerNextX);
        outerNext.setY(outerNextY);
        outerNext.setRadiusX(OUTER_RADIUS);
        outerNext.setRadiusY(OUTER_RADIUS);
        outerNext.setSweepFlag(clockwise);

        LineTo innerNext = new LineTo();
        innerNext.setX(innerNextX);
        innerNext.setY(innerNextY);

        ArcTo innerStart = new ArcTo();
        innerStart.setX(innerStartX);
        innerStart.setY(innerStartY);
        innerStart.setRadiusX(INNER_RADIUS);
        innerStart.setRadiusY(INNER_RADIUS);
        innerStart.setSweepFlag(!clockwise);

        LineTo end = new LineTo();
        end.setX(outerStartX);
        end.setY(outerStartY);

        path.getElements().add(outerStart);
        path.getElements().add(outerNext);
        path.getElements().add(innerNext);
        path.getElements().add(innerStart);
        path.getElements().add(end);

        // create and position label in the middle of the arc
        double centerRadius = (INNER_RADIUS + OUTER_RADIUS) / 2;
        double centerOffset = offset + length / 2;
        double centerX = centerRadius * Math.sin(length * index + centerOffset);
        double centerY = centerRadius * Math.cos(length * index + centerOffset);

//        Label label = new Label(index + "");
        Label label = new Label(item.icon.getUnicode() + "");
        label.setRotate(item.iconRotation);
        label.getStyleClass().add("new-radial" + sub + "-menu-item-icon");
        label.layoutXProperty().bind(label.widthProperty().divide(2).negate().add(centerX));
        label.layoutYProperty().bind(label.heightProperty().divide(2).negate().add(centerY));

        // group everything together and return
        item.getChildren().add(path);
        item.getChildren().add(label);
        item.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                radialMenuLabel.setText(item.name);
                path.getStyleClass().add("new-radial" + sub + "-menu-item-hover");
            } else {
                radialMenuLabel.setText(RADIAL_MENU_ITEM_LABEL_TEXT);
                path.getStyleClass().remove("new-radial" + sub + "-menu-item-hover");
            }
        });
        return item;
    }

    private final EventHandler<MouseEvent> handle_returnToMainClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            returnToMain();
        }
    };

    private final EventHandler<MouseEvent> handle_openSubMenuClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            RadialSubMenu subMenu = (RadialSubMenu) event.getSource();
            openSubMenu(subMenu);
        }
    };

    private void openSubMenu(RadialSubMenu subMenu) {
        this.getChildren().add(radialSubMenuCircle);
        this.getChildren().addAll(subMenu.items);
        activeRadialSubMenu = subMenu;
    }

    private void returnToMain() {
        if (activeRadialSubMenu == null) {
            return;
        }
        this.getChildren().remove(radialSubMenuCircle);
        this.getChildren().removeAll(activeRadialSubMenu.items);
        activeRadialSubMenu = null;
    }

}
