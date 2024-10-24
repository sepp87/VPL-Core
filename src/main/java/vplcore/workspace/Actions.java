package vplcore.workspace;

import vplcore.graph.model.Port;
import vplcore.graph.model.Connection;
import vplcore.graph.util.CopyConnection;
import vplcore.graph.io.GraphSaver;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vplcore.editor.ZoomController;
import vplcore.editor.ZoomModel;

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
        DELETE_SELECTED_BLOCKS,
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

    private final ZoomModel zoomModel;
    private final Workspace workspace;
    private final ZoomController zoomController;

    public Actions(Workspace workspace, ZoomController zoomController, ZoomModel zoomModel) {
        this.zoomController = zoomController;
        this.zoomModel = zoomModel;
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
            case DELETE_SELECTED_BLOCKS:
                deleteSelectedBlocks(workspace);
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
                saveFile(workspace, zoomModel);
                break;
            case ZOOM_IN:
                zoomController.incrementZoom();
                break;
            case ZOOM_OUT:
                zoomController.decrementZoom();
                break;
            case ZOOM_TO_FIT:
                zoomController.zoomToFit();
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
        workspace.reset();
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
        workspace.reset();

        //Load file
        GraphLoader.deserialize(file, workspace);

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
        Point2D pastePoint = workspace.mouse.getPosition();

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

    public static void saveFile(Workspace workspace, ZoomModel zoomModel) {
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            GraphSaver.serialize(file, workspace, zoomModel);
        }
    }

    public static void selectAllBlocks(Workspace workspace) {
        workspace.selectedBlockSet.clear();
        for (Block block : workspace.blockSet) {
            block.setSelected(true);
            workspace.selectedBlockSet.add(block);
        }

    }

}
