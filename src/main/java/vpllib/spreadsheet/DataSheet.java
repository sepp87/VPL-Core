package vpllib.spreadsheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joostmeulenkamp
 */
public class DataSheet {

    private final List<String> headers;
    private final Map<String, Class<?>> columnTypes;
    private final List<List<Object>> rows;
    private final List<List<Object>> firstRows;
    
    public DataSheet(List<String> headers, Map<String, Class<?>> columnTypes, List<List<Object>> rows,  List<List<Object>> firstRows) {
        this.headers = headers;
        this.columnTypes = columnTypes;
        this.rows = rows;
        this.firstRows = firstRows;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public Map<String, Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public List<List<Object>> getFirstRows() {
        return firstRows;
    }
    
    public Map<String, Object> getRowAsMap(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            return null;
        }
        Map<String, Object> rowMap = new HashMap<>();
        List<Object> rowData = rows.get(rowIndex);
        for (int i = 0; i < headers.size(); i++) {
            rowMap.put(headers.get(i), rowData.get(i));
        }
        return rowMap;
    }

}
