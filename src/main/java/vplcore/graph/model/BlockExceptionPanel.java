package vplcore.graph.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// TODO Block should llsten to when the exception panel is removed and as such decide to show the exception button again only in case the exception is still present
/**
 *
 * @author Joost
 */
public class BlockExceptionPanel extends BlockInfoPanel {

    private final List<BlockException> exceptions = new ArrayList<>();
    private int currentIndex = 0;

    private Label messageLabel;

    private HBox pagingControls;
    private Label pageLabel;
    private Button nextButton;
    private Button previousButton;
    private Severity highestSeverity;

    public BlockExceptionPanel(Block block) {
        super(block);

        this.pagingControls = buildPagingControls();
        this.infoBubble.getChildren().add(pagingControls);
    }

    @Override
    protected void setPosition() {
        double layoutX = block.getLayoutX() + block.getWidth() - 89;
        double layoutY = block.getLayoutY() - MAX_HEIGHT + 25;
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
    }

    @Override
    protected VBox buildContent() {
        VBox content = new VBox(5);
        this.messageLabel = new Label();
        messageLabel.setWrapText(true);
        content.getChildren().add(messageLabel);
        return content;
    }

    // Set the exceptions to be shown in the panel
    public void setExceptions(List<BlockException> exceptions) {
        this.exceptions.clear();
        this.exceptions.addAll(exceptions);
        this.currentIndex = 0;

        // Update UI state
        updateLabels();
    }

    private HBox buildPagingControls() {

        this.pageLabel = new Label("1 of 10");

        // Create paging buttons
        this.previousButton = new Button("<");
        previousButton.getStyleClass().add("block-info-navigation-button");
        this.nextButton = new Button(">");
        nextButton.getStyleClass().add("block-info-navigation-button");

        // Set navigation button handlers
        previousButton.setOnAction(e -> showPreviousMessage());
        nextButton.setOnAction(e -> showNextMessage());

        HBox navigationBox = new HBox(previousButton, pageLabel, nextButton);
        return navigationBox;
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
        messageLabel.setText(exceptions.get(currentIndex).exception.getMessage());
        pageLabel.setText((currentIndex + 1) + " of " + exceptions.size());
        messagePane.layout(); // Force scroll pane to recompute viewport height
    }

    @Override
    public void delete() {
        super.delete();
        previousButton.setOnAction(null);
        nextButton.setOnAction(null);
        block.exceptionPanel = null;
        block.exceptionButton.setVisible(true);
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
