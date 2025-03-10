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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private ScrollPane columnLetterScrollPane;
    private HBox columnLetterBar;
    private GridPane columnLetterGrid;

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
        columnLetterGrid = new GridPane();
        columnLetterBar = new HBox();

        // Spacer for aligning the column headers with row headers
        Region spacer = new Region();
        spacer.minWidthProperty().bind(rowHeaders.widthProperty()); // Match row header width
        spacer.setOnMouseClicked((e) -> {
            syncScrollBars();
        });

        // Wrap column letters in a ScrollPane for horizontal scrolling
        columnLetterScrollPane = new ScrollPane(columnLetterGrid);
        columnLetterScrollPane.setFitToHeight(true);
//        columnLetterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        columnLetterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        columnLetterScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        columnLetterScrollPane.setStyle("-fx-background: transparent; -fx-border-color: gray;");

        columnLetterBar.getChildren().addAll(spacer, columnLetterScrollPane);
        columnLetterBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
        columnLetterBar.setAlignment(Pos.CENTER_LEFT);

        // Style row headers
        rowHeaders.setPrefWidth(50);
        rowHeaders.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");

        // Layout structure
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
        columnLetterGrid.getChildren().clear();
    }

    private void updateTableView(DataSheet dataSheet) {
        List<String> headers = dataSheet.getHeaders();
        List<List<Object>> rows = dataSheet.getRows();

        columnLetterGrid.getChildren().clear();

        for (int i = 0; i < headers.size(); i++) {
            TableColumn<List<Object>, String> column = new TableColumn<>(headers.get(i));
            final int colIndex = i;
            column.setCellValueFactory(cellData -> Bindings.createStringBinding(()
                    -> (cellData.getValue().size() > colIndex) ? String.valueOf(cellData.getValue().get(colIndex)) : ""));

            column.setPrefWidth(100);
            column.widthProperty().addListener((obs, oldVal, newVal)
                    -> adjustColumnHeaderWidth(colIndex, newVal.doubleValue()));

            tableView.getColumns().add(column);

            // Add column letter label
            Label letterLabel = new Label(getColumnLetter(i));
            letterLabel.setAlignment(Pos.CENTER);
            letterLabel.setStyle("-fx-padding: 5px; -fx-border-color: gray;");
            letterLabel.setMinWidth(column.getWidth());
            columnLetterGrid.add(letterLabel, i, 0);
        }

        // Bind column letter grid width to table width
//        columnLetterGrid.prefWidthProperty().bind(tableView.widthProperty());
        // Fill row numbers (1, 2, 3, ...)
        rowHeaders.getItems().addAll(IntStream.rangeClosed(1, rows.size())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // Sync row height with table row height
        tableView.setRowFactory(tv -> {
            TableRow<List<Object>> row = new TableRow<>();
            row.indexProperty().addListener((obs, oldIndex, newIndex) -> {
                if (newIndex.intValue() >= 0 && newIndex.intValue() < rowHeaders.getItems().size()) {
                    row.heightProperty().addListener((obs2, oldHeight, newHeight)
                            -> adjustRowHeaderHeight(newIndex.intValue(), newHeight.doubleValue()));
                }
            });
            return row;
        });

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
        Platform.runLater(() -> {
            ScrollBar tableHScrollBar = getScrollBar(tableView, "horizontal");
            ScrollBar columnLetterHScrollBar = getScrollBar(columnLetterScrollPane, "horizontal");

            tableHScrollBar.setStyle("-fx-background-color: #ff0000;");
            columnLetterHScrollBar.setStyle("-fx-background-color: #ff0000;");

            if (tableHScrollBar != null && columnLetterHScrollBar != null) {

                columnLetterHScrollBar.valueProperty().addListener((b, o, n) -> {
                    tableHScrollBar.setValue(n.doubleValue() * tableHScrollBar.getMax());
                });

                tableHScrollBar.valueProperty().addListener((b, o, n) -> {
                    columnLetterScrollPane.setHvalue(n.doubleValue() / tableHScrollBar.getMax());
                });
            }

            ScrollBar tableVScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
            ScrollBar rowHeaderVScrollBar = (ScrollBar) rowHeaders.lookup(".scroll-bar:vertical");

            if (tableVScrollBar != null && rowHeaderVScrollBar != null) {
                rowHeaderVScrollBar.valueProperty().bindBidirectional(tableVScrollBar.valueProperty());
            }
        });
    }

    private ScrollBar getScrollBar(Control control, String orientation) {
        for (Node node : control.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (("horizontal".equals(orientation) && scrollBar.getOrientation() == javafx.geometry.Orientation.HORIZONTAL)
                        || ("vertical".equals(orientation) && scrollBar.getOrientation() == javafx.geometry.Orientation.VERTICAL)) {
                    return scrollBar;
                }
            }
        }
        return null;
    }

    private String getColumnLetter(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
        }
        return sb.toString();
    }

    private void adjustColumnHeaderWidth(int columnIndex, double newWidth) {
        if (columnIndex < columnLetterGrid.getChildren().size()) {
            Label label = (Label) columnLetterGrid.getChildren().get(columnIndex);
            label.setMinWidth(newWidth);
        }
    }

    private void adjustRowHeaderHeight(int rowIndex, double newHeight) {
        if (rowIndex < rowHeaders.getItems().size()) {
            rowHeaders.setFixedCellSize(newHeight);
        }
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
