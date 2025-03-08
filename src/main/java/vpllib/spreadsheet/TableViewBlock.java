package vpllib.spreadsheet;

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

    public TableViewBlock(WorkspaceModel workspace) {
        super(workspace);
        addInputPort("Data", DataSheet.class);
    }

    @Override
    protected void initialize() {
    }

    @Override
    public Region getCustomization() {
        TableView tableView = new TableView();
        return tableView;
    }

    @Override
    protected void process() throws Exception {
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
