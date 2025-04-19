package btslib.spreadsheet;

import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public interface RowConsumer {

    public void addRow(List<Object> row);
}
