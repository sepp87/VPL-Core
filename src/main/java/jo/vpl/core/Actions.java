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
        Bounds localBBox = Hub.getBoundingBoxOfHubs(workspace.hubSet);
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
        bBox = workspace.localToParent(Hub.getBoundingBoxOfHubs(workspace.hubSet));
        double deltaX = (bBox.getMinX() + bBox.getWidth() / 2) - bScene.getWidth() / 2;
        double deltaY = (bBox.getMinY() + bBox.getHeight() / 2) - bScene.getHeight() / 2;
        workspace.setTranslateX(workspace.getTranslateX() - deltaX);
        workspace.setTranslateY(workspace.getTranslateY() - deltaY);
    }

    public static void align(AlignType type, Workspace workspace) {
        Bounds bBox = Hub.getBoundingBoxOfHubs(workspace.selectedHubSet);
        switch (type) {
            case LEFT:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutX(bBox.getMinX());
                }
                break;
            case RIGHT:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutX(bBox.getMaxX() - hub.getWidth());
                }
                break;
            case TOP:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutY(bBox.getMinY());
                }
                break;
            case BOTTOM:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutY(bBox.getMaxY() - hub.getHeight());
                }
                break;
            case V_CENTER:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutX(bBox.getMaxX() - bBox.getWidth() / 2 - hub.getWidth());
                }
                break;
            case H_CENTER:
                for (Hub hub : workspace.selectedHubSet) {
                    hub.setLayoutY(bBox.getMaxY() - bBox.getHeight() / 2 - hub.getHeight());
                }
                break;
        }
    }

    public static void newFile(Workspace workspace) {
        workspace.hubSet.clear();
        workspace.connectionSet.clear();
        workspace.getChildren().clear();
    }

    public static void openFile(Workspace workspace) {
        //Clear Layout
        workspace.hubSet.clear();
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

    public static void groupHubs(Workspace workspace) {
        if (workspace.selectedHubSet.size() <= 1) {
            return;
        }

        HubGroup hubGroup = new HubGroup(workspace);
        hubGroup.setChildHubs(workspace.selectedHubSet);
    }

    public static void copyHubs(Workspace workspace) {
        workspace.tempHubSet = FXCollections.observableSet();

        for (Hub hub : workspace.selectedHubSet) {
            workspace.tempHubSet.add(hub);
        }
    }

    public static void pasteHubs(Workspace workspace) {
        Bounds bBox = Hub.getBoundingBoxOfHubs(workspace.tempHubSet);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        double pastePointX = MouseInfo.getPointerInfo().getLocation().x;
        double pastePointY = MouseInfo.getPointerInfo().getLocation().y;
        Point2D pastePoint = workspace.screenToLocal(pastePointX, pastePointY);

        pastePoint = workspace.mousePosition;

        Point2D delta = pastePoint.subtract(copyPoint);

        //First deselect selected hubs. Simply said, deselect copied hubs.
        for (Hub hub : workspace.selectedHubSet) {
            hub.setSelected(false);
        }
        workspace.selectedHubSet.clear();

        List<Connection> alreadyClonedConnectors = new ArrayList<>();
        List<CopyConnection> copyConnections = new ArrayList<>();

        // copy hub from clipboard to canvas
        for (Hub hub : workspace.tempHubSet) {
            Hub newHub = hub.clone();

            newHub.setLayoutX(hub.getLayoutX() + delta.getX());
            newHub.setLayoutY(hub.getLayoutY() + delta.getY());

            workspace.getChildren().add(newHub);
            workspace.hubSet.add(newHub);

            //Set pasted hub(s) as selected
            workspace.selectedHubSet.add(newHub);
            newHub.setSelected(true);

            copyConnections.add(new CopyConnection(hub, newHub));
        }

        for (CopyConnection cc : copyConnections) {
            int counter = 0;

            for (Port port : cc.oldHub.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyClonedConnectors.contains(connection)) {
                        Connection newConnection = null;

                        // start and end hub are contained in selection
                        if (workspace.tempHubSet.contains(connection.startPort.parentHub)) {
                            CopyConnection cc2 = copyConnections
                                    .stream()
                                    .filter(i -> i.oldHub == connection.startPort.parentHub)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                newConnection = new Connection(workspace, cc2.newHub.outPorts.get(0), cc.newHub.inPorts.get(counter));
                            }
                        } else {
                            // only end hub is contained in selection
                            newConnection = new Connection(workspace, connection.startPort, cc.newHub.inPorts.get(counter));
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
