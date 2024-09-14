package vplcore.workspace.input;

import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import vplcore.Util;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardInputHandler {

    private final Workspace workspace;
    private boolean init = false;

    public KeyboardInputHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventHandler(KeyEvent.KEY_PRESSED, handleKeyPressed);
    }

    private final EventHandler<KeyEvent> handleKeyPressed = new EventHandler<>() {

        @Override
        public void handle(KeyEvent keyEvent) {
            boolean isModifierDown = Util.isModifierDown(keyEvent);

            switch (keyEvent.getCode()) {
                case BACK_SPACE:
                    Actions.deleteSelectedBlocks(workspace);
                    break;
                case DELETE:
                    Actions.deleteSelectedBlocks(workspace);
                    break;
                case C:
                    if (isModifierDown) {
                        Actions.copyBlocks(workspace);
                    }
                    break;
                case V:
                    if (isModifierDown) {
                        Actions.pasteBlocks(workspace);
                    }
                    break;
                case G:
                    if (isModifierDown) {
                        Actions.groupBlocks(workspace);
                    }
                    break;
                case N:
                    if (isModifierDown) {
                        Actions.newFile(workspace);
                    }
                    break;
                case S:
                    if (isModifierDown) {
                        Actions.saveFile(workspace);
                    }
                    break;
                case O:
                    if (isModifierDown) {
                        Actions.openFile(workspace);
                    }
                    break;
                case A:
                    if (isModifierDown) {
                        Actions.selectAllBlocks(workspace);
                    }
                    break;
            }
        }
    };



}
