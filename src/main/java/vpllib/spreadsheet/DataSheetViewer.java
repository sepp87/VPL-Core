package vpllib.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Subscription;

/**
 *
 * @author joostmeulenkamp
 */
public class DataSheetViewer extends BorderPane {

    private final TableView<List<Object>> tableView;

    public DataSheetViewer() {
        tableView = new TableView<>();

        // Detect Column Sorting - Ascending and Unsorted
        tableView.getSortOrder().addListener(tableViewSortListener);
        tableView.setPrefWidth(425);

        // Layout structure
        this.setCenter(tableView);
    }

    public void setDataSheet(DataSheet dataSheet, boolean showAll) {
        clearTableView();
        if (dataSheet == null) {
            return;
        }
        updateTableView(dataSheet, showAll);
    }

    public void remove() {
        tableView.getSortOrder().removeListener(tableViewSortListener);
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

    private void clearTableView() {
        System.out.println("CLEAR TABLE VIEWs");
        tableView.getColumns().clear();
        tableView.getItems().clear();
        tableView.getColumns().removeListener(columnsListener);
        columnSortSubscriptions.forEach(Subscription::unsubscribe);
        columnSortSubscriptions.clear();
    }

    private final List<Subscription> columnSortSubscriptions = new ArrayList<>();

    private void updateTableView(DataSheet dataSheet, boolean showAll) {

        boolean hasHeaderRow = showAll ? false : dataSheet.hasHeaderRow();
        List<String> headers = dataSheet.getHeaderRow();
        List<List<Object>> rows = hasHeaderRow ? dataSheet.getDataRows() : dataSheet.getAllRows();
        int limit = hasHeaderRow ? headers.size() : dataSheet.lengthOfLongestRow();

        System.out.println(rows.size() + " number of rows");

        for (int i = 0; i < limit; i++) {

            TableColumn<List<Object>, String> letterColumn = new TableColumn<>(getColumnLetter(i));
            tableView.getColumns().add(letterColumn);
            letterColumn.setSortable(false);
            TableColumn<List<Object>, String> dataColumn = letterColumn;
            if (hasHeaderRow) {
                TableColumn<List<Object>, String> namedColumn = new TableColumn<>(headers.get(i));
                letterColumn.getColumns().add(namedColumn);
                dataColumn = namedColumn;

                // Detect column sorting - ascending and descending
                Subscription columnSortSubscriber = namedColumn.sortTypeProperty().subscribe((o, n) -> {
                    if (tableView.getSortOrder().contains(namedColumn)) {
                        System.out.println("Sorted column: " + namedColumn.getText() + " (" + n + ")");
                    }
                });

                columnSortSubscriptions.add(columnSortSubscriber);
            }
            final int colIndex = i;

            // TBD Switch to actual column types e.g. String, Boolean, Double etc.
//            column.setCellValueFactory(cellData
//                    -> new SimpleStringProperty((cellData.getValue().size() > colIndex)
//                            ? String.valueOf(cellData.getValue().get(colIndex)) : ""));
            dataColumn.setCellValueFactory(cellData
                    -> new SimpleObjectProperty((cellData.getValue().size() > colIndex) // tenary check needed, because otherwise empty trailing rows will cause index out of bounds 
                            ? cellData.getValue().get(colIndex) : ""));
            dataColumn.setPrefWidth(100);

        }

        // Detect Column Reordering
        tableView.getColumns().addListener(columnsListener);

//        tableView.setFixedCellSize(25);
        // Add rows to the table
        tableView.getItems().addAll(rows);

        TableColumn<List<Object>, String> rowNumColumn = new TableColumn<>("#");
        if (hasHeaderRow) {
            TableColumn<List<Object>, String> cornerColumn = new TableColumn<>();
            cornerColumn.getColumns().add(rowNumColumn);
            tableView.getColumns().add(0, cornerColumn);
        } else {
            tableView.getColumns().add(0, rowNumColumn);
        }
        rowNumColumn.setSortable(false);
        rowNumColumn.setReorderable(false);
        rowNumColumn.setPrefWidth(50);

        // Add custom styling to the row number column
        rowNumColumn.getStyleClass().add("numbers-column");
        // For more advanced styling, you can set a custom cell factory
        rowNumColumn.setCellFactory(column -> {
            return new TableCell<List<Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(String.valueOf(getIndex() + 1)); // Correct row number

                    if (!getStyleClass().contains("numbers-column")) {
                        getStyleClass().add("numbers-column"); // Prevent duplicate additions
                    }

                }
            };
        });

        Node corner = tableView.lookup(".corner");

        if (corner != null) {
            // Set the bottom left corner of the scrollpane to be transparent
            corner.setStyle("-fx-background-color: transparent;");
        } else {
            System.out.println("Corner node not found!");
        }
    }

    private final ListChangeListener<TableColumn<List<Object>, ?>> columnsListener = this::onColumnsChanged;

    private void onColumnsChanged(ListChangeListener.Change<? extends TableColumn<List<Object>, ?>> change) {
        while (change.next()) {
            if (change.wasAdded() && change.wasRemoved() && change.wasReplaced()) { // Column order changed
                // mutate data sheet
            }
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

    private void adjustColumnWidth() {
        Platform.runLater(() -> {
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                column.setPrefWidth(Math.min(column.getWidth(), 120)); // Auto-size but cap at 120px
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

}
