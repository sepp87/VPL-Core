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

    public DataSheetBlock() {
//    public DataSheetBlock(WorkspaceModel workspace) {
//        super(workspace);
        nameProperty().set("Table");
        resizableProperty().set(true);
        addInputPort("data", DataSheet.class);
        addInputPort("showAll", Boolean.class);
        addOutputPort("dataSheet", DataSheet.class);
        addOutputPort("allRows", Object.class);
        addOutputPort("leadingRows", Object.class);
        addOutputPort("headerRow", String.class);
        addOutputPort("columnTypes", Object.class);
        addOutputPort("dataRows", Object.class);
        addOutputPort("trailingRows", Object.class);

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
        Boolean showAll = (Boolean) inputPorts.get(1).getData();
        showAll = (showAll != null) ? showAll : false;

        if (dataSheet != null) {
            outputPorts.get(0).setData(dataSheet);
            outputPorts.get(1).setData(dataSheet.getAllRows());
            outputPorts.get(2).setData(dataSheet.getLeadingRows());
            outputPorts.get(3).setData(dataSheet.getHeaderRow());
            outputPorts.get(4).setData(dataSheet.getColumnTypes());
            outputPorts.get(5).setData(dataSheet.getDataRows());
            outputPorts.get(6).setData(dataSheet.getTrailingRows());
        }

        if (dataSheetViewer == null) {
            return;
        }
        dataSheetViewer.setDataSheet(dataSheet, showAll);
    }

    @Override
    public BlockModel copy() {
//        DataSheetBlock tableViewBlock = new DataSheetBlock(workspace);
        DataSheetBlock tableViewBlock = new DataSheetBlock();
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
