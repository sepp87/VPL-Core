package vplcore.editor.radialmenu;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import vplcore.IconType;

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
        RadialMenuItem openFileItem = new RadialMenuItem("Open\nfile", "OPEN_FILE", IconType.FA_FOLDER_OPEN_O);
        RadialMenuItem saveFileItem = new RadialMenuItem("Save\nfile", "SAVE_FILE", IconType.FA_FLOPPY_O);
        RadialMenuItem zoomToFitItem = new RadialMenuItem("Zoom\nto fit", "ZOOM_TO_FIT", IconType.FA_SEARCH);
        RadialSubMenu alignItem = new RadialSubMenu("Align", IconType.FA_SORT_AMOUNT_ASC);
        RadialMenuItem copyItem = new RadialMenuItem("Copy", "COPY_BLOCKS", IconType.FA_CLONE);
        RadialMenuItem pasteItem = new RadialMenuItem("Paste", "PASTE_BLOCKS", IconType.FA_CLIPBOARD);
        RadialMenuItem groupItem = new RadialMenuItem("Group", "GROUP_BLOCKS", IconType.FA_OBJECT_GROUP);
        RadialMenuItem newFileItem = new RadialMenuItem("New\nfile", "NEW_FILE", IconType.FA_FILE_O);

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
        RadialMenuItem alignTopItem = new RadialMenuItem("Align\ntop", "ALIGN_TOP", IconType.FA_ALIGN_LEFT, 90.);
        RadialMenuItem alignVerticallyItem = new RadialMenuItem("Align\nvertically", "ALIGN_VERTICALLY", IconType.FA_ALIGN_CENTER);
        RadialMenuItem alignRightItem = new RadialMenuItem("Align\nright", "ALIGN_RIGHT", IconType.FA_ALIGN_RIGHT);
        RadialMenuItem alignBottomItem = new RadialMenuItem("Align\nbottom", "ALIGN_BOTTOM", IconType.FA_ALIGN_RIGHT, 90.);
        RadialMenuItem alignHorizontallyItem = new RadialMenuItem( "Align\nhorizontally", "ALIGN_HORIZONTALLY",IconType.FA_ALIGN_CENTER, 90.);
        RadialMenuItem alignLefItem = new RadialMenuItem( "Align\nleft", "ALIGN_LEFT", IconType.FA_ALIGN_LEFT);

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
