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

    public void setDataSheet(DataSheet dataSheet) {
        clearTableView();
        if (dataSheet == null) {
            return;
        }
        updateTableView(dataSheet);
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
        tableView.getColumns().removeListener(columnsListener);
        columnSortSubscriptions.forEach(Subscription::unsubscribe);
        columnSortSubscriptions.clear();
    }

    private final List<Subscription> columnSortSubscriptions = new ArrayList<>();

    // Create a Map to store row heights for visible rows only
    private final Map<Integer, Double> visibleRowHeights = new HashMap<>();

    private void updateTableView(DataSheet dataSheet) {

        List<String> headers = dataSheet.getHeaderRow();
        List<List<Object>> rows = dataSheet.getDataRows();

        for (int i = 0; i < headers.size(); i++) {

            TableColumn<List<Object>, String> letterColumn = new TableColumn<>(getColumnLetter(i));
            TableColumn<List<Object>, String> namedColumn = new TableColumn<>(headers.get(i));
            letterColumn.getColumns().addAll(namedColumn);
            final int colIndex = i;

            // TBD Switch to actual column types e.g. String, Boolean, Double etc.
//            column.setCellValueFactory(cellData
//                    -> new SimpleStringProperty((cellData.getValue().size() > colIndex)
//                            ? String.valueOf(cellData.getValue().get(colIndex)) : ""));
            namedColumn.setCellValueFactory(cellData
                    -> new SimpleObjectProperty((cellData.getValue().size() > colIndex) // tenary check needed, because otherwise empty trailing rows will cause index out of bounds 
                            ? cellData.getValue().get(colIndex) : ""));

            tableView.setStyle("-fx-background-color: transparent;");

            letterColumn.setStyle("-fx-background-color: transparent;");

            namedColumn.setPrefWidth(100);
            namedColumn.setMinWidth(20);

            tableView.getColumns().add(letterColumn);

            // Detect column sorting - ascending and descending
            Subscription columnSortSubscriber = namedColumn.sortTypeProperty().subscribe((o, n) -> {
                if (tableView.getSortOrder().contains(namedColumn)) {
                    System.out.println("Sorted column: " + namedColumn.getText() + " (" + n + ")");
                }
            });

            columnSortSubscriptions.add(columnSortSubscriber);
        }

        // Detect Column Reordering
        tableView.getColumns().addListener(columnsListener);

//        tableView.setFixedCellSize(25);
        // Add rows to the table
        tableView.getItems().addAll(rows);

        TableColumn<List<Object>, String> cornerColumn = new TableColumn<>();
        TableColumn<List<Object>, String> rowNumColumn = new TableColumn<>("#");
        cornerColumn.getColumns().add(rowNumColumn);
        rowNumColumn.setCellValueFactory(data
                -> new SimpleStringProperty(String.valueOf(tableView.getItems().indexOf(data.getValue()) + 1)));
        rowNumColumn.setSortable(false);
        rowNumColumn.setReorderable(false);
        rowNumColumn.setPrefWidth(50);
        tableView.getColumns().add(0, cornerColumn);

        // Add custom styling to the row number column
//        rowNumColumn.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gray;");
        // For more advanced styling, you can set a custom cell factory
        rowNumColumn.setCellFactory(column -> {
            return new TableCell<List<Object>, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);

                        // Style specific to row number cells
                        setStyle("-fx-alignment: center;"
                                + "-fx-font-weight: bold;");

                    }
                }
            };
        });
        
        

    }

    private final ListChangeListener<TableColumn<List<Object>, ?>> columnsListener = this::onColumnsChanged;

    private void onColumnsChanged(ListChangeListener.Change<? extends TableColumn<List<Object>, ?>> change) {
        while (change.next()) {
            if (change.wasAdded() && change.wasRemoved() && change.wasReplaced()) { // Column order changed
                // mutate data sheet
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

    private String getColumnLetter(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + (index % 26)));
            index = (index / 26) - 1;
        }
        return sb.toString();

    }

}
