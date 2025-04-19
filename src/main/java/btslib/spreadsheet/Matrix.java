package btslib.spreadsheet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joostmeulenkamp
 */
public class Matrix implements RowConsumer {

    private final List<List<Object>> rows = new ArrayList<>();

    @Override
    public void addRow(List<Object> row) {
        rows.add(row);
    }

    public List<List<Object>> getAllRows() {
        return rows;
    }
    
    public List<Object> getColumn(int index) {
        List<Object> result = new ArrayList<>();
        for(List<Object> row : rows){
            result.add(row.get(index));
        }
        return result;
    }

}
