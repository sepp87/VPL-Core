package btslib.spreadsheet;

import java.text.DecimalFormat;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.model.StylesTable;
import btscore.util.ParsingUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class TypedSheetContentsHandler implements SheetContentsHandler {

    private final StylesTable styles;
    private final RowConsumer rowConsumer;
    private final List<List<Object>> firstHundredRows = new ArrayList<>();
    private final List<List<Object>> lastHundredRows = new ArrayList<>();
    private final Map<Integer, Integer> columnCountRecurrences = new HashMap<>();

    private List<Object> currentRow;
    private String currentCellType;
    private int currentStyleIndex = -1;

    public TypedSheetContentsHandler(StylesTable styles, RowConsumer rowConsumer) {
        this.styles = styles;
        this.rowConsumer = rowConsumer;
    }

    public void setCurrentCellType(String cellType) {
        this.currentCellType = cellType;
    }

    public void setCurrentStyleIndex(int styleIndex) {
        this.currentStyleIndex = styleIndex;
    }

    @Override
    public void startRow(int rowNum) {
        currentRow = new ArrayList<>();
        rowConsumer.addRow(currentRow);

        if (rowNum < 100) {
            firstHundredRows.add(currentRow);
        }

        lastHundredRows.add(currentRow);
        if (lastHundredRows.size() > 100) {
            lastHundredRows.removeFirst();
        }
    }

    @Override
    public void endRow(int rowNum) {
        int columnCount = currentRow.size();
        int occurences = columnCountRecurrences.getOrDefault(columnCount, 0) + 1;
        columnCountRecurrences.put(columnCount, occurences);
//        System.out.println();
    }

    @Override
    public void cell(String cellRef, String formattedValue, XSSFComment comment) {
        Object value = parseCell(formattedValue, currentCellType);
//        System.out.print(currentCellType + ":" + value.getClass().getSimpleName() + ":" + value.toString() + "; ");
        currentRow.add(value);
    }

    private Object parseCell(String val, String type) {
        if (val == null) {
            return null;
        }
        if (type == null) {
            type = "n"; // default is number
        }

        try {
            switch (type) {
                case "s": // shared string
                case "str": // string
                case "inlineStr": // string
                    return val;
                case "b": // boolean
                    return val.equals("1") || val.equalsIgnoreCase("true");
                case "n": // number or date
                    boolean isDate = false;

                    if (currentStyleIndex >= 0) {
                        CellStyle style = styles.getStyleAt(currentStyleIndex);
                        String formatStr = style.getDataFormatString();
                        short formatIdx = style.getDataFormat();
                        isDate = DateUtil.isADateFormat(formatIdx, formatStr);
                    }
                    if (isDate) {
                        return ExcelUtils.parseExcelDate(val);
                    }

                    Locale locale = Locale.getDefault();
                    Number number = DecimalFormat.getInstance(locale).parse(val);

                    return ParsingUtils.castToBestNumericType(number);

                case "e": // error
                    return "#ERROR:" + val;
                default:
                    return val;
            }
        } catch (Exception e) {
//            Logger.getLogger(TypedSheetContentsHandler.class.getName()).log(Level.SEVERE, null, e);
            return val; // fallback
        }
    }

    /**
     *
     * @param rows
     * @return Result will be -1 in case no header row was found
     */
    public int detectHeaderRowNumber() {
        int mostRecurringColumnCount = getMostRecurringColumnCount();

        System.out.println(firstHundredRows.get(0).size() + " == " + mostRecurringColumnCount + " firstHundredRows.get(0).size() == mostRecurringColumnCount");

        // case A - promote the first row to header if it has the same number of colums as the data
        if (firstHundredRows.get(0).size() == mostRecurringColumnCount) {
            return 0;
        }

        // case B - look for the first row that has as many columns as the data and does not have any missing data
        int headerRowNumber = -1;
        rowLoop:
        for (List<Object> headerRowCandidate : firstHundredRows) {
            headerRowNumber++;
            if (headerRowCandidate.size() == mostRecurringColumnCount) {
                for (Object cell : headerRowCandidate) {
                    if (cell == null || (cell instanceof String && cell.equals(""))) {
                        continue rowLoop;
                    }
                }
                return headerRowNumber;

            }
        }

        return -1;
    }

    private int getMostRecurringColumnCount() {
        Integer mostRecurringColumnCount = -1;
        Integer recurrences = -1;

        for (Map.Entry<Integer, Integer> count : columnCountRecurrences.entrySet()) {
            if (count.getValue() > recurrences) {
                recurrences = count.getValue();
                mostRecurringColumnCount = count.getKey();
            }
        }
        return mostRecurringColumnCount;
    }

}
