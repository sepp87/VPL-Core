package btscore.graph.block;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import btscore.icons.FontAwesomeSolid;
import btscore.workspace.WorkspaceView;

// TODO Block should llsten to when the exception panel is removed and as such decide to show the exception button again only in case the exception is still present
/**
 *
 * @author Joost
 */
public class ExceptionPanel extends InfoPanel {

    private final ObservableList<BlockException> exceptions;
    private int currentIndex = 0;

    private Label exceptionType;
    private Label exceptionMessage;
    private Label severityHeader;
    private Label severity;
    private Severity highestSeverity = Severity.WARNING;

    private HBox pagingControls;
    private Label pagingLabel;
    private Button nextButton;
    private Button previousButton;

    public ExceptionPanel(WorkspaceView workspaceView, BlockController blockController) {
        super(workspaceView, blockController);

//        Exception e1 = new Exception("Short message! 🧐");
//        Exception e2 = new Exception("""
//                                     Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus eget odio vel purus sodales ullamcorper. Sed id suscipit ante, vitae molestie quam. Donec turpis nulla, rhoncus ac fermentum sit amet, tempus non justo. Proin mattis fringilla dui. Curabitur elementum, odio ut porta rhoncus, quam sapien fermentum augue, vitae mattis risus velit quis mauris. Nam eleifend tortor ac dignissim aliquam. In bibendum magna sed erat ultricies, id imperdiet odio ultrices. Etiam in euismod nunc. Nullam varius lacus eu est aliquet tempus. Fusce suscipit, enim vel maximus tristique, erat mauris hendrerit quam, ac convallis augue dui id nulla. Praesent convallis diam non nunc cursus feugiat. Nullam gravida, tortor a bibendum iaculis, erat mauris dapibus lacus, eu lobortis turpis enim luctus quam. Morbi sed lectus suscipit nibh lacinia viverra. Fusce laoreet tortor at risus molestie ultrices.
//                                                                           
//                                     Vivamus pellentesque eros mi, nec commodo leo sagittis mollis. Suspendisse ultricies ac nisi id facilisis. Sed ac nisl quis neque blandit vestibulum. Nunc ullamcorper odio at ante tincidunt ultrices. Aliquam nec varius sem. Donec sed convallis nibh. Donec nec ultricies tellus, at pulvinar tortor. Nullam enim dolor, malesuada sit amet libero euismod, imperdiet faucibus elit. Ut ligula dui, luctus vel venenatis at, vehicula in metus. Nunc ultricies id nunc sit amet dignissim. Maecenas et nunc lacus. Donec sit amet sapien hendrerit turpis interdum vulputate a vitae metus.
//                                                                      
//                                     Praesent non tincidunt orci. Morbi egestas ex velit, eget laoreet ipsum posuere et. Morbi tempor lacinia tincidunt. Mauris vitae arcu sed neque aliquam malesuada. Suspendisse a efficitur mi, ac vestibulum elit. Donec luctus gravida dui vel mollis. Ut gravida urna lorem, sed tincidunt elit pellentesque sed. Mauris viverra pharetra purus, nec ultricies enim rhoncus dictum. Ut odio purus, scelerisque quis arcu sed, ullamcorper tincidunt risus. Praesent ac velit ut nibh rutrum malesuada id non nulla.
//                                     """);
//        Exception e3 = new Exception("This is a mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
//        Exception e4 = new Exception("This is the second mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
//        List<ExceptionPanel.BlockException> list = new ArrayList<>();
//        list.add(new BlockException("[0]", ExceptionPanel.Severity.ERROR, e1));
//        list.add(new BlockException("[1]", ExceptionPanel.Severity.ERROR, e2));
//        list.add(new BlockException("[2]", ExceptionPanel.Severity.ERROR, e3));
//        list.add(new BlockException("[3]", ExceptionPanel.Severity.ERROR, e4));
        this.exceptions = blockController.getModel().getExceptions();
        this.exceptions.addListener(exceptionsListener);

        this.pagingControls = buildPagingControls();
        this.infoBubble.getChildren().add(pagingControls);
        this.infoBubble.getStyleClass().add("block-exception-bubble");
        this.tail.getStyleClass().add("block-exception-tail");

        updateLabels();
    }

    private final ListChangeListener<Object> exceptionsListener = (c) -> onExceptionsChanged();

    private void onExceptionsChanged() {
        if (exceptions.isEmpty()) {
            remove();
        } else {
            currentIndex = 0;
            updateLabels();
        }
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

        this.severityHeader = buildHeader("ERROR");
        this.severity = buildLabel("Block process stopped. Output data set to null for this item.");
        this.exceptionType = buildHeader("NullPointerException");
        this.exceptionMessage = buildLabel("Cannot invoke \"java.lang.Boolean.booleanValue()\" because \"a\" is null");

        content.getChildren().addAll(severityHeader, severity, exceptionType, exceptionMessage);
        return content;
    }

    private HBox buildPagingControls() {

        this.pagingLabel = new Label("1 of 10");

        // Create paging buttons
        this.previousButton = new Button(FontAwesomeSolid.CHEVRON_LEFT.unicode());
        this.nextButton = new Button(FontAwesomeSolid.CHEVRON_RIGHT.unicode());

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

        severityHeader.setText(buildSeverityHeader(blockException));
        String conflictResolution = "The process has encountered an unexpected interruption.";
        severity.setText(conflictResolution);

        exceptionType.setText(blockException.exception.getClass().getSimpleName());
        exceptionMessage.setText(blockException.exception().getMessage());

        pagingLabel.setText((currentIndex + 1) + " of " + exceptions.size());
        messagePane.layout(); // Force scroll pane to recompute viewport height
    }

    private String buildSeverityHeader(BlockException blockException) {
        String result = blockException.severity().toString() + " occurred";
        if (blockException.index == null) {
            return result;
        }
        result += " for List" + blockException.index();

        return result;
    }

    @Override
    public void remove() {
        previousButton.setOnAction(null);
        nextButton.setOnAction(null);
        super.remove();

        // remove block info panel
        // remove block port labels
    }

    public record BlockException(String index, Severity severity, Throwable exception) {

    }

    public enum Severity {
        WARNING,
        ERROR
    }

}
