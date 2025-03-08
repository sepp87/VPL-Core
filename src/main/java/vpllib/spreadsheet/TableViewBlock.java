package vpllib.spreadsheet;

import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
        return tableView;
    }

    @Override
    protected void process() throws Exception {
        DataSheet dataSheet = (DataSheet) inputPorts.get(0).getData();

        if (dataSheet == null) {
            tableView.getColumns().clear();
            tableView.getItems().clear();
            return;
        }

        updateTableView(dataSheet);
    }

    private void updateTableView(DataSheet dataSheet) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        List<String> headers = dataSheet.getHeaders();
        List<List<Object>> rows = dataSheet.getRows();

        if (headers.isEmpty()) {
            return; // No data to display
        }

        // Create columns dynamically
        for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
            final int index = colIndex; // Required for lambda expressions
            TableColumn<List<Object>, Object> column = new TableColumn<>(headers.get(index));

            column.setCellValueFactory(cellData -> {
                List<Object> row = cellData.getValue();
                if (index < row.size()) {
                    return new SimpleObjectProperty<>(row.get(index));
                }
                return new SimpleObjectProperty<>(null);
            });

            tableView.getColumns().add(column);
        }

        // Add data rows
        tableView.getItems().addAll(rows);


        Platform.runLater(() -> {
            System.out.println("Column Widths (after layout pass):");
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                System.out.println(column.getText() + ": " + column.getWidth() + " px");
            }
        });

        Platform.runLater(() -> {
            for (TableColumn<?, ?> column : tableView.getColumns()) {
                column.setPrefWidth(Math.min(column.getWidth(), 100)); // Auto-size but cap at 220px
//                column.setMaxWidth(220); // Prevent it from expanding beyond 220px
            }
        });
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
