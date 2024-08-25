package vplcore.workspace.radialmenu;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import static vplcore.workspace.radialmenu.RadialMenuAction.OPEN_FILE;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenu extends Group {

    static final double INNER_RADIUS = 50;
    static final double OUTER_RADIUS = 120;
    static final List<RadialMenuItem> items = new ArrayList<>();

    private static RadialMenu radialMenu;
    private static Label radialMenuLabel;
    private static final String RADIAL_MENU_ITEM_LABEL_TEXT = "Exit\nmenu";
    private static Shape radialSubMenuCircle;
    private static RadialSubMenu activeRadialSubMenu;

    public static RadialMenu get() {

        if (radialMenu == null) {
            radialMenu = new RadialMenu();
        }
        return radialMenu;
    }

    public void show(double x, double y) {
        this.setVisible(true);
        this.setTranslateX(x);
        this.setTranslateY(y);
        returnToMain();
    }

    public void hide() {
        this.setVisible(false);

    }

    private RadialMenu() {

        RadialSubMenu align = new RadialSubMenu(IconType.FA_SORT_AMOUNT_ASC, "Align");
        items.add(new RadialMenuItem(IconType.FA_FOLDER_OPEN_O, "Open\nfile", RadialMenuAction.OPEN_FILE));
        items.add(new RadialMenuItem(IconType.FA_FLOPPY_O, "Save\nfile", RadialMenuAction.SAVE_FILE));
        items.add(new RadialMenuItem(IconType.FA_SEARCH, "Zoom\nto fit", RadialMenuAction.ZOOM_TO_FIT));
        items.add(align);
        items.add(new RadialMenuItem(IconType.FA_CLONE, "Copy", RadialMenuAction.COPY));
        items.add(new RadialMenuItem(IconType.FA_CLIPBOARD, "Paste", RadialMenuAction.PASTE));
        items.add(new RadialMenuItem(IconType.FA_OBJECT_GROUP, "Group", RadialMenuAction.GROUP));
        items.add(new RadialMenuItem(IconType.FA_FILE_O, "New\nfile", RadialMenuAction.NEW_FILE));

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
            hide();
        });

        this.getChildren().addAll(radialMenuCircle, radialMenuLabel);
        this.getProperties().put("label", radialMenuLabel);

        int count = items.size();
        double length = Math.PI * 2 / items.size();
        for (int i = 0; i < count; i++) {
            RadialMenuItem item = items.get(i);
            item.index = i;
            item.length = length;
            buildRadialMenuItem(item, false);
            this.getChildren().add(item);
        }

        // build align sub menu
        List<RadialMenuItem> alignItems = new ArrayList<>();
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_LEFT, "Align\ntop", RadialMenuAction.ALIGN_TOP, 90.));
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_CENTER, "Align\nvertically", RadialMenuAction.ALIGN_VERTICALLY));
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_RIGHT, "Align\nright", RadialMenuAction.ALIGN_RIGHT));
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_RIGHT, "Align\nbottom", RadialMenuAction.ALIGN_BOTTOM, 90.));
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_CENTER, "Align\nhorizontally", RadialMenuAction.ALIGN_HORIZONTALLY, 90.));
        alignItems.add(new RadialMenuItem(IconType.FA_ALIGN_LEFT, "Align\nleft", RadialMenuAction.ALIGN_LEFT));
        buildSubMenu(align, alignItems);

    }

    private static List<RadialMenuItem> buildSubMenu(RadialSubMenu menu, List<RadialMenuItem> subItems) {

        // build items
        int count = subItems.size();
        int remainder = count % 2;
        int start = menu.index - (count - remainder) / 2;
        int limit = start + count + 1;
        int lastIndexOfParent = items.size() - 1;
        List<Integer> indeces = new ArrayList<>();
        for (int i = start; i < limit; i++) {
            int index = i;
            // omit exit sub menu button index
            if (index == menu.index) {
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
            RadialMenuItem subItem = subItems.get(i);
            subItem.index = indeces.get(i);
            subItem.length = menu.length;
            buildRadialMenuItem(subItem, true);
        }

        // build exit button
        RadialMenuItem exit = new RadialMenuItem(menu.icon, "Return\nto main", RadialMenuAction.RETURN_TO_MAIN);
        exit.index = menu.index;
        exit.length = menu.length;
        buildRadialMenuItem(exit, true);

        subItems.add(exit);
        menu.items.addAll(subItems);
        return subItems;
    }

    private static RadialMenuItem buildRadialMenuItem(RadialMenuItem item, boolean isSubMenu) {
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

        item.setOnMouseClicked((MouseEvent mouseEvent) -> {
            handle_onMouseClicked(mouseEvent);
//            mouseEvent.consume();
        });

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

    private static void handle_onMouseClicked(MouseEvent mouseEvent) {
        RadialMenuItem item = (RadialMenuItem) mouseEvent.getSource();
        System.out.println(item.action);
        switch (item.action) {
            case RETURN_TO_MAIN:
                returnToMain();
                break;
            case OPEN_SUB_MENU:
                RadialSubMenu subMenu = (RadialSubMenu) item;
                radialMenu.getChildren().add(radialSubMenuCircle);
                radialMenu.getChildren().addAll(subMenu.items);
                activeRadialSubMenu = subMenu;
                break;
            case OPEN_FILE:
                break;
            case SAVE_FILE:
                break;
            case ALIGN_LEFT:
                break;
            case ALIGN_VERTICALLY:
                break;
            case ALIGN_RIGHT:
                break;
            case ALIGN_TOP:
                break;
            case ALIGN_HORIZONTALLY:
                break;
            case ALIGN_BOTTOM:
                break;
            case ZOOM_TO_FIT:
                break;
            case COPY:
                break;
            case PASTE:
                break;
            case GROUP:
                break;
            case NEW_FILE:
                break;
        }
    }

    private static void returnToMain() {
        if (activeRadialSubMenu == null) {
            return;
        }
        radialMenu.getChildren().remove(radialSubMenuCircle);
        radialMenu.getChildren().removeAll(activeRadialSubMenu.items);
        activeRadialSubMenu = null;
    }

}

class RadialMenuItem extends Group {

    RadialMenuAction action;
    IconType icon;
    double iconRotation = 0.;
    String name;
    int index;
    double length;

    public RadialMenuItem(IconType icon, String name, RadialMenuAction action) {
        this.action = action;
        this.icon = icon;
        this.name = name;
    }

    public RadialMenuItem(IconType icon, String name, RadialMenuAction action, double iconRotation) {
        this(icon, name, action);
        this.iconRotation = iconRotation;
    }

}

class RadialSubMenu extends RadialMenuItem {

    final List<RadialMenuItem> items = new ArrayList<>();

    public RadialSubMenu(IconType icon, String name) {
        super(icon, name, RadialMenuAction.OPEN_SUB_MENU);
    }

    public RadialSubMenu(IconType icon, String name, double iconRotation) {
        super(icon, name, RadialMenuAction.OPEN_SUB_MENU, iconRotation);
    }
}
