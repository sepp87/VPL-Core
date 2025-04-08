package vpllib.spreadsheet;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.model.Comments;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author joostmeulenkamp
 */
public class TypedSheetXMLHandler extends XSSFSheetXMLHandler {

    private final TypedSheetContentsHandler sheetHandler;

    public TypedSheetXMLHandler(
            StylesTable styles,
            SharedStrings sharedStrings,
            TypedSheetContentsHandler sheetHandler,
            boolean formulasNotResults
    ) {
        super(styles, sharedStrings, sheetHandler, formulasNotResults);
        this.sheetHandler = sheetHandler;
    }

    public TypedSheetXMLHandler(
            StylesTable styles,
            Comments comments,
            SharedStrings sharedStrings,
            TypedSheetContentsHandler sheetHandler,
            DataFormatter dataFormatter,
            boolean formulasNotResults
    ) {
        super(styles, comments, sharedStrings, sheetHandler, dataFormatter, formulasNotResults);
        this.sheetHandler = sheetHandler;
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equals("c")) {
            String currentCellType = attributes.getValue("t"); // capture the type
//            System.out.println(currentCellType);
            sheetHandler.setCurrentCellType(currentCellType);

            String styleStr = attributes.getValue("s");
            int currentStyleIndex = (styleStr != null) ? Integer.parseInt(styleStr) : -1;
            sheetHandler.setCurrentStyleIndex(currentStyleIndex);

        } else if (name.equals("f")) {
            
        }
        super.startElement(uri, localName, name, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (name.equals("c")) {

        }
        super.endElement(uri, localName, name);
    }

}
