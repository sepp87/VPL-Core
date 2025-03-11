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

    public DataSheet(List<String> headers, Map<String, Class<?>> columnTypes, List<List<Object>> dataRows, List<List<Object>> leadingRows) {
        this.headerRow = headers;
        this.columnTypes = columnTypes;
        this.leadingRows = leadingRows;
        this.dataRows = dataRows;
        this.trailingRows = Collections.emptyList();
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

    public List<String> getHeaderRow() {
        return headerRow;
    }

    public List<List<Object>> getDataRows() {
        return dataRows;
    }

    public List<List<Object>> getTrailingRows() {
        return trailingRows;
    }

    public List<List<? extends Object>> getAllRows() {
        List<List<? extends Object>> result = new ArrayList<>();
        result.addAll(leadingRows);
        result.add(headerRow);
        result.addAll(dataRows);
        result.addAll(trailingRows);
        return result;
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
