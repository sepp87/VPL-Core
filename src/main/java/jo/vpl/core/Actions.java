package jo.vpl.core;

import java.awt.MouseInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static jo.vpl.core.AlignType.BOTTOM;
import static jo.vpl.core.AlignType.H_CENTER;
import static jo.vpl.core.AlignType.LEFT;
import static jo.vpl.core.AlignType.RIGHT;
import static jo.vpl.core.AlignType.TOP;
import static jo.vpl.core.AlignType.V_CENTER;

/**
 *
 * @author joostmeulenkamp
 */
public class Actions {

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

    public static void align(AlignType type, Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        switch (type) {
            case LEFT:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutX(bBox.getMinX());
                }
                break;
            case RIGHT:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutX(bBox.getMaxX() - block.getWidth());
                }
                break;
            case TOP:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutY(bBox.getMinY());
                }
                break;
            case BOTTOM:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutY(bBox.getMaxY() - block.getHeight());
                }
                break;
            case V_CENTER:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutX(bBox.getMaxX() - bBox.getWidth() / 2 - block.getWidth());
                }
                break;
            case H_CENTER:
                for (Block block : workspace.selectedBlockSet) {
                    block.setLayoutY(bBox.getMaxY() - bBox.getHeight() / 2 - block.getHeight());
                }
                break;
        }
    }

    public static void newFile(Workspace workspace) {
        workspace.blockSet.clear();
        workspace.connectionSet.clear();
        workspace.getChildren().clear();
    }

    public static void openFile(Workspace workspace) {
        //Clear Layout
        workspace.blockSet.clear();
        workspace.connectionSet.clear();
        workspace.getChildren().clear();

        //Open File
        Stage stage = (Stage) workspace.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            GraphLoader.deserialize(file, workspace);
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

    public static void groupBlocks(Workspace workspace) {
        if (workspace.selectedBlockSet.size() <= 1) {
            return;
        }

        BlockGroup blockGroup = new BlockGroup(workspace);
        blockGroup.setChildBlocks(workspace.selectedBlockSet);
    }

    public static void copyBlocks(Workspace workspace) {
        workspace.tempBlockSet = FXCollections.observableSet();

        for (Block block : workspace.selectedBlockSet) {
            workspace.tempBlockSet.add(block);
        }
    }

    public static void pasteBlocks(Workspace workspace) {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.tempBlockSet);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        double pastePointX = MouseInfo.getPointerInfo().getLocation().x;
        double pastePointY = MouseInfo.getPointerInfo().getLocation().y;
        Point2D pastePoint = workspace.screenToLocal(pastePointX, pastePointY);

        pastePoint = workspace.mousePosition;

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
}
