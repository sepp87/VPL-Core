package vpllib.spreadsheet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author joostmeulenkamp
 */
@BlockMetadata(
        identifier = "Spreadsheet.tableView",
        category = "Spreadsheet",
        description = "View the spreadsheet data in the data sheet with this table view.",
        tags = {"tableView", "spreadsheet"})
public class TableViewBlock extends BlockModel {

    private TableView<List<Object>> tableView;
    private ListView<String> rowHeaders;
    private HBox columnLetterBar;

    public TableViewBlock(WorkspaceModel workspace) {
        super(workspace);
        nameProperty().set("Table");
        resizableProperty().set(true);
        addInputPort("Data", DataSheet.class);
    }

    @Override
    protected void initialize() {
    }

    @Override
    public Region getCustomization() {
        tableView = new TableView<>();
        rowHeaders = new ListView<>();
        columnLetterBar = new HBox();

        // Style headers
        rowHeaders.setPrefWidth(50);
        rowHeaders.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
        columnLetterBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
        columnLetterBar.setAlignment(Pos.CENTER_LEFT);

        // Create a spacer for alignment
        Label spacer = new Label();
        spacer.setPrefWidth(rowHeaders.getPrefWidth());

        columnLetterBar.getChildren().add(spacer); // Add spacer first

        // Wrap everything into a layout
        BorderPane layout = new BorderPane();
        layout.setTop(columnLetterBar);
        layout.setLeft(rowHeaders);
        layout.setCenter(tableView);

        return layout;
    }

    @Override
    protected void process() throws Exception {
        DataSheet dataSheet = (DataSheet) inputPorts.get(0).getData();

        // Clear the table once before adding new data
        clearTableView();

        if (dataSheet != null) {
            updateTableView(dataSheet);
            syncScrollBars();

        }
    }

    private void clearTableView() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        rowHeaders.getItems().clear();
//        columnLetterBar.getChildren().clear();
        columnLetterBar.getChildren().removeIf(node -> node != columnLetterBar.getChildren().get(0)); // Keep the spacer

    }

    private void updateTableView(DataSheet dataSheet) {
        List<String> headers = dataSheet.getHeaders();
        List<List<Object>> rows = dataSheet.getRows();

        // Create column letter indicators (A, B, C...)
        for (int i = 0; i < headers.size(); i++) {
            // Create TableColumn with original header name
            TableColumn<List<Object>, String> column = new TableColumn<>(headers.get(i));
            final int colIndex = i;
            column.setCellValueFactory(cellData -> Bindings.createStringBinding(()
                    -> (cellData.getValue().size() > colIndex) ? String.valueOf(cellData.getValue().get(colIndex)) : ""));
            column.setPrefWidth(100);
            tableView.getColumns().add(column);

            // Add letter-based headers (A, B, C, ...)
            Label letterLabel = new Label(getColumnLetter(i));
            letterLabel.setPrefWidth(100);
            letterLabel.setAlignment(Pos.CENTER);
            letterLabel.setStyle("-fx-padding: 5px; -fx-border-color: gray;");
            columnLetterBar.getChildren().add(letterLabel);
        }

        // Fill row numbers (1, 2, 3, ...)
        rowHeaders.getItems().addAll(IntStream.rangeClosed(1, rows.size())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // Add rows to the table
        tableView.getItems().addAll(rows);
    }

    private void adjustColumnWidth() {
        Platform.runLater(() -> {
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                column.setPrefWidth(Math.min(column.getWidth(), 120)); // Auto-size but cap at 120px
            }
        });
    }

    private void syncScrollBars() {
        ScrollBar tableScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
        ScrollBar rowHeaderScrollBar = (ScrollBar) rowHeaders.lookup(".scroll-bar:vertical");

        if (tableScrollBar != null && rowHeaderScrollBar != null) {
            rowHeaderScrollBar.valueProperty().bindBidirectional(tableScrollBar.valueProperty());
        }
    }

    private String getColumnLetter(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
        }
        return sb.toString();
    }

    @Override
    public BlockModel copy() {
        TableViewBlock tableViewBlock = new TableViewBlock(workspace);
        return tableViewBlock;
    }

    @Override
    protected void onRemoved() {
    }

}
