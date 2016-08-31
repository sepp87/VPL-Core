package jo.vpl.core;

import java.awt.MouseInfo;
import java.util.*;
import java.util.logging.*;
import static java.util.stream.Collectors.toCollection;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import org.reflections.Reflections;

import javafx.scene.input.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;

/**
 *
 * @author JoostMeulenkamp
 */
public class SelectHub extends Hub {

    private ListView listView;
    private TextField searchField;
    private static Map<String, Class> hubMap = new HashMap<>();
    private static ObservableList<String> hubList = observableArrayList();

    /**
     * Retrieve all hubs from jo.vpl.hub package
     */
    static {
        Reflections reflections = new Reflections("jo.vpl.hub");
        Set<Class<? extends Hub>> hubTypes = reflections.getSubTypesOf(Hub.class);

        for (Class type : hubTypes) {
            if (type.isAnnotationPresent(HubInfo.class)) {
                HubInfo info = (HubInfo) type.getAnnotation(HubInfo.class);
                hubMap.put(info.name(), type);
                hubList.add(info.name());
            }
        }

        Collections.sort(hubList);
    }

    /**
     * Select hub is used to pick a hub type and place it on the host canvas.
     * It reads out classes inside the package and adds them to a list view.
     *
     * @param hostCanvas
     */
    public SelectHub(VPLControl hostCanvas) {
        super(hostCanvas);

        searchField = new TextField();
        searchField.setMaxWidth(140);
        searchField.setPromptText("Search...");

        listView = new ListView();
        listView.layoutBoundsProperty().addListener(e -> {
            ScrollBar scrollBarv = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            scrollBarv.setDisable(true);
        });

        listView.setMaxWidth(240);
        listView.setPrefHeight(127);

        listView.setItems(hubList);

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
             * Regular expression to filter the list with.
             * (?i) : makes it ignore case
             * \Q ... \E : takes care of possible special characters in keyWord
             * . : means any character
             * * : zero to multiple times recurring
             */

            String regex = "(?si).*\\Q" + keyWord + "\\E.*";

            /**
             * OPTION
             * List<String> list = hubList.stream()
             * .filter(x -> x.matches(regex))
             * .collect(Collectors.toList());
             * ObservableList<String> tempList = observableArrayList();
             * for (String s : list) {
             * tempList.add(s);
             * }
             * listView.setItems(tempList);
             *
             * OPTION
             * listView.setItems(hubList.stream()
             * .filter(x -> x.matches(regex))
             * .collect(collectingAndThen(toList(), l ->
             * FXCollections.observableArrayList(l))));
             *
             * OPTION
             */
            listView.setItems(hubList.stream()
                    .filter(x -> x.matches(regex))
                    .collect(toCollection(FXCollections::observableArrayList)));

            if (listView.getSelectionModel().getSelectedIndex() == -1) {
                listView.getSelectionModel().selectFirst();
            }

        } else {
            listView.setItems(hubList);
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
        String selectedType = (String) listView.getSelectionModel().getSelectedItem();

        if (selectedType == null) {
            return;
        }

        //Maybe move this to Hostcanvas
        Class type = hubMap.get(selectedType);

        try {
            Hub hub = (Hub) type.getConstructor(VPLControl.class).newInstance(hostCanvas);

            double x = MouseInfo.getPointerInfo().getLocation().x;
            double y = MouseInfo.getPointerInfo().getLocation().y;
            Point2D pt = hostCanvas.screenToLocal(x, y);

            hub.setLayoutX(pt.getX() - 20);
            hub.setLayoutY(pt.getY() - 20);

            hostCanvas.getChildren().add(hub);
            hostCanvas.hubSet.add(hub);
            removed = true;
            hostCanvas.getChildren().remove(this);
        } catch (Exception e) {
            Logger.getLogger(SelectHub.class.getName()).log(Level.SEVERE, null, e);
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
    public Hub clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
