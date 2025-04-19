package btscore.workspace;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import btscore.graph.block.BlockController;
import btscore.graph.block.ExceptionPanel;
import btscore.graph.block.InfoPanel;

/**
 *
 * @author JoostMeulenkamp
 */
public class InfoPanelHelper {

    private final WorkspaceView view;

    private BlockController activeBlockController;
    private InfoPanel activeBlockInfoPanel;

    public InfoPanelHelper(WorkspaceView workspaceView) {
        this.view = workspaceView;
    }

    public void showInfoPanel(BlockController blockController) {
        InfoPanel infoPanel = new InfoPanel(view, blockController);
        setInfoPanel(blockController, infoPanel);
    }

    public void showExceptionPanel(BlockController blockController) {
        ExceptionPanel exceptionPanel = new ExceptionPanel(view, blockController);
        setInfoPanel(blockController, exceptionPanel);
    }

    private void setInfoPanel(BlockController blockController, InfoPanel infoPanel) {
        if (activeBlockInfoPanel != null) {
            activeBlockInfoPanel.remove(); // another info panel is about to be shown
        }
        activeBlockInfoPanel = infoPanel;
        activeBlockController = blockController;

        activeBlockController.getView().layoutXProperty().addListener(layoutXListener);
        activeBlockController.getView().widthProperty().addListener(layoutXListener);
        activeBlockController.getView().layoutYProperty().addListener(layoutYListener);
        activeBlockController.getModel().removedProperty().addListener(removedListener);
        activeBlockInfoPanel.removedProperty().addListener(removedListener);

        view.getInfoLayer().getChildren().add(infoPanel);
    }

    private final ChangeListener<Number> layoutXListener = this::onLayoutXChanged;

    private void onLayoutXChanged(Object b, Number o, Number n) {
        double delta = n.doubleValue() - o.doubleValue();
        activeBlockInfoPanel.move(delta, 0);
    }

    private final ChangeListener<Number> layoutYListener = this::onLayoutYChanged;

    private void onLayoutYChanged(Object b, Number o, Number n) {
        double delta = n.doubleValue() - o.doubleValue();
        activeBlockInfoPanel.move(0, delta);
    }

    private final ChangeListener<Boolean> removedListener = this::onRemoved; // in the case the info panel is removed by itself

    private void onRemoved(Observable b, Boolean o, boolean n) {
        hideInfoPanel();
    }

    private void hideInfoPanel() {
        activeBlockController.getView().layoutXProperty().removeListener(layoutXListener);
        activeBlockController.getView().widthProperty().removeListener(layoutXListener);
        activeBlockController.getView().layoutYProperty().removeListener(layoutYListener);
        activeBlockController.getModel().removedProperty().removeListener(removedListener);
        activeBlockInfoPanel.removedProperty().removeListener(removedListener);

        view.getInfoLayer().getChildren().remove(activeBlockInfoPanel);
        System.out.println(activeBlockInfoPanel.getClass());

        if (activeBlockInfoPanel instanceof ExceptionPanel) {
            System.out.println("Exception Panel ");
            activeBlockController.onExceptionPanelRemoved();
//            activeBlockController.showExceptionButton();
        } else {
            activeBlockController.onInfoPanelRemoved();
        }
        activeBlockInfoPanel = null;
        activeBlockController = null;
    }

}
