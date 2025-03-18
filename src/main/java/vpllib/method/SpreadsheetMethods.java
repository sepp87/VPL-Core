package vpllib.method;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vplcore.graph.block.BlockMetadata;
import vplcore.util.ParsingUtils;
import vpllib.spreadsheet.DataSheet;

/**
 *
 * @author joostmeulenkamp
 */
public class SpreadsheetMethods {

    @BlockMetadata(
            name = "readCsv",
            description = "Reads a CSV file and returns a data sheet object. This method automatically detects whether a header is present. If you do not wish to specify a header, set the row number to -1. A value of 0 or 1 will assume the first row as the header. For N ≥ 2, row N will be used as the header.",
            identifier = "Spreadsheet.readCsv",
            category = "Core")
    public static DataSheet readCsv(File csv, Integer headerRowNumber) throws IOException {
        try ( Reader reader = new FileReader(csv);  CSVParser csvParser = CSVParser.parse(reader,
                CSVFormat.Builder.create(CSVFormat.DEFAULT).setSkipHeaderRecord(false).build())) {

            List<List<Object>> rows = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                List<Object> row = new ArrayList<>();
                for (String value : record) {
                    row.add(value); // Read all values as Strings initially
                }
                rows.add(row);
            }

            return convertRowsToDataSheet(rows, headerRowNumber);
        }
    }

    @BlockMetadata(
            name = "writeCsv",
            description = "",
            identifier = "Spreadsheet.writeCsv",
            category = "Core")
    public static void writeCsv(File csv, DataSheet dataSheet) throws IOException {
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(csv));  CSVPrinter csvPrinter = new CSVPrinter(writer,
                CSVFormat.Builder.create(CSVFormat.DEFAULT).build())) {

            printRowsToCsv(csvPrinter, dataSheet.getLeadingRows());
            csvPrinter.printRecord(dataSheet.getHeaderRow());
            printRowsToCsv(csvPrinter, dataSheet.getDataRows());
            printRowsToCsv(csvPrinter, dataSheet.getTrailingRows());
        }
    }

    private static void printRowsToCsv(CSVPrinter csvPrinter, List<List<Object>> rows) throws IOException {
        for (List<Object> row : rows) {
            csvPrinter.printRecord(row);
        }
    }

    @BlockMetadata(
            name = "readExcel",
            description = "Reads an XLSX file and returns a data sheet object. This method automatically detects whether a header is present. If you do not wish to specify a header, set the row number to -1. A value of 0 or 1 will assume the first row as the header. For N ≥ 2, row N will be used as the header.",
            identifier = "Spreadsheet.readExcel",
            category = "Core")
    public static DataSheet readExcel(File excel, Integer headerRowNumber) throws IOException {
        try ( FileInputStream fis = new FileInputStream(excel);  Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                short lastCell = row.getLastCellNum();
                List<Object> rowData = new ArrayList<>();
                for (int j = 0; j < lastCell; j++) {
                    rowData.add(getCellValue(row.getCell(j)));
                }
                rows.add(rowData);
            }
            DataSheet dataSheet = convertRowsToDataSheet(rows, headerRowNumber);
            dataSheet.nameProperty().set(sheet.getSheetName());
            return dataSheet;
        }
    }

    private static DataSheet convertRowsToDataSheet(List<List<Object>> rows, Integer headerRowNumber) {
        int size = rows.size();
        if (headerRowNumber == null) {
            headerRowNumber = detectHeaderRowNumber(rows);
        } else if (headerRowNumber < -1 || headerRowNumber > size) {
            throw new IndexOutOfBoundsException("Header row number " + headerRowNumber + " out of bounds; index should be within -1 and " + size);
        } else if (headerRowNumber == -1) {
            return new DataSheet(rows); // if there is no header row detected or specified, just return a data sheet with all rows as data rows
        } else if (headerRowNumber > 0) {
            headerRowNumber--; // headerRowNumber was input by user, decrement row number by one, since data sheets row numbering starts with one
        }
        List<String> headerRow = getHeaderRowByRowNumber(rows, headerRowNumber);
        List<List<Object>> leadingRows = removeLeadingRows(rows, headerRowNumber);
        rows.removeFirst(); // also remove the header row, after stripping the leading rows, the header row is first
        List<List<Object>> trailingRows = removeTrailingRows(rows); // remove trailing rows with only null values
        Map<String, Class<?>> columnTypes = detectColumnTypes(rows);
        List<List<Object>> dataRows = rows;
        return new DataSheet(headerRow, columnTypes, leadingRows, dataRows, trailingRows);
    }

    private static List<List<Object>> removeTrailingRows(List<List<Object>> rows) {
        List<List<Object>> trailingRows = new ArrayList<>();
        int last = rows.size() - 1;
        reversedLoop:
        for (int i = last; i > -1; i--) {
            List<Object> row = rows.get(i);
            for (Object object : row) {
                if (object != null) {
                    break reversedLoop;
                }
            }

            trailingRows.add(rows.removeLast());
        }
        Collections.reverse(trailingRows);
        return trailingRows;
    }

    private static List<List<Object>> removeLeadingRows(List<List<Object>> rows, int headerRowNumber) {
        List<List<Object>> leadingRows = new ArrayList<>();
        for (int i = 0; i < headerRowNumber; i++) {
            leadingRows.add(rows.removeFirst());
        }
        System.out.println("LEADING ROWS " + leadingRows.size());
        return leadingRows;
    }

    private static List<String> getHeaderRowByRowNumber(List<List<Object>> rows, int headerRowNumber) {
        List<String> result = new ArrayList<>();
        List<Object> headerRow = rows.get(headerRowNumber);
        for (Object header : headerRow) {
            result.add((String) header);
        }
        return result;
    }

    /**
     *
     * @param rows
     * @return Result will be -1 in case no header row was found
     */
    private static int detectHeaderRowNumber(List<List<Object>> rows) {
        int mostRecurringColumnCount = getMostRecurringColumnCount(rows);

        System.out.println(rows.get(0).size() + " == " + mostRecurringColumnCount + " rows.get(0).size() == mostRecurringColumnCount");

        // case A - promote the first row to header if it has the same number of colums as the data
        if (rows.get(0).size() == mostRecurringColumnCount) {
            return 0;
        }

        // case B - look for the first row that has as many columns as the data and does not have any missing data
        int headerRowNumber = -1;
        rowLoop:
        for (List<Object> headerRowCandidate : rows) {
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

    private static int getMostRecurringColumnCount(List<List<Object>> rows) {
        Map<Integer, Integer> columnCount = new HashMap<>();

        for (List<Object> row : rows) {
            int lastCell = row.size();
            int count = columnCount.getOrDefault(lastCell, 0) + 1;
            columnCount.put(lastCell, count);
        }

        Integer mostRecurringColumnCount = -1;
        Integer recurrences = -1;

        for (Entry<Integer, Integer> count : columnCount.entrySet()) {
            if (count.getValue() > recurrences) {
                recurrences = count.getValue();
                mostRecurringColumnCount = count.getKey();
            }
        }
        return mostRecurringColumnCount;
    }

    @BlockMetadata(
            name = "writeExcel",
            description = "",
            identifier = "Spreadsheet.writeExcel",
            category = "Core")
    public static void writeExcel(File excel, DataSheet dataSheet) throws IOException {
        try ( Workbook workbook = new XSSFWorkbook();  FileOutputStream fos = new FileOutputStream(excel)) {

            Sheet sheet = workbook.createSheet("Sheet1");

            int index = 0;
            addRowsToSheet(dataSheet.getLeadingRows(), sheet, index);
            index += dataSheet.getLeadingRows().size();

            Row headerRow = sheet.createRow(index++);
            for (int i = 0; i < dataSheet.getHeaderRow().size(); i++) {
                headerRow.createCell(i).setCellValue(dataSheet.getHeaderRow().get(i));
            }

            addRowsToSheet(dataSheet.getDataRows(), sheet, index);
            index += dataSheet.getDataRows().size();

            addRowsToSheet(dataSheet.getTrailingRows(), sheet, index);

            workbook.write(fos);
        }
    }

    private static void addRowsToSheet(List<List<Object>> rows, Sheet sheet, int startIndex) {
        for (List<Object> row : rows) {
            Row excelRow = sheet.createRow(startIndex++);
            for (int i = 0; i < row.size(); i++) {
                setCellValue(excelRow.createCell(i), row.get(i));
            }
        }
    }

    private static Map<String, Class<?>> detectColumnTypes(List<List<Object>> rows) {
        Map<String, Class<?>> columnTypes = new HashMap<>();
        if (rows.isEmpty()) {
            return columnTypes;
        }

        int columnCount = rows.get(0).size();

        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            Set<Class<?>> detectedTypes = new HashSet<>();

            for (List<Object> row : rows) {
                if (colIndex < row.size() && row.get(colIndex) != null) {
                    detectedTypes.add(row.get(colIndex).getClass());
                }
            }
//            System.out.println("Column " + (colIndex + 1) + " " + determineBestType(detectedTypes).getSimpleName());
            columnTypes.put("Column " + (colIndex + 1), determineBestType(detectedTypes));
        }
        return columnTypes;
    }

    private static Class<?> determineBestType(Set<Class<?>> types) {
        if (types.isEmpty()) {
            return String.class;
        }

        if (types.contains(String.class)) {
            return String.class; // If any value is a String, treat the entire column as String
        }

        if (types.contains(Date.class) && types.size() == 1) {
            return Date.class;
        }

        if (types.contains(Boolean.class) && types.size() == 1) {
            return Boolean.class;
        }

        if (types.contains(Double.class)) {
            return Double.class;
        }

        if (types.contains(Long.class)) {
            return Long.class;
        }

        if (types.contains(Integer.class)) {
            return Integer.class;
        }

        return String.class; // Default case: Convert to String
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                String string = cell.getStringCellValue();
                String lower = string.toLowerCase();
                if (lower.equals("false") || lower.equals("true")) {
                    return Boolean.valueOf(lower);
                }
                return string;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double number = cell.getNumericCellValue();
                return ParsingUtils.castToBestNumericType(number);
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static List<List<Object>> castRowsToTypes(List<List<Object>> rows, Map<String, Class<?>> columnTypes) {
        List<List<Object>> castedRows = new ArrayList<>();
        for (List<Object> row : rows) {
            List<Object> newRow = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                newRow.add(castValue(row.get(i), columnTypes.getOrDefault("Column " + (i + 1), String.class)));
            }
            castedRows.add(newRow);
        }
        return castedRows;
    }

    private static Object castValue(Object value, Class<?> targetType) {
        String str = value.toString();
        if (str.isEmpty()) {
            return null;
        }
        if (targetType == Integer.class) {
            return Integer.parseInt(str);
        }
        if (targetType == Double.class) {
            System.out.println(value);

            return Double.parseDouble(str);
        }
        if (targetType == Boolean.class) {
            return Boolean.parseBoolean(str);
        }
        return str;
    }

    @BlockMetadata(
            name = "filterData",
            description = "",
            identifier = "Spreadsheet.filterData",
            category = "Core")
    public static DataSheet filterData(DataSheet dataSheet, String column, Predicate<Object> condition) {
        int colIndex = dataSheet.getHeaderRow().indexOf(column);
        if (colIndex == -1) {
            return dataSheet; // Column not found
        }
        List<List<Object>> filteredRows = dataSheet.getDataRows().stream()
                .filter(row -> condition.test(row.get(colIndex)))
                .collect(Collectors.toList());

        return new DataSheet(dataSheet.getHeaderRow(), dataSheet.getColumnTypes(), dataSheet.getLeadingRows(), filteredRows, dataSheet.getTrailingRows());
    }

    @BlockMetadata(
            name = "sortData",
            description = "",
            identifier = "Spreadsheet.sortData",
            category = "Core")
    public static DataSheet sortData(DataSheet dataSheet, String column, boolean ascending) {
        int colIndex = dataSheet.getHeaderRow().indexOf(column);
        if (colIndex == -1) {
            return dataSheet;
        }

        List<List<Object>> sortedRows = new ArrayList<>(dataSheet.getDataRows());
        sortedRows.sort(Comparator.comparing(row -> (Comparable) row.get(colIndex)));

        if (!ascending) {
            Collections.reverse(sortedRows);
        }

        return new DataSheet(dataSheet.getHeaderRow(), dataSheet.getColumnTypes(), dataSheet.getLeadingRows(), sortedRows, dataSheet.getTrailingRows());
    }

    @BlockMetadata(
            name = "mergeDataSheets",
            description = "",
            identifier = "Spreadsheet.mergeDataSheets",
            category = "Core")
    public static DataSheet mergeDataSheets(DataSheet sheet1, DataSheet sheet2) {
        if (!sheet1.getHeaderRow().equals(sheet2.getHeaderRow())) {
            throw new IllegalArgumentException("Headers must match to merge DataSheets");
        }

        List<List<Object>> mergedLeadingRows = new ArrayList<>(sheet1.getLeadingRows());
        mergedLeadingRows.addAll(sheet2.getLeadingRows());

        List<List<Object>> mergedRows = new ArrayList<>(sheet1.getDataRows());
        mergedRows.addAll(sheet2.getDataRows());

        List<List<Object>> mergedTrailingRows = new ArrayList<>(sheet1.getLeadingRows());
        mergedTrailingRows.addAll(sheet2.getLeadingRows());

        return new DataSheet(sheet1.getHeaderRow(), sheet1.getColumnTypes(), mergedLeadingRows, mergedRows, mergedTrailingRows);
    }

    @BlockMetadata(
            name = "getUniqueValues",
            description = "",
            identifier = "Spreadsheet.getUniqueValues",
            category = "Core")
    public static Set<Object> getUniqueValues(DataSheet dataSheet, String column) {
        int colIndex = dataSheet.getHeaderRow().indexOf(column);
        if (colIndex == -1) {
            return Collections.emptySet();
        }

        return dataSheet.getDataRows().stream()
                .map(row -> row.get(colIndex))
                .collect(Collectors.toSet());
    }

    @BlockMetadata(
            name = "findMaxValue",
            description = "",
            identifier = "Spreadsheet.findMaxValue",
            category = "Core")
    public static Object findMaxValue(DataSheet dataSheet, String column) {
        int colIndex = dataSheet.getHeaderRow().indexOf(column);
        if (colIndex == -1) {
            return null;
        }

        return dataSheet.getDataRows().stream()
                .map(row -> row.get(colIndex))
                .filter(Objects::nonNull)
                .max(Comparator.comparing(o -> (Comparable) o))
                .orElse(null);
    }

    public static String dataSheetToJson(DataSheet dataSheet) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(dataSheet.getHeaderRow());
    }

    public static DataSheet jsonToDataSheet(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, DataSheet.class);
    }

//    public static String dataSheetToJson(DataSheet dataSheet) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> jsonList = new ArrayList<>();
//
//        for (List<Object> row : dataSheet.getRows()) {
//            Map<String, Object> rowMap = new HashMap<>();
//            for (int i = 0; i < dataSheet.getHeaders().size(); i++) {
//                rowMap.put(dataSheet.getHeaders().get(i), row.get(i));
//            }
//            jsonList.add(rowMap);
//        }
//        return objectMapper.writeValueAsString(jsonList);
//    }
//
//    public static DataSheet jsonToDataSheet(String json) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<Map<String, Object>> list = objectMapper.readValue(json, new TypeReference<>() {
//        });
//
//        if (list.isEmpty()) {
//            return new DataSheet(Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
//        }
//
//        List<String> headers = new ArrayList<>(list.get(0).keySet());
//        List<List<Object>> rows = list.stream()
//                .map(row -> headers.stream().map(row::get).collect(Collectors.toList()))
//                .collect(Collectors.toList());
//
//        Map<String, Class<?>> columnTypes = inferColumnTypes(rows);
//        return new DataSheet(headers, columnTypes, castRowsToTypes(rows, columnTypes));
//    }
    @BlockMetadata(
            name = "convertExcelToCsv",
            description = "",
            identifier = "Spreadsheet.convertExcelToCsv",
            category = "Core")
    public static void convertExcelToCsv(File excel, File csv) throws IOException {
        DataSheet sheet = readExcel(excel, -1);
        writeCsv(csv, sheet);
    }

    @BlockMetadata(
            name = "convertCsvToExcel",
            description = "",
            identifier = "Spreadsheet.convertCsvToExcel",
            category = "Core")
    public static void convertCsvToExcel(File csv, File excel) throws IOException {
        DataSheet sheet = readCsv(csv, -1);
        writeExcel(excel, sheet);
    }

}
