package jo.vpl;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import jo.vpl.util.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class NewRadialMenu {

    public static double innerRadius = 50;
    public static double outerRadius = 120;
    public static List<IconType> menuItems = new ArrayList<>();

    // innerRadius
    // outerRadius
    // menuItems
    // color
    // onHover
    // onClick
    // icon
    public static Group getMenu() {

        menuItems.add(IconType.FA_FOLDER_OPEN_O);
        menuItems.add(IconType.FA_FLOPPY_O);
        menuItems.add(IconType.FA_SEARCH);
        menuItems.add(IconType.FA_SORT_AMOUNT_ASC);
        menuItems.add(IconType.FA_CLONE);
        menuItems.add(IconType.FA_CLIPBOARD);
        menuItems.add(IconType.FA_OBJECT_GROUP);
        menuItems.add(IconType.FA_FILE_O);

        Circle outer = new Circle(0, 0, outerRadius);
        Circle inner = new Circle(0, 0, innerRadius);
        Shape circle = Shape.subtract(outer, inner);
        circle.getStyleClass().add("new-radial-menu");

        Group menu = new Group();
        menu.getChildren().add(circle);

        int count = menuItems.size();
        for (int i = 0; i < count; i++) {
            Group menuItem = getMenuItem(i);
            menu.getChildren().add(menuItem);
        }

        //testing purposes. move to visible area
        menu.setTranslateX(outerRadius);
        menu.setTranslateY(outerRadius);
        return menu;
    }

    public static Group getMenuItem(int index) {

        Path path = new Path();
        path.getProperties().put("index", index);
//        path.setFill(Color.RED);
        path.getStyleClass().add("new-radial-menu-item");

        double length = Math.PI * 2 / menuItems.size();
        int next = index + 1;

        // negate the length so the menu items are added clockwise
        boolean clockwise = true;
        length = clockwise ? -length : length;

        // set offset so the first menu items is placed on the top center
        double offset = Math.PI - length / 2;
        offset = clockwise ? offset : -offset - length;

        // calculate coordinates for arcs
        double innerStartX = innerRadius * Math.sin(length * index + offset);
        double innerStartY = innerRadius * Math.cos(length * index + offset);
        double innerNextX = innerRadius * Math.sin(length * next + offset);
        double innerNextY = innerRadius * Math.cos(length * next + offset);

        double outerStartX = outerRadius * Math.sin(length * index + offset);
        double outerStartY = outerRadius * Math.cos(length * index + offset);
        double outerNextX = outerRadius * Math.sin(length * next + offset);
        double outerNextY = outerRadius * Math.cos(length * next + offset);

        // generate arc by path
        MoveTo outerStart = new MoveTo();
        outerStart.setX(outerStartX);
        outerStart.setY(outerStartY);

        ArcTo outerNext = new ArcTo();
        outerNext.setX(outerNextX);
        outerNext.setY(outerNextY);
        outerNext.setRadiusX(outerRadius);
        outerNext.setRadiusY(outerRadius);
        outerNext.setSweepFlag(clockwise);

        LineTo innerNext = new LineTo();
        innerNext.setX(innerNextX);
        innerNext.setY(innerNextY);

        ArcTo innerStart = new ArcTo();
        innerStart.setX(innerStartX);
        innerStart.setY(innerStartY);
        innerStart.setRadiusX(innerRadius);
        innerStart.setRadiusY(innerRadius);
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
        double centerRadius = (innerRadius + outerRadius) / 2;
        double centerOffset = offset + length / 2;
        double centerX = centerRadius * Math.sin(length * index + centerOffset);
        double centerY = centerRadius * Math.cos(length * index + centerOffset);

//        Label label = new Label(index + "");
        Label label = new Label(menuItems.get(index).getUnicode() + "");
        label.getStyleClass().add("new-radial-menu-item-icon");
        label.layoutXProperty().bind(label.widthProperty().divide(2).negate().add(centerX));
        label.layoutYProperty().bind(label.heightProperty().divide(2).negate().add(centerY));

        // group everything together and return
        Group item = new Group();
        item.getChildren().add(path);
        item.getChildren().add(label);
        item.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                path.getStyleClass().add("new-radial-menu-item-hover");
            } else {
                path.getStyleClass().remove("new-radial-menu-item-hover");
            }
        });

        return item;
    }

}
