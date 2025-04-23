package btscore.editor.radialmenu;

import btscore.icons.FontAwesomeRegular;
import btscore.icons.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuView extends Group {

    private final RadialMenu radialMenu;

    public RadialMenuView() {
        this.radialMenu = buildRadialMenu();
        this.radialMenu.setVisible(false);
        this.getChildren().add(radialMenu);
    }

    private RadialMenu buildRadialMenu() {
        // create menu items
        RadialMenuItem openFileItem = new RadialMenuItem("Open\nfile", "OPEN_FILE", FontAwesomeRegular.FOLDER_OPEN);
        RadialMenuItem saveFileItem = new RadialMenuItem("Save\nfile", "SAVE_FILE", FontAwesomeRegular.FLOPPY_DISK);
        RadialMenuItem zoomToFitItem = new RadialMenuItem("Zoom\nto fit", "ZOOM_TO_FIT", FontAwesomeSolid.SEARCH);
        RadialSubMenu alignItem = new RadialSubMenu("Align", FontAwesomeSolid.SORT_AMOUNT_DOWN);
        RadialMenuItem copyItem = new RadialMenuItem("Copy", "COPY_BLOCKS", FontAwesomeRegular.COPY);
        RadialMenuItem pasteItem = new RadialMenuItem("Paste", "PASTE_BLOCKS", FontAwesomeRegular.PASTE);
        RadialMenuItem groupItem = new RadialMenuItem("Group", "GROUP_BLOCKS", FontAwesomeRegular.OBJECT_GROUP);
        RadialMenuItem newFileItem = new RadialMenuItem("New\nfile", "NEW_FILE", FontAwesomeRegular.FILE);

        // create menu
        List<RadialMenuItem> items = new ArrayList();
        items.add(openFileItem);
        items.add(saveFileItem);
        items.add(zoomToFitItem);
        items.add(alignItem);
        items.add(copyItem);
        items.add(pasteItem);
        items.add(groupItem);
        items.add(newFileItem);
        RadialMenu menu = new RadialMenu(items);

        // build align sub menu
        List<RadialMenuItem> alignItems = buildAlignSubMenu();
        menu.addSubMenu(alignItem, alignItems);

        return menu;
    }

    private List<RadialMenuItem> buildAlignSubMenu() {
        List<RadialMenuItem> alignItems = new ArrayList();
        RadialMenuItem alignTopItem = new RadialMenuItem("Align\ntop", "ALIGN_TOP", FontAwesomeSolid.ALIGN_LEFT, 90.);
        RadialMenuItem alignVerticallyItem = new RadialMenuItem("Align\nvertically", "ALIGN_VERTICALLY", FontAwesomeSolid.ALIGN_CENTER);
        RadialMenuItem alignRightItem = new RadialMenuItem("Align\nright", "ALIGN_RIGHT", FontAwesomeSolid.ALIGN_RIGHT);
        RadialMenuItem alignBottomItem = new RadialMenuItem("Align\nbottom", "ALIGN_BOTTOM", FontAwesomeSolid.ALIGN_RIGHT, 90.);
        RadialMenuItem alignHorizontallyItem = new RadialMenuItem("Align\nhorizontally", "ALIGN_HORIZONTALLY", FontAwesomeSolid.ALIGN_CENTER, 90.);
        RadialMenuItem alignLefItem = new RadialMenuItem("Align\nleft", "ALIGN_LEFT", FontAwesomeSolid.ALIGN_LEFT);

        // create sub menu
        alignItems.add(alignTopItem);
        alignItems.add(alignVerticallyItem);
        alignItems.add(alignRightItem);
        alignItems.add(alignBottomItem);
        alignItems.add(alignHorizontallyItem);
        alignItems.add(alignLefItem);

        return alignItems;
    }

    public RadialMenu getRadialMenu() {
        return radialMenu;
    }

    public List<RadialMenuItem> getAllRadialMenuItems() {
        return radialMenu.getAllItems();
    }

}
