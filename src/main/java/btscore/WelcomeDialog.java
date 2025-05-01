package btscore;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author joostmeulenkamp
 */
public class WelcomeDialog extends VBox {

    // Static final strings for the dialog content
    private static final String QUICK_START_CONTENT = """
Welcome to BlockSmith! Here’s how to get started: 
                                                      
Blocks generate, process, and output data. Double-click anywhere on the canvas to add a block. Each block does something specific, like entering a number or doing math. Need help? Click the (i) icon on a block to learn more about it.
                                                      
Connect blocks by linking their ports. Hover over a port to see what kind of data a port accepts or produces. Example: A multiply block needs two numbers and gives you the result. That’s it! You’re ready to build your first graph.

                                                      """;

    private static final String[] CONTROLS = {
        "Right drag: pan",
        "Scroll: zoom",
        "Left double click: create blocks",
        "Left click: select a block",
        "Left click + CMD/CTRL: multi select blocks",
        "Left drag: selection rectangle",
        "Left drag block: move selected block(s)",
        "Left click port: connect blocks",
        "Left click connection: remove connection",
        "Right click: opens radial menu"
    };

    private static final String[] SHORTCUTS = {
        "CMD/CTRL-A: select all blocks",
        "CMD/CTRL-C: copy selected blocks",
        "CMD/CTRL-V: paste copied blocks",
        "CMD/CTRL-G: group selected blocks",
        "CMD/CTRL-O: load graph",
        "CMD/CTRL-S: save graph",
        "CMD/CTRL-N: new graph",
        "CMD/CTRL-PLUS: zoom in",
        "CMD/CTRL-MINUS: zoom out",
        "CMD/CTRL-Z: undo",
        "CMD/CTRL-Y: redo",
        "Space: zoom to fit",
        "Del/Backspace: delete selected blocks"
    };

    public WelcomeDialog(Stage parentStage) {
//        this.setPrefSize(520, 600); // Size of the dialog window

        // Content container
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Quick Start
        Label quickStartHeader = new Label("Quick Start");
        quickStartHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label quickStartContent = new Label(QUICK_START_CONTENT);
        quickStartContent.setWrapText(true);

        // Controls
        Label controlsHeader = new Label("Controls");
        controlsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Node controlsList = createLabeledList(CONTROLS);

        // Shortcuts
        Label shortcutsHeader = new Label("Shortcuts");
        shortcutsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Node shortcutsList = createLabeledList(SHORTCUTS);

        // Show on start + close button
        CheckBox showOnStartCheckbox = new CheckBox("Show this help dialog on startup");
        showOnStartCheckbox.setSelected(true);
        showOnStartCheckbox.setFocusTraversable(false);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> ((Stage) this.getScene().getWindow()).close());

        HBox footer = new HBox(10, showOnStartCheckbox, closeButton);
        footer.setPadding(new Insets(10, 0, 0, 0));

        // Add all content
        content.getChildren().addAll(
                quickStartHeader, quickStartContent,
                controlsHeader, controlsList,
                shortcutsHeader, shortcutsList,
                footer
        );

        this.getChildren().add(content);
    }

    // Helper method to create a labeled list from an array
    private Node createLabeledList(String[] items) {
        VBox box = new VBox(4); // spacing between items
        box.setPrefWidth(480);

        for (String item : items) {
            Label label = new Label("• " + item);
            label.setWrapText(true);
            label.setMaxWidth(460);
            box.getChildren().add(label);
        }
        
        box.getChildren().add(new Label());

        return box;
    }

    // Static method to show the WelcomeDialog
    public static void show(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Welcome");

        WelcomeDialog welcomeView = new WelcomeDialog(owner);
        Scene scene = new Scene(welcomeView, 520, 870);
        dialog.setScene(scene);
        dialog.show();
    }
}
