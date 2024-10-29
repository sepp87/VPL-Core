package vplcore.editor;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import vplcore.Util;
import vplcore.workspace.Actions;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {

    private final Actions actions;

    public KeyboardController(Actions actions) {
        this.actions = actions;
    }

    public void processEditorShortcutAction(KeyEvent event) {
        boolean isModifierDown = Util.isModifierDown(event);
        switch (event.getCode()) {
            case BACK_SPACE:
            case DELETE:
                actions.perform(Actions.ActionType.DELETE_SELECTED_BLOCKS);
                break;
            case C:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.COPY_BLOCKS);
                }
                break;
            case V:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.PASTE_BLOCKS);
                }
                break;
            case G:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.GROUP_BLOCKS);
                }
                break;
            case N:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.NEW_FILE);
                }
                break;
            case S:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.SAVE_FILE);
                }
                break;
            case O:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.OPEN_FILE);
                }
                break;
            case A:
                if (isModifierDown) {
                    actions.perform(Actions.ActionType.SELECT_ALL_BLOCKS);
                }
                break;
        }
    }
}
