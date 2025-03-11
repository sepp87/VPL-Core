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
import vpllib.spreadsheet.DataSheet;

/**
 *
 * @author joostmeulenkamp
 */
public class SpreadsheetMethods {

    @BlockMetadata(
            name = "readCsv",
            description = "",
            identifier = "Spreadsheet.readCsv",
            category = "Core")
    public static DataSheet readCsv(File csv) throws IOException {
        try (Reader reader = new FileReader(csv); CSVParser csvParser = CSVParser.parse(reader,
                CSVFormat.Builder.create(CSVFormat.DEFAULT).setSkipHeaderRecord(false).build())) {

            List<List<Object>> rows = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                List<Object> row = new ArrayList<>();
                for (String value : record) {
                    row.add(value); // Read all values as Strings initially
                }
                rows.add(row);
            }

            int headerRowNumber = detectHeaderRowNumber(rows);
            List<String> headers = getHeadersByRowNumber(rows, headerRowNumber);
            List<List<Object>> firstRows = removeFirstRows(rows, headerRowNumber);
            Map<String, Class<?>> columnTypes = detectColumnTypes(rows);
            return new DataSheet(headers, columnTypes, rows, firstRows);
        }
    }

    @BlockMetadata(
            name = "writeCsv",
            description = "",
            identifier = "Spreadsheet.writeCsv",
            category = "Core")
    public static void writeCsv(File csv, DataSheet dataSheet) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csv)); CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(dataSheet.getHeaderRow().toArray(new String[0])))) {

            for (List<Object> row : dataSheet.getDataRows()) {
                csvPrinter.printRecord(row);
            }
        }
    }

    @BlockMetadata(
            name = "readExcel",
            description = "",
            identifier = "Spreadsheet.readExcel",
            category = "Core")
    public static DataSheet readExcel(File excel) throws IOException {
        try (FileInputStream fis = new FileInputStream(excel); Workbook workbook = new XSSFWorkbook(fis)) {

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

            int headerRowNumber = detectHeaderRowNumber(rows);
            List<String> headers = getHeadersByRowNumber(rows, headerRowNumber);
            List<List<Object>> firstRows = removeFirstRows(rows, headerRowNumber);

            Map<String, Class<?>> columnTypes = detectColumnTypes(rows);
            return new DataSheet(headers, columnTypes, rows, firstRows);
        }
    }

    private static List<List<Object>> removeFirstRows(List<List<Object>> rows, int rowNumber) {
        List<List<Object>> firstRows = new ArrayList<>();
        for (int i = 0; i <= rowNumber; i++) {
            firstRows.add(rows.removeFirst());
        }
        return firstRows;
    }

    private static List<String> getHeadersByRowNumber(List<List<Object>> rows, int rowNumber) {
        List<String> headers = new ArrayList<>();
        List<Object> headerRow = rows.get(rowNumber);
        for (Object header : headerRow) {
            headers.add((String) header);
        }
        return headers;
    }

    private static int detectHeaderRowNumber(List<List<Object>> rows) {
        int mostRecurringColumnCount = getMostRecurringColumnCount(rows);

        // case A - promote the first row to header if it has the same number of colums as the data
        if (rows.get(0).size() == mostRecurringColumnCount) {
            return 0;

            // case B - look for the first row that has as many columns as the data and does not have any missing data
        } else {
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
        }
        return -1;
    }

    private static int getMostRecurringColumnCount(List<List<Object>> rows) {
        Map<Integer, Integer> columnCount = new HashMap<>();

        for (List<Object> row : rows) {

            int lastCell = row.size();
            if (columnCount.containsKey(lastCell)) {
                int count = columnCount.get(lastCell) + 1;
                columnCount.put(lastCell, count);
            } else {
                columnCount.put(lastCell, 1);
            }
        }

        Integer mostRecurringColumnCount = -1;
        Integer recurrences = -1;

        for (Entry<Integer, Integer> count : columnCount.entrySet()) {
            if (count.getValue() > recurrences) {
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
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(excel)) {

            Sheet sheet = workbook.createSheet("Sheet1");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < dataSheet.getHeaderRow().size(); i++) {
                headerRow.createCell(i).setCellValue(dataSheet.getHeaderRow().get(i));
            }

            int rowIndex = 1;
            for (List<Object> row : dataSheet.getDataRows()) {
                Row excelRow = sheet.createRow(rowIndex++);
                for (int i = 0; i < row.size(); i++) {
                    setCellValue(excelRow.createCell(i), row.get(i));
                }
            }

            workbook.write(fos);
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
            return "";
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
                if (number % 1 == 0) { // If no decimal part
                    if (number >= Integer.MIN_VALUE && number <= Integer.MAX_VALUE) {
                        return (int) number; // Convert to Integer
                    } else {
                        return (long) number; // Convert to Long
                    }
                }
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
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

        return new DataSheet(dataSheet.getHeaderRow(), dataSheet.getColumnTypes(), filteredRows, dataSheet.getLeadingRows());
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

        return new DataSheet(dataSheet.getHeaderRow(), dataSheet.getColumnTypes(), sortedRows, dataSheet.getLeadingRows());
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

        List<List<Object>> mergedRows = new ArrayList<>(sheet1.getDataRows());
        mergedRows.addAll(sheet2.getDataRows());

        List<List<Object>> mergedFirstRows = new ArrayList<>(sheet1.getLeadingRows());
        mergedFirstRows.addAll(sheet2.getLeadingRows());

        return new DataSheet(sheet1.getHeaderRow(), sheet1.getColumnTypes(), mergedRows, mergedFirstRows);
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
        DataSheet sheet = readExcel(excel);
        writeCsv(csv, sheet);
    }

    @BlockMetadata(
            name = "convertCsvToExcel",
            description = "",
            identifier = "Spreadsheet.convertCsvToExcel",
            category = "Core")
    public static void convertCsvToExcel(File csv, File excel) throws IOException {
        DataSheet sheet = readCsv(csv);
        writeExcel(excel, sheet);
    }

}
