package btslib.spreadsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author joostmeulenkamp
 */
public class ExcelUtils {

    public static LocalDate parseExcelDate(String rawValue) {
        return parseExcelDate(rawValue, ZoneId.systemDefault());
    }

    public static LocalDate parseExcelDate(String rawValue, ZoneId zone) {
        try {
            double numericDate = Double.parseDouble(rawValue);
            Date javaDate = DateUtil.getJavaDate(numericDate); // Converts Excel number to java.util.Dates
            return javaDate.toInstant().atZone(zone).toLocalDate();
        } catch (NumberFormatException e) {
            // Handle invalid date value
            Logger.getLogger(ExcelUtils.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static List<List<Object>> readStream(File excel) throws InvalidFormatException, IOException, SAXException, OpenXML4JException, Exception {

        // Open the Excel file as an OPCPackage
        OPCPackage pkg = OPCPackage.open(excel);
        XSSFReader reader = new XSSFReader(pkg);

        // Get shared strings and styles table
        ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(pkg);
        StylesTable styles = reader.getStylesTable();

        // Iterate through each sheet
        Iterator<InputStream> sheets = reader.getSheetsData();
        int sheetIndex = 1;
        while (sheets.hasNext()) {
            try (InputStream sheetStream = sheets.next()) {
                System.out.println("Reading Sheet " + sheetIndex + "...");
                List<List<Object>> rows = parseSheet(styles, sst, sheetStream);
                sheetIndex++;

                // For now just read the first sheet
                pkg.close();
                return rows;
            }
        }

        pkg.close();
        return Collections.emptyList();
    }

    private static List<List<Object>> parseSheet(StylesTable styles, ReadOnlySharedStringsTable sst, InputStream sheetInputStream) throws Exception {

        // Use SAXParserFactory with secure settings
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);  // Ensure it processes namespaces correctly
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // Secure parsing

        // Create an XMLReader (SAX parser)
        SAXParser saxParser = factory.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        // Set content handler to process the sheet
        Matrix matrix = new Matrix();
        TypedSheetContentsHandler sheetHandler = new TypedSheetContentsHandler(styles, matrix);
        xmlReader.setContentHandler(new TypedSheetXMLHandler(styles, sst, sheetHandler, false));

        // Parse the sheet
        xmlReader.parse(new InputSource(sheetInputStream));

        return matrix.getAllRows();
    }

    public static void write(File excel, DataSheet dataSheet) {

        // Create a Workbook 
        // TODO is   workbook.dispose();   needed?
        try (SXSSFWorkbook workbook = new SXSSFWorkbook();) {
            // Create a sheet
            Sheet sheet = workbook.createSheet("data");

            List<List<Object>> rows = dataSheet.getAllRows();
            int rowNumber = 0;

            List<Integer> widths = new ArrayList<>();

            // Create data rows
            for (List<Object> list : rows) {
                int columnNumber = 0;
                Row row = sheet.createRow(rowNumber);
                for (Object value : list) {
                    Cell cell = row.createCell(columnNumber);
                    int width = 0;
                    if (value != null) {
                        String str = value.toString();
                        cell.setCellValue(str);
                        width = str.length();
                    }
                    if (width > 255) {
                        width = 255;
                    }
                    if (columnNumber < widths.size()) {
                        if (width > widths.get(columnNumber)) {
                            widths.add(columnNumber, width);
                        }
                    } else {
                        widths.add(columnNumber, width);
                    }

                    columnNumber++;
                }
                rowNumber++;
            }

            // Resize all columns to fit the content size
            for (int j = 0; j < widths.size(); j++) {
                sheet.setColumnWidth(j, widths.get(j) * 256);
            }

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(excel);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (IOException ex) {
            Logger.getLogger(ExcelUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
