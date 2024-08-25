package vpllib.util;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
public class TablePane extends HBox {

    ObservableList<Column> columns;
    ObservableList<ColumnConstraints> columnConstraints;

    public TablePane() {
        getStylesheets().add("css/table.css");
        getStyleClass().add("table-pane");
        columns = FXCollections.observableArrayList();
        columnConstraints = FXCollections.observableArrayList();
        columnConstraints.addListener(columnConstraintsListener);
    }

    ListChangeListener<ColumnConstraints> columnConstraintsListener = new ListChangeListener<ColumnConstraints>() {

        @Override
        public void onChanged(ListChangeListener.Change<? extends ColumnConstraints> c) {

            int size = columnConstraints.size();
            int colSize = columns.size();
            for (int i = 0; i < size; i++) {
                if (i == colSize) {
                    break;
                }
                ColumnConstraints constraint = columnConstraints.get(i);
                Column col = columns.get(i);
                col.prefWidthProperty().unbind();
                col.minWidthProperty().unbind();
                col.maxWidthProperty().unbind();
                col.prefWidthProperty().bind(constraint.prefWidthProperty());
                col.minWidthProperty().bind(constraint.minWidthProperty());
                col.maxWidthProperty().bind(constraint.maxWidthProperty());
            }
        }
    };

    public int getColumnSize() {
        return columns.size();
    }

    public int getRowSize() {
        if (columns.size() > 0) {
//            System.out.println(columns.get(0).getChildren().size());
            return columns.get(0).getChildren().size();
        } else {
            return 0;
        }
    }

    public final ObservableList<ColumnConstraints> getColumnConstraints() {
        return columnConstraints;
    }

    public void addColumn() {
        int rowSize = getRowSize();
        Column column = new Column();
        while (rowSize > 0) {
            Cell cell = new Cell();
            column.getChildren().add(cell);
            rowSize--;
        }
//        column.setSpacing(2);
        columns.add(column);
        getChildren().add(column);
    }

    public void removeColumn(int i) {
        columns.remove(i);
        getChildren().remove(i);
    }

    public void addRow() {
        for (Column col : columns) {
            Cell cell = new Cell();
            cell.prefWidthProperty().bind(col.prefWidthProperty());
            cell.minWidthProperty().bind(col.minWidthProperty());
            cell.maxWidthProperty().bind(col.maxWidthProperty());
            col.getChildren().add(cell);
        }
    }

    public void removeRow(int i) {
        for (Column col : columns) {
            col.getChildren().remove(i);
        }
    }

    public void add(Region node, int x, int y) {
        int columnSize = columns.size();

        if (x == columnSize) {
            addColumn();
            Column col = columns.get(x);
            add(node, col, y);
        } else if (x < columnSize) {
            Column col = columns.get(x);
            add(node, col, y);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private void add(Region node, Column col, int y) {
        int rowSize = col.getChildren().size();
        if (y == rowSize) {
            addRow();
            Cell cell = (Cell) col.getChildren().get(y);
            node.prefWidthProperty().bind(col.prefWidthProperty());
            node.minWidthProperty().bind(col.minWidthProperty());
            node.maxWidthProperty().bind(col.maxWidthProperty());
            cell.getChildren().add(node);
        } else if (y < rowSize) {
            Cell cell = (Cell) col.getChildren().get(y);
            node.prefWidthProperty().bind(col.prefWidthProperty());
            node.minWidthProperty().bind(col.minWidthProperty());
            node.maxWidthProperty().bind(col.maxWidthProperty());
            cell.getChildren().add(node);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public class Column extends VBox {

        ObservableList<Node> cells;

        public Column() {
            getStyleClass().add("column");
            cells = FXCollections.observableArrayList();
        }

        public void setRowHeight(Double y) {
            throw new UnsupportedOperationException();
        }
    }

    public class Cell extends HBox {

        public Cell() {
            getStyleClass().add("cell");
        }
    }
}
