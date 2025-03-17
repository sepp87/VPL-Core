package vpllib.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author joostmeulenkamp
 */
public class DataSheet {

    private StringProperty name = new SimpleStringProperty(this, "name", "data");

    private final Map<String, Class<?>> columnTypes;

    private final List<List<Object>> leadingRows;
    private final List<String> headerRow;
    private final List<List<Object>> dataRows;
    private final List<List<Object>> trailingRows;

    private final int lengthOfLongestRow;

    public DataSheet(List<List<Object>> rows) {
        this(Collections.emptyList(), Collections.emptyMap(), Collections.emptyList(), rows, Collections.emptyList());
    }

    public DataSheet(List<String> headerRow, Map<String, Class<?>> columnTypes, List<List<Object>> leadingRows, List<List<Object>> dataRows, List<List<Object>> trailingRows) {
        this.leadingRows = leadingRows;
        this.headerRow = headerRow;
        this.columnTypes = columnTypes;
        this.dataRows = dataRows;
        this.trailingRows = trailingRows;
        this.lengthOfLongestRow = getLongestRowLength();
    }

    private int getLongestRowLength() {
        List<List<Object>> rows = getAllRows();
        int result = -1;
        for (List<Object> row : rows) {
            if (row.size() > result) {
                result = row.size();
            }
        }
        return result;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Map<String, Class<?>> getColumnTypes() {
        return columnTypes;
    }

    public List<List<Object>> getLeadingRows() {
        return leadingRows;
    }

    public boolean hasHeaderRow() {
        return !headerRow.isEmpty();
    }

    public List<String> getHeaderRow() {
        return headerRow;
    }

    public List<List<Object>> getDataRows() {
        return dataRows;
    }

    public List<List<Object>> getTrailingRows() {
        return trailingRows;
    }

    public List<List<Object>> getAllRows() {
        List<List<Object>> result = new ArrayList<>();
        result.addAll(leadingRows);
        if (hasHeaderRow()) {
            result.add(new ArrayList<>(headerRow));
        }
        result.addAll(dataRows);
        result.addAll(trailingRows);
        return result;
    }

    public int lengthOfLongestRow() {
        return lengthOfLongestRow;
    }

    public Map<String, Object> getRowAsMap(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= dataRows.size()) {
            return null;
        }
        Map<String, Object> rowMap = new HashMap<>();
        List<Object> rowData = dataRows.get(rowIndex);
        for (int i = 0; i < headerRow.size(); i++) {
            rowMap.put(headerRow.get(i), rowData.get(i));
        }
        return rowMap;
    }

}
