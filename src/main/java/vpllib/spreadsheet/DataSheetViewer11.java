package vpllib.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;

/**
 *
 * @author joostmeulenkamp
 */
public class DataSheetViewer11 extends BorderPane {

    private final TableView<List<Object>> tableView;
    private final ListView<String> rowHeaders;
    private final VBox rowHeadersBar;
    private final ScrollPane columnLetterScrollPane;
    private final HBox columnLetterBar;
    private final GridPane columnLetterGrid;
    private final Region spacerHorizontal;
    private final Region spacerVertical;
    private ScrollBar tableVScrollBar;
    private ScrollBar rowHeaderVScrollBar;

    public DataSheetViewer11() {
        tableView = new TableView<>();
        rowHeaders = new ListView<>();
        rowHeadersBar = new VBox();
        columnLetterGrid = new GridPane();
        columnLetterBar = new HBox();

        // Detect Column Sorting - Ascending and Unsorted
        tableView.getSortOrder().addListener(tableViewSortListener);
        tableView.setPrefWidth(425);

        // Spacer for aligning the column headers with row headers
        spacerHorizontal = new Region();
        spacerHorizontal.minWidthProperty().bind(rowHeaders.widthProperty()); // Match row header width

        // Wrap column letters in a ScrollPane for horizontal scrolling
        columnLetterScrollPane = new ScrollPane(columnLetterGrid);
        columnLetterScrollPane.setFitToHeight(true);
        columnLetterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        columnLetterScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        columnLetterScrollPane.setStyle("-fx-background: transparent; -fx-border-color: gray;");

        columnLetterBar.getChildren().addAll(spacerHorizontal, columnLetterScrollPane);
        columnLetterBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
        columnLetterBar.setAlignment(Pos.CENTER_LEFT);

        // Spacer for aligning the column headers with row headers
        spacerVertical = new Region();
        spacerVertical.setMinHeight(25); // Match column header height

        // Style row headers
        rowHeaders.setPrefWidth(50);
        rowHeaders.setStyle("-fx-background-color: #f0f0f0;");

        rowHeadersBar.getChildren().addAll(spacerVertical, rowHeaders);

        // Layout structure
        this.setTop(columnLetterBar);
        this.setLeft(rowHeadersBar);
        this.setCenter(tableView);
    }

    public void setDataSheet(DataSheet dataSheet) {
        clearTableView();
        if (dataSheet == null) {
            return;
        }
        updateTableView(dataSheet);
        syncScrollBars();
    }

    public void remove() {
        tableView.getSortOrder().removeListener(tableViewSortListener);
        spacerHorizontal.minWidthProperty().unbind();
        clearTableView();
    }

    private final ListChangeListener<TableColumn<List<Object>, ?>> tableViewSortListener = this::onTableViewSorted;

    private void onTableViewSorted(ListChangeListener.Change<? extends TableColumn<List<Object>, ?>> change) {
        while (change.next()) {
            System.out.println(change.wasAdded() + " " + change.wasPermutated() + " " + change.wasRemoved() + " " + change.wasReplaced() + " " + change.wasUpdated());
            if (change.wasAdded() || change.wasRemoved()) {
                TableColumn<List<Object>, ?> sortedColumn = tableView.getSortOrder().isEmpty() ? null : tableView.getSortOrder().get(0);
                if (sortedColumn != null) {
                    System.out.println("Sorted column: " + sortedColumn.getText() + " (" + sortedColumn.getSortType() + ")");
                }
            }
        }
    }

    // horizontal scroll listener DONE
    // vertical scroll binding DONE
    // row height listener
    // column sort listener - table view DONE
    // column sort subscriptions - letter bar DONE
    // column width subscriptions - letter bar DONE
    // column order listener DONE
    // unknown string binding UNNEEDED -> only when switching data sheet rows to observable lists, if data sheet updates should be reflected automatically by the table views
    // spacer width binding DONE
    private void clearTableView() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
        rowHeaders.getItems().clear();
        columnLetterGrid.getChildren().clear();
        columnWidthSubscriptions.forEach(Subscription::unsubscribe);
        columnWidthSubscriptions.clear();
        columnSortSubscriptions.forEach(Subscription::unsubscribe);
        columnSortSubscriptions.clear();
        tableView.getColumns().removeListener(columnsListener);
        if (rowHeaderVScrollBar != null && tableVScrollBar != null) {
            rowHeaderVScrollBar.valueProperty().unbindBidirectional(tableVScrollBar.valueProperty());
        }
        horizontalScrollBarSubscriptions.forEach(Subscription::unsubscribe);
        horizontalScrollBarSubscriptions.clear();
    }

    private final List<Subscription> columnWidthSubscriptions = new ArrayList<>();
    private final List<Subscription> columnSortSubscriptions = new ArrayList<>();
    private final List<Subscription> horizontalScrollBarSubscriptions = new ArrayList<>();

    // Create a Map to store row heights for visible rows only
    private final Map<Integer, Double> visibleRowHeights = new HashMap<>();

    private void updateTableView(DataSheet dataSheet) {
        List<String> headers = dataSheet.getHeaderRow();
        List<List<Object>> rows = dataSheet.getDataRows();

        columnLetterGrid.getChildren().clear();

        for (int i = 0; i < headers.size(); i++) {
            TableColumn<List<Object>, String> letterColumn = new TableColumn<>(getColumnLetter(i));
            TableColumn<List<Object>, String> namedColumn = new TableColumn<>(headers.get(i));
            final int colIndex = i;

            // TBD Switch to actual column types e.g. String, Boolean, Double etc.
//            column.setCellValueFactory(cellData
//                    -> new SimpleStringProperty((cellData.getValue().size() > colIndex)
//                            ? String.valueOf(cellData.getValue().get(colIndex)) : ""));
            namedColumn.setCellValueFactory(cellData
                    -> new SimpleObjectProperty((cellData.getValue().size() > colIndex) // tenary check needed, because otherwise empty trailing rows will cause index out of bounds 
                            ? cellData.getValue().get(colIndex) : ""));

            namedColumn.setPrefWidth(100);
            namedColumn.setMinWidth(20);

            tableView.getColumns().add(namedColumn);

            // Add column letter label
            Label letterLabel = new Label(getColumnLetter(i));
            letterLabel.setAlignment(Pos.CENTER);
            letterLabel.setStyle("-fx-padding: 5px; -fx-border-color: gray;");
            letterLabel.setMinWidth(namedColumn.getWidth());
            columnLetterGrid.add(letterLabel, i, 0);

        }

        for (int i = 0; i < headers.size(); i++) {
            TableColumn<List<Object>, String> column = new TableColumn<>(headers.get(i));
            final int colIndex = i;

            // Detect column width changes
            Subscription columnWidthSubscriber = column.widthProperty().subscribe((o, n) -> {
                adjustColumnHeaderWidth(colIndex, n.doubleValue());
            });
            columnWidthSubscriptions.add(columnWidthSubscriber);

            // Detect column sorting - ascending and descending
            Subscription columnSortSubscriber = column.sortTypeProperty().subscribe((o, n) -> {
                if (tableView.getSortOrder().contains(column)) {
                    System.out.println("Sorted column: " + column.getText() + " (" + n + ")");
                }
            });
            columnSortSubscriptions.add(columnSortSubscriber);
        }

        // Detect Column Reordering
        tableView.getColumns().addListener(columnsListener);

        // Fill row numbers (1, 2, 3, ...)
        rowHeaders.getItems().addAll(IntStream.rangeClosed(1, rows.size())
                .mapToObj(String::valueOf)
                .collect(Collectors.toList()));

        // Sync row headers with table scrolling
        rowHeaders.setFixedCellSize(25);
        tableView.setFixedCellSize(25);

        // Add rows to the table
        tableView.getItems().addAll(rows);
    }

    private final ListChangeListener<TableColumn<List<Object>, ?>> columnsListener = this::onColumnsChanged;

    private void onColumnsChanged(ListChangeListener.Change<? extends TableColumn<List<Object>, ?>> change) {
        while (change.next()) {
            if (change.wasAdded() && change.wasRemoved() && change.wasReplaced()) { // Column order changed
                updateColumnHeaders();
            }
        }
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

                Subscription columnLetterHScrollBarSubscriber = columnLetterHScrollBar.valueProperty().subscribe((o, n) -> {
                    tableHScrollBar.setValue(n.doubleValue() * tableHScrollBar.getMax());
                });
                horizontalScrollBarSubscriptions.add(columnLetterHScrollBarSubscriber);

                Subscription tableHScrollBarSubscriber = tableHScrollBar.valueProperty().subscribe((o, n) -> {
                    columnLetterScrollPane.setHvalue(n.doubleValue() / tableHScrollBar.getMax());
                });
                horizontalScrollBarSubscriptions.add(tableHScrollBarSubscriber);

            }

            tableVScrollBar = (ScrollBar) tableView.lookup(".scroll-bar:vertical");
            rowHeaderVScrollBar = (ScrollBar) rowHeaders.lookup(".scroll-bar:vertical");

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

    private void updateColumnHeaders() {
        columnWidthSubscriptions.forEach(Subscription::unsubscribe);
        columnWidthSubscriptions.clear();
        columnLetterGrid.getChildren().clear(); // Clear old headers

        for (int i = 0; i < tableView.getColumns().size(); i++) {
            TableColumn<?, ?> column = tableView.getColumns().get(i);
            final int colIndex = i;
            String columnLetter = getColumnLetter(i); // Get updated letter

            Label letterLabel = new Label(columnLetter);
            letterLabel.setAlignment(Pos.CENTER);
            letterLabel.setStyle("-fx-padding: 5px; -fx-border-color: gray;");
            letterLabel.setMinWidth(column.getWidth());

            columnLetterGrid.add(letterLabel, i, 0);

            Subscription columnListener = column.widthProperty().subscribe((o, n) -> {
                adjustColumnHeaderWidth(colIndex, n.doubleValue());
            });
            columnWidthSubscriptions.add(columnListener);
        }
    }

    private void adjustRowHeaderHeight(int rowIndex, double newHeight) {
        if (rowIndex < rowHeaders.getItems().size()) {
            rowHeaders.setFixedCellSize(newHeight);
        }
    }

}
