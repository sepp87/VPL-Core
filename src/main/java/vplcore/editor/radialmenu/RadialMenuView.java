package vplcore.editor.radialmenu;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Group;
import vplcore.IconType;
import vplcore.workspace.Actions.ActionType;
import vplcore.editor.radialmenu.RadialMenu;
import vplcore.editor.radialmenu.RadialMenuItem;
import vplcore.editor.radialmenu.RadialSubMenu;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuView extends Group {

    private RadialMenu radialMenu;
    
    public RadialMenuView() {
        this.radialMenu = buildRadialMenu();
        this.radialMenu.setVisible(false);
        this.getChildren().add(radialMenu);
    }

    private RadialMenu buildRadialMenu() {
        // create menu items
        RadialMenuItem<ActionType> openFileItem = new RadialMenuItem<>(ActionType.OPEN_FILE, IconType.FA_FOLDER_OPEN_O, "Open\nfile");
        RadialMenuItem<ActionType> saveFileItem = new RadialMenuItem<>(ActionType.SAVE_FILE, IconType.FA_FLOPPY_O, "Save\nfile");
        RadialMenuItem<ActionType> zoomToFitItem = new RadialMenuItem<>(ActionType.ZOOM_TO_FIT, IconType.FA_SEARCH, "Zoom\nto fit");
        RadialSubMenu<?> alignItem = new RadialSubMenu<>(IconType.FA_SORT_AMOUNT_ASC, "Align");
        RadialMenuItem<ActionType> copyItem = new RadialMenuItem<>(ActionType.COPY_BLOCKS, IconType.FA_CLONE, "Copy");
        RadialMenuItem<ActionType> pasteItem = new RadialMenuItem<>(ActionType.PASTE_BLOCKS, IconType.FA_CLIPBOARD, "Paste");
        RadialMenuItem<ActionType> groupItem = new RadialMenuItem<>(ActionType.GROUP_BLOCKS, IconType.FA_OBJECT_GROUP, "Group");
        RadialMenuItem<ActionType> newFileItem = new RadialMenuItem<>(ActionType.NEW_FILE, IconType.FA_FILE_O, "New\nfile");

        // create menu
        List<RadialMenuItem<?>> items = new ArrayList<>();
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
        List<RadialMenuItem<?>> alignItems = buildAlignSubMenu();
        menu.addSubMenu(alignItem, alignItems);

        return menu;
    }

    private List<RadialMenuItem<?>> buildAlignSubMenu() {
        List<RadialMenuItem<?>> alignItems = new ArrayList<>();
        RadialMenuItem<ActionType> alignTopItem = new RadialMenuItem<>(ActionType.ALIGN_TOP, IconType.FA_ALIGN_LEFT, "Align\ntop", 90.);
        RadialMenuItem<ActionType> alignVerticallyItem = new RadialMenuItem<>(ActionType.ALIGN_VERTICALLY, IconType.FA_ALIGN_CENTER, "Align\nvertically");
        RadialMenuItem<ActionType> alignRightItem = new RadialMenuItem<>(ActionType.ALIGN_RIGHT, IconType.FA_ALIGN_RIGHT, "Align\nright");
        RadialMenuItem<ActionType> alignBottomItem = new RadialMenuItem<>(ActionType.ALIGN_BOTTOM, IconType.FA_ALIGN_RIGHT, "Align\nbottom", 90.);
        RadialMenuItem<ActionType> alignHorizontallyItem = new RadialMenuItem<>(ActionType.ALIGN_HORIZONTALLY, IconType.FA_ALIGN_CENTER, "Align\nhorizontally", 90.);
        RadialMenuItem<ActionType> alignLefItem = new RadialMenuItem<>(ActionType.ALIGN_LEFT, IconType.FA_ALIGN_LEFT, "Align\nleft");

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
    
    public List<RadialMenuItem<?>> getAllRadialMenuItems() {
        return radialMenu.getAllItems();
    }

}
