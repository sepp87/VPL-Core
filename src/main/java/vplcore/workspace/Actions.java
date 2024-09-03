package vplcore.workspace;

import vplcore.graph.model.Port;
import vplcore.graph.model.Connection;
import vplcore.graph.util.CopyConnection;
import vplcore.graph.io.GraphSaver;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import java.awt.MouseInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author joostmeulenkamp
 */
public class Actions {

    public enum ActionType {
        ALIGN_BOTTOM,
        ALIGN_HORIZONTALLY,
        ALIGN_LEFT,
        ALIGN_RIGHT,
        ALIGN_TOP,
        ALIGN_VERTICALLY,
        COPY_BLOCKS,
        DELETE_BLOCKS,
        DESELECT_ALL_BLOCKS,
        GROUP_BLOCKS,
        NEW_FILE,
        OPEN_FILE,
        PASTE_BLOCKS,
        SAVE_FILE,
        SELECT_ALL_BLOCKS,
        ZOOM_IN,
        ZOOM_OUT,
        ZOOM_TO_FIT,
    }

    private final Workspace workspace;

    public Actions(Workspace workspace) {
        this.workspace = workspace;
    }

    public void perform(ActionType actionType) {
        switch (actionType) {
            case ALIGN_BOTTOM:
                alignBottom(workspace);
                break;
            case ALIGN_HORIZONTALLY:
                alignHorizontally(workspace);
                break;
            case ALIGN_LEFT:
                alignLeft(workspace);
                break;
            case ALIGN_RIGHT:
                alignRight(workspace);
                break;
            case ALIGN_TOP:
                alignTop(workspace);
                break;
            case ALIGN_VERTICALLY:
                alignVertically(workspace);
                break;
            case COPY_BLOCKS:
                copyBlocks(workspace);
                break;
            case DELETE_BLOCKS:
                break;
            case GROUP_BLOCKS:
                groupBlocks(workspace);
                break;
            case NEW_FILE:
                newFile(workspace);
                break;
            case OPEN_FILE:
                openFile(workspace);
                break;
            case PASTE_BLOCKS:
                pasteBlocks(workspace);
                break;
            case SAVE_FILE:
                saveFile(workspace);
                break;
            case ZOOM_IN:
                break;
            case ZOOM_OUT:
                break;
            case ZOOM_TO_FIT:
                zoomToFit(workspace);
                break;
        }
    }

    public static void alignBottom(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutY(bBox.getMaxY() - block.getHeight());
        }
    }

    public static void alignHorizontally(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutY(bBox.getMaxY() - bBox.getHeight() / 2 - block.getHeight());
        }
    }

    public static void alignLeft(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutX(bBox.getMinX());
        }
    }

    public static void alignRight(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutX(bBox.getMaxX() - block.getWidth());
        }
    }

    public static void alignTop(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutY(bBox.getMinY());
        }
    }

    public static void alignVertically(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutX(bBox.getMaxX() - bBox.getWidth() / 2 - block.getWidth());
        }
    }

    public static void copyBlocks(Workspace workspace) {
        workspace.tempBlockSet = FXCollections.observableSet();

        for (Block block : workspace.selectedBlockSet) {
            workspace.tempBlockSet.add(block);
        }
    }

    public static void deleteSelectedBlocks(Workspace workspace) {
        for (Block block : workspace.selectedBlockSet) {
            block.delete();
        }
        workspace.selectedBlockSet.clear();
    }

    public static void deselectAllBlocks(Workspace workspace) {
        for (Block block : workspace.selectedBlockSet) {
            block.setSelected(false);
        }
        workspace.selectedBlockSet.clear();
    }

    public static void groupBlocks(Workspace workspace) {
        if (workspace.selectedBlockSet.size() <= 1) {
            return;
        }

        BlockGroup blockGroup = new BlockGroup(workspace);
        blockGroup.setChildBlocks(workspace.selectedBlockSet);
    }

    public static void newFile(Workspace workspace) {
        workspace.blockSet.clear();
        workspace.connectionSet.clear();
        resetWorkspace(workspace);
    }

    public static void openFile(Workspace workspace) {

        //Open File
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        //Clear Layout
        workspace.blockSet.clear();
        workspace.connectionSet.clear();
        resetWorkspace(workspace);

        //Load file
        GraphLoader.deserialize(file, workspace);

    }

    private static void resetWorkspace(Workspace workspace) {
        workspace.getChildren().clear();
        workspace.getChildren().add(workspace.selectBlockHandler.getSelectBlock());
        workspace.getChildren().add(workspace.portDisconnector.getRemoveButton());
    }

    public static void pasteBlocks(Workspace workspace) {
        if (workspace.tempBlockSet == null || workspace.tempBlockSet.isEmpty()) {
            return;
        }

        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.tempBlockSet);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        double pastePointX = MouseInfo.getPointerInfo().getLocation().x;
        double pastePointY = MouseInfo.getPointerInfo().getLocation().y;
        Point2D pastePoint = workspace.screenToLocal(pastePointX, pastePointY);

        pastePoint = workspace.mouse.getPosition();

        Point2D delta = pastePoint.subtract(copyPoint);

        //First deselect selected blocks. Simply said, deselect copied blocks.
        for (Block block : workspace.selectedBlockSet) {
            block.setSelected(false);
        }
        workspace.selectedBlockSet.clear();

        List<Connection> alreadyClonedConnectors = new ArrayList<>();
        List<CopyConnection> copyConnections = new ArrayList<>();

        // copy block from clipboard to canvas
        for (Block block : workspace.tempBlockSet) {
            Block newBlock = block.clone();

            newBlock.setLayoutX(block.getLayoutX() + delta.getX());
            newBlock.setLayoutY(block.getLayoutY() + delta.getY());

            workspace.getChildren().add(newBlock);
            workspace.blockSet.add(newBlock);

            //Set pasted block(s) as selected
            workspace.selectedBlockSet.add(newBlock);
            newBlock.setSelected(true);

            copyConnections.add(new CopyConnection(block, newBlock));
        }

        for (CopyConnection cc : copyConnections) {
            int counter = 0;

            for (Port port : cc.oldBlock.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyClonedConnectors.contains(connection)) {
                        Connection newConnection = null;

                        // start and end block are contained in selection
                        if (workspace.tempBlockSet.contains(connection.startPort.parentBlock)) {
                            CopyConnection cc2 = copyConnections
                                    .stream()
                                    .filter(i -> i.oldBlock == connection.startPort.parentBlock)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                newConnection = new Connection(workspace, cc2.newBlock.outPorts.get(0), cc.newBlock.inPorts.get(counter));
                            }
                        } else {
                            // only end block is contained in selection
                            newConnection = new Connection(workspace, connection.startPort, cc.newBlock.inPorts.get(counter));
                        }

                        if (newConnection != null) {
                            alreadyClonedConnectors.add(connection);
                            workspace.connectionSet.add(newConnection);
                        }
                    }
                }
                counter++;
            }
        }
    }

    public static void saveFile(Workspace workspace) {
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            GraphSaver.serialize(file, workspace);
        }
    }

    public static void selectAllBlocks(Workspace workspace) {
        workspace.selectedBlockSet.clear();
        for (Block block : workspace.blockSet) {
            block.setSelected(true);
            workspace.selectedBlockSet.add(block);
        }

    }

    public static void zoomToFit(Workspace workspace) {

        Scene bScene = workspace.getScene();
        Bounds localBBox = Block.getBoundingBoxOfBlocks(workspace.blockSet);
        if (localBBox == null) {
            return;
        }

        //Zoom to fit        
        Bounds bBox = workspace.localToParent(localBBox);
        double ratioX = bBox.getWidth() / bScene.getWidth();
        double ratioY = bBox.getHeight() / bScene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        workspace.setScale((workspace.getScale() / ratio) - 0.03); //little extra zoom out, not to touch the borders

        //Pan to fit
        bBox = workspace.localToParent(Block.getBoundingBoxOfBlocks(workspace.blockSet));
        double deltaX = (bBox.getMinX() + bBox.getWidth() / 2) - bScene.getWidth() / 2;
        double deltaY = (bBox.getMinY() + bBox.getHeight() / 2) - bScene.getHeight() / 2;
        workspace.setTranslateX(workspace.getTranslateX() - deltaX);
        workspace.setTranslateY(workspace.getTranslateY() - deltaY);
    }

}
