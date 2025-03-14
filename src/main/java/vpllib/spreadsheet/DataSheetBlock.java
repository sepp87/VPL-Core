package vpllib.spreadsheet;

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
public class DataSheetBlock extends BlockModel {

    private DataSheetViewer dataSheetViewer;

    public DataSheetBlock(WorkspaceModel workspace) {
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
        dataSheetViewer = new DataSheetViewer();
        return dataSheetViewer;
    }

    @Override
    protected void process() throws Exception {
        DataSheet dataSheet = (DataSheet) inputPorts.get(0).getData();
        if (dataSheetViewer == null) {
            return;
        }
        dataSheetViewer.setDataSheet(dataSheet);
    }

    @Override
    public BlockModel copy() {
        DataSheetBlock tableViewBlock = new DataSheetBlock(workspace);
        return tableViewBlock;
    }

    @Override
    protected void onRemoved() {
        if (dataSheetViewer == null) {
            return;
        }
        dataSheetViewer.remove();
    }

}
