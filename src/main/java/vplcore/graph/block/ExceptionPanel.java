package vplcore.graph.block;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import vplcore.FontAwesomeIcon;
import vplcore.workspace.WorkspaceView;

// TODO Block should llsten to when the exception panel is removed and as such decide to show the exception button again only in case the exception is still present
/**
 *
 * @author Joost
 */
public class ExceptionPanel extends InfoPanel {

    private ObservableList<BlockException> exceptions;
    private int currentIndex = 0;

    private Label messageLabel;
    private Label exceptionHeader;
    private Label exception;
    private Label severityHeader;
    private Label severity;
    private Severity highestSeverity = Severity.WARNING;

    private HBox pagingControls;
    private Label pagingLabel;
    private Button nextButton;
    private Button previousButton;

    public ExceptionPanel(WorkspaceView workspaceView, BlockController blockController) {
        super(workspaceView, blockController);

        this.exceptions = FXCollections.observableArrayList();
        this.pagingControls = buildPagingControls();
        this.infoBubble.getChildren().add(pagingControls);
        this.infoBubble.getStyleClass().add("block-exception-bubble");
        this.tail.getStyleClass().add("block-exception-tail");

    }

    @Override
    protected void setPosition() {
        double layoutX = blockView.getLayoutX() + blockView.getWidth() - 89;
        double layoutY = blockView.getLayoutY() - MAX_HEIGHT + 25;
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }

    @Override
    protected VBox buildContent() {
        VBox content = new VBox(5);
        this.severityHeader = buildHeader("Error");
        this.exceptionHeader = buildHeader("NullPointerException");
        this.severity = buildLabel("Block process stopped. Output data set to null for this item.");
        this.exception = buildLabel("Cannot invoke \"java.lang.Boolean.booleanValue()\" because \"a\" is null");

        this.messageLabel = new Label();
        messageLabel.setWrapText(true);
//        content.getChildren().add(messageLabel);
        content.getChildren().addAll(severityHeader, severity, exceptionHeader, exception);
        return content;
    }

    // Set the exceptions to be shown in the panel
    public void setExceptions(List<BlockException> exceptions) {
        this.exceptions.clear();
        this.exceptions.addAll(exceptions);
        this.currentIndex = 0;

        for (BlockException blockException : exceptions) {
            if (blockException.severity == Severity.ERROR) {
                highestSeverity = Severity.ERROR;
                break;
            }
        }

        // Update UI state
        updateLabels();
    }

    private HBox buildPagingControls() {

        this.pagingLabel = new Label("1 of 10");

        // Create paging buttons
        this.previousButton = new Button(FontAwesomeIcon.CHEVRON_LEFT.unicode());
        this.nextButton = new Button(FontAwesomeIcon.CHEVRON_RIGHT.unicode());

        // Set navigation button handlers
        previousButton.setOnAction(e -> showPreviousMessage());
        nextButton.setOnAction(e -> showNextMessage());

        HBox pagingControls = new HBox(previousButton, pagingLabel, nextButton);
        pagingControls.getStyleClass().add("block-exception-paging-controls");
        return pagingControls;
    }

    // Show the previous exception in the list
    private void showPreviousMessage() {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = exceptions.size() - 1;
        }
        updateLabels();
    }

    // Show the next exception in the list
    private void showNextMessage() {
        if (currentIndex < exceptions.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateLabels();
    }

    // Update UI to reflect the current exception and pagination
    private void updateLabels() {
        BlockException blockException = exceptions.get(currentIndex);
        messageLabel.setText(blockException.exception.getMessage());

        severityHeader.setText(buildSeverityHeader(blockException));
        exceptionHeader.setText(blockException.exception.getClass().getSimpleName());
        String severityMessage = "Block process stopped. Output data set to null";
        severityMessage += (exceptions.size() > 1) ? " for this item." : ".";
        severity.setText(severityMessage);
        exception.setText(blockException.exception().getMessage());

        pagingLabel.setText((currentIndex + 1) + " of " + exceptions.size());
        messagePane.layout(); // Force scroll pane to recompute viewport height
    }

    private String buildSeverityHeader(BlockException blockException) {
        String result = blockException.severity().toString() + " occurred";
        if (exceptions.size() == 1) {
            return result;
        }
        result += " for List" + blockException.index();

        return result;
    }

    @Override
    public void remove() {
        previousButton.setOnAction(null);
        nextButton.setOnAction(null);
        blockView.removeExceptionPanel();
        super.remove();

        // remove block info panel
        // remove block port labels
    }

    public record BlockException(String index, Severity severity, Exception exception) {

    }

    public enum Severity {
        WARNING,
        ERROR
    }

}
