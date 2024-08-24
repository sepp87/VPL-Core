package jo.vpl.core;

import java.awt.MouseInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.logging.*;
import static java.util.stream.Collectors.toCollection;
import javafx.collections.FXCollections;

import javafx.scene.input.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import jo.vpl.block.ReflectionHub;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public class SelectBlock extends Block {

    private ListView<String> listView;
    private TextField searchField;

    /**
     * Select hub is used to pick a hub type and place it on the host canvas. It
     * reads out classes inside the package and adds them to a list view.
     *
     * @param hostCanvas
     */
    public SelectBlock(Workspace hostCanvas) {
        super(hostCanvas);

        searchField = new TextField();
        searchField.setMaxWidth(140);
        searchField.setPromptText("Search...");

        listView = new ListView<>();
        listView.layoutBoundsProperty().addListener(e -> {
            ScrollBar scrollBarv = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            scrollBarv.setDisable(true);
        });

        listView.setMaxWidth(240);
//        listView.setPrefHeight(127);
        listView.setPrefHeight(265);

        listView.setItems(BlockLoader.HUB_TYPE_LIST);

        this.setOnMouseExited(this::selectHub_MouseExit);
        this.setOnMouseDragExited(this::selectHub_MouseExit);
        this.setOnMouseEntered(this::selectHub_MouseEnter);
        listView.setOnMousePressed(this::listView_MousePress);
        searchField.setOnKeyPressed(this::searchField_KeyPress);
        searchField.setOnKeyReleased(this::searchField_KeyRelease);

        VBox searchBox = new VBox(10);
//        searchBox.setStyle("-fx-spacing: 10;\n"
//                + "-fx-aligment: center;\n"
//                + "-fx-padding: 5 0 5 0;\n");
        searchBox.getChildren().addAll(searchField, listView);

        mainContentGrid.setStyle("-fx-padding: 0;");

        addControlToHub(searchBox);
    }

    /**
     * Place focus back on search field when selecting a list item
     *
     * @param e
     */
    private void listView_MousePress(MouseEvent e) {
        searchField.requestFocus();

        if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
            return;
        }

        if (e.getClickCount() == 2) {
            createHub();
        }
    }

    /**
     * Search for a particular hub type matching the search phrase
     *
     * @param e
     */
    private void searchField_KeyRelease(KeyEvent e) {
        String keyWord = searchField.getText();

        if (!"".equals(keyWord)) {
            /**
             * Regular expression to filter the list with. (?i) : makes it
             * ignore case \Q ... \E : takes care of possible special characters
             * in keyWord . : means any character * : zero to multiple times
             * recurring
             */

            String regex = "(?si).*\\Q" + keyWord + "\\E.*";

            /**
             * OPTION List<String> list = hubList.stream() .filter(x ->
             * x.matches(regex)) .collect(Collectors.toList());
             * ObservableList<String> tempList = observableArrayList(); for
             * (String s : list) { tempList.add(s); }
             * listView.setItems(tempList);
             *
             * OPTION listView.setItems(hubList.stream() .filter(x ->
             * x.matches(regex)) .collect(collectingAndThen(toList(), l ->
             * FXCollections.observableArrayList(l))));
             *
             * OPTION
             */
            listView.setItems(BlockLoader.HUB_TYPE_LIST.stream()
                    .filter(x -> x.matches(regex))
                    .collect(toCollection(FXCollections::observableArrayList)));

            if (listView.getSelectionModel().getSelectedIndex() == -1) {
                listView.getSelectionModel().selectFirst();
            }

        } else {
            listView.setItems(BlockLoader.HUB_TYPE_LIST);
        }
    }

    /**
     * Handle key events in text field. Up and down changes the selected list
     * index. Enter confirms choice of selection and generates a type of hub.
     *
     * @param e
     */
    private void searchField_KeyPress(KeyEvent e) {

        KeyCode key = e.getCode();

        int listSize = listView.getItems().size();
        int listIndex = listView.getSelectionModel().getSelectedIndex();

        if (key == KeyCode.ENTER && listSize > 0) {
            if ((!searchField.getText().equals(""))
                    || (listIndex > -1 && listIndex < listSize)) {
                createHub();

            } else {
//                Dispose();
//                HostCanvas.Children.Remove(Border);

            }
        } else {
            switch (key) {
                case DOWN:
                    if (listIndex < listSize) {
                        listView.getSelectionModel().select(listIndex + 1);
                        e.consume();
                    }
                    break;

                case UP:
                    if (listIndex > 0) {
                        listView.getSelectionModel().select(listIndex - 1);
                    }
                    e.consume();
                    break;
            }
        }
    }

    /**
     * Create a hub of the selected type and insert it at the mouse position
     */
    private void createHub() {
        String selectedType = listView.getSelectionModel().getSelectedItem();

        if (selectedType == null) {
            return;
        }

        Object type = BlockLoader.HUB_LIBRARY.get(selectedType);

        if (type.getClass().equals(Class.class)) {
            try {
                Class<?> cType = (Class<?>) type;
                Block hub = (Block) cType.getConstructor(Workspace.class).newInstance(hostCanvas);

                hub.setLayoutX(hostCanvas.mousePosition.getX() - 20);
                hub.setLayoutY(hostCanvas.mousePosition.getY() - 20);

                hostCanvas.getChildren().add(hub);
                hostCanvas.hubSet.add(hub);
                removed = true;
                hostCanvas.getChildren().remove(this);
            } catch (Exception e) {
                Logger.getLogger(SelectBlock.class.getName()).log(Level.SEVERE, null, e);
            }

        } else if (type.getClass().equals(Method.class)) {
            try {
                Method mType = (Method) type;
                BlockInfo info = mType.getAnnotation(BlockInfo.class);
                ReflectionHub hub = new ReflectionHub(hostCanvas, info.identifier(), info.category(), info.description(), info.tags(), mType);

                Class<?> returnType = mType.getReturnType();
                if (returnType.equals(Number.class)) {
                    hub.addOutPortToHub("double", double.class);
                } else if (List.class.isAssignableFrom(returnType)) {
                    
                    hub.isListOperatorListReturnType = true;
                    hub.addOutPortToHub(Object.class.getSimpleName(), Object.class);
                } else {
                    hub.addOutPortToHub(returnType.getSimpleName(), returnType);
                }

                if (!info.name().equals("") && info.icon().equals(IconType.NULL)) {
                    hub.setName(info.name());
                    Label label = new Label(info.name());
                    label.getStyleClass().add("hub-text");
                    hub.addControlToHub(label);
                } else {
                    String shortName = info.identifier().split("\\.")[1];
                    hub.setName(shortName);
                    Label label = new Label(shortName);
                    label.getStyleClass().add("hub-text");
                    hub.addControlToHub(label);
                }

                if (!info.icon().equals(IconType.NULL)) {
                    Label label = hub.getAwesomeIcon(info.icon());
                    hub.addControlToHub(label);
                }

                // If first input parameter is of type list, then this is a list operator hub
                if (List.class.isAssignableFrom(mType.getParameters()[0].getType())) {
                    hub.isListOperator = true;
                }

                for (Parameter p : mType.getParameters()) {
                    if (List.class.isAssignableFrom(p.getType())) {
                        hub.addInPortToHub("Object : List", Object.class);
                    } else {
                        hub.addInPortToHub(p.getName(), p.getType());
                    }
                }

                hub.setLayoutX(hostCanvas.mousePosition.getX() - 20);
                hub.setLayoutY(hostCanvas.mousePosition.getY() - 20);

                hostCanvas.getChildren().add(hub);
                hostCanvas.hubSet.add(hub);
                removed = true;
                hostCanvas.getChildren().remove(this);
            } catch (Exception e) {
                Logger.getLogger(SelectBlock.class.getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    boolean removed = false;

    /**
     * Remove hub from host canvas if user move his mouse outside of the panel.
     * Event also gets fired when deleted by other means, which will lead to a
     * Duplicate Children Added Exception when from createHub() and a Array Out
     * Of Bounds Exception in searchField_KeyPress(). Check against the removed
     * boolean prevents this from happening.
     *
     * @param e
     */
    private void selectHub_MouseExit(MouseEvent e) {
        if (!removed) {
            hostCanvas.getChildren().remove(this);
        }
        e.consume();

    }

    /**
     * Search field requests focus on opening of select hub so user can start
     * typing
     *
     * @param e
     */
    private void selectHub_MouseEnter(MouseEvent e) {
        TextField text = (TextField) controls.get(0).getChildrenUnmodifiable().get(0);
        text.requestFocus();
    }

    @Override
    public void calculate() {
    }

    @Override
    public Block clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
