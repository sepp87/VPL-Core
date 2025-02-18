package vplcore.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;

/**
 *
 * @author Joost
 */
public class WorkspaceZoomHelper {

    private final WorkspaceModel model;
    private final WorkspaceView view;

    public WorkspaceZoomHelper(WorkspaceModel workspaceModel, WorkspaceView workspaceView) {
        this.model = workspaceModel;
        this.view = workspaceView;

        view.scaleXProperty().bind(model.zoomFactorProperty());
        view.scaleYProperty().bind(model.zoomFactorProperty());
        view.translateXProperty().bind(model.translateXProperty());
        view.translateYProperty().bind(model.translateYProperty());

    }

    public void zoomIn() {
        double newScale = model.getIncrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    public void zoomOut() {
        double newScale = model.getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void applyZoom(double newScale) {
        applyZoom(newScale, null);
    }

    public void applyZoom(double newScale, Point2D pivotPoint) {

        double oldScale = model.zoomFactorProperty().get();
        double scaleChange = (newScale / oldScale) - 1;

        // Get the bounds of the workspace
        Bounds workspaceBounds = view.getBoundsInParent();

        double dx, dy;

        if (pivotPoint != null) {
            // Calculate the distance from the zoom point (mouse cursor/graph center) to the workspace origin
            dx = pivotPoint.getX() - workspaceBounds.getMinX();
            dy = pivotPoint.getY() - workspaceBounds.getMinY();
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = view.getScene().getWidth() / 2;
            double sceneCenterY = view.getScene().getHeight() / 2;

            // Calculate the distance from the workspace's center to the scene's center
            dx = sceneCenterX - workspaceBounds.getMinX();
            dy = sceneCenterY - workspaceBounds.getMinY();
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double dX = scaleChange * dx;
        double dY = scaleChange * dy;

        double newTranslateX = model.translateXProperty().get() - dX;
        double newTranslateY = model.translateYProperty().get() - dY;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
        model.zoomFactorProperty().set(newScale);
    }

    public void zoomToFitBlockControllers(Collection<BlockController> blockControllers) {

        Scene scene = view.getScene();
        if (blockControllers.isEmpty()) {
            return;
        }

        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : blockControllers) {
            blockViews.add(blockController.getView());
        }
        
        //Zoom to fit        
        Bounds boundingBox = view.localToParent(BlockView.getBoundingBoxOfBlocks(blockViews));
        double ratioX = boundingBox.getWidth() / scene.getWidth();
        double ratioY = boundingBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double scale = Math.ceil((model.zoomFactorProperty().get() / ratio) * 10 - 1) / 10;
        scale = scale < WorkspaceModel.MIN_ZOOM ? WorkspaceModel.MIN_ZOOM : scale;
        scale = scale > WorkspaceModel.MAX_ZOOM ? WorkspaceModel.MAX_ZOOM : scale;
        model.zoomFactorProperty().set(scale);

        //Pan to fit
        boundingBox = view.localToParent(BlockView.getBoundingBoxOfBlocks(blockViews));
        double dx = (boundingBox.getMinX() + boundingBox.getWidth() / 2) - scene.getWidth() / 2;
        double dy = (boundingBox.getMinY() + boundingBox.getHeight() / 2) - scene.getHeight() / 2;
        double newTranslateX = model.translateXProperty().get() - dx;
        double newTranslateY = model.translateYProperty().get() - dy;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
    }
}
