/**
 * Copyright 2013 (C) Mr LoNee - (Laurent NICOLAS) - www.mrlonee.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package jo.vpl.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import jo.vpl.radialmenu.RadialMenuItem;
import jo.vpl.radialmenu.RadialMenuItemBuilder;
import jo.vpl.util.IconType;

public class SpiffyMenu extends Group {

    private Workspace hostCanvas;

    private final Group itemsContainer = new Group();
    private Color baseColor = Color.web("e0e0e0");
    private Color hoverColor = Color.web("d35f5f");
    private Color selectionColor = Color.BLACK;
    private Color valueColor = Color.web("d35f5f");
    private Color valueHoverColor = Color.web("d35f5f");

    private final double menuSize = 45;
    private final double innerRadius = 50;
    private final double radius = 120;
    private final List<RadialMenuItem> items = new ArrayList<RadialMenuItem>();
    private final DoubleProperty initialAngle = new SimpleDoubleProperty(22.5);
    private final SpiffyMenuCenter centerNode = new SpiffyMenuCenter();
    private SelectionEventHandler selectionEventHandler = new SelectionEventHandler();
    private RadialMenuItem selectedItem = null;
    private final Map<RadialMenuItem, List<RadialMenuItem>> itemToValues = new HashMap<RadialMenuItem, List<RadialMenuItem>>();
    private final Map<RadialMenuItem, Group> itemToGroupValue = new HashMap<RadialMenuItem, Group>();
    private final Map<RadialMenuItem, Label> itemAndValueToIcon = new HashMap<>();
    private final Map<RadialMenuItem, Label> itemAndValueToWhiteIcon = new HashMap<>();
    private final Map<RadialMenuItem, RadialMenuItem> valueItemToItem = new HashMap<RadialMenuItem, RadialMenuItem>();
    private final Group notSelectedItemEffect;
    private Transition openAnim;

    public SpiffyMenu(Workspace vplControl) {
        hostCanvas = vplControl;

        itemsContainer.getStyleClass().add("radial-menu");

        if (true) {
            baseColor = Color.WHITE;
            hoverColor = Color.SALMON;
            selectionColor = Color.web("333333");
            valueColor = Color.SALMON;
            valueHoverColor = Color.SALMON;
        } else {
            baseColor = Color.WHITE;
            hoverColor = Color.LIGHTBLUE;
            selectionColor = Color.BLACK;
            valueColor = Color.LIGHTBLUE;
            valueHoverColor = Color.LIGHTBLUE;
        }

        initialAngle.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    final ObservableValue<? extends Number> paramObservableValue,
                    final Number paramT1, final Number paramT2) {
                SpiffyMenu.this.setInitialAngle(paramObservableValue
                        .getValue().doubleValue());
            }
        });
        centerNode.visibleProperty().bind(visibleProperty());
        getChildren().add(itemsContainer);
        getChildren().add(centerNode);

        //Build menu
        RadialMenuItem item = addMenuItem(IconType.FA_SORT_AMOUNT_ASC, MenuAction.ALIGN);
        addMenuItem(IconType.FA_CLONE, MenuAction.COPY);
        addMenuItem(IconType.FA_CLIPBOARD, MenuAction.PASTE);
        addMenuItem(IconType.FA_OBJECT_GROUP, MenuAction.GROUP);
        addMenuItem(IconType.FA_FILE_O, MenuAction.NEW);
        addMenuItem(IconType.FA_FOLDER_OPEN_O, MenuAction.OPEN);
        addMenuItem(IconType.FA_FLOPPY_O, MenuAction.SAVE);
        addMenuItem(IconType.FA_SEARCH, MenuAction.ZOOM_TO_FIT);

        //Build sub-menu
        List<RadialMenuItem> subItems = new ArrayList<>();
        final RadialMenuItem right = newValueRadialMenuItem(IconType.FA_ALIGN_RIGHT, AlignType.RIGHT);
        final RadialMenuItem vCenter = newValueRadialMenuItem(IconType.FA_ALIGN_CENTER, AlignType.V_CENTER);
        final RadialMenuItem top = newValueRadialMenuItem(IconType.FA_ALIGN_LEFT, AlignType.TOP, 90);
        final RadialMenuItem bottom = newValueRadialMenuItem(IconType.FA_ALIGN_RIGHT, AlignType.BOTTOM, 90);
        final RadialMenuItem left = newValueRadialMenuItem(IconType.FA_ALIGN_LEFT, AlignType.LEFT);
        final RadialMenuItem hCenter = newValueRadialMenuItem(IconType.FA_ALIGN_CENTER, AlignType.H_CENTER, 90);

        subItems.add(bottom);
        subItems.add(right);
        subItems.add(hCenter);
        subItems.add(vCenter);
        subItems.add(left);
        subItems.add(top);

        addSubMenuItems(item, subItems);

        centerNode.addCenterItem(getIconBig(IconType.FA_POWER_OFF));
        centerNode.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent event) {
                if (selectedItem == null) {
                    centerNode.displayCenter();
                }
            }
        });

        centerNode.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent event) {
                if (selectedItem == null) {
                    centerNode.hideCenter();
                }
            }
        });

        centerNode.setOnMouseClicked(this::handle_CloseMenu);

        final RadialMenuItem notSelected1 = createNotSelectedItemEffect();
        final RadialMenuItem notSelected2 = createNotSelectedItemEffect();
        notSelected2.setClockwise(false);

        notSelectedItemEffect = new Group(notSelected1, notSelected2);
        notSelectedItemEffect.setVisible(false);
        notSelectedItemEffect.setOpacity(0);

        itemsContainer.getChildren().add(notSelectedItemEffect);

        computeItemsStartAngle();

//        setTranslateX(210);
//        setTranslateY(210);
    }

    private RadialMenuItem createNotSelectedItemEffect() {
        final RadialMenuItem notSelectedItemEffect = RadialMenuItemBuilder
                .create().length(180).backgroundFill(baseColor).startAngle(0)
                .strokeFill(baseColor).backgroundMouseOnFill(baseColor)
                .strokeMouseOnFill(baseColor).innerRadius(innerRadius)
                .radius(radius).offset(0).clockwise(true).strokeVisible(true)
                .backgroundVisible(true).build();
        return notSelectedItemEffect;
    }

    private RadialMenuItem addMenuItem(IconType type, Enum actionType) {

        Label active = getIconActive(type);
        Label passive = getIconPassive(type);

        final RadialMenuItem item = newRadialMenuItem(passive, active, actionType);

        itemsContainer.getChildren().addAll(item);

        item.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent event) {
                // TODO Animate the little long click spoiler...
            }
        });

        item.addEventHandler(MouseEvent.MOUSE_CLICKED, selectionEventHandler);

        return item;

    }

    private void addSubMenuItems(RadialMenuItem item, List<RadialMenuItem> subItems) {

        Group valueGroup = new Group();
        for (RadialMenuItem subItem : subItems) {
            valueGroup.getChildren().add(subItem);
            valueItemToItem.put(subItem, item);
            subItem.addEventHandler(MouseEvent.MOUSE_CLICKED, selectionEventHandler);
        }

        itemToValues.put(item, subItems);
        itemToGroupValue.put(item, valueGroup);
        valueGroup.setVisible(false);

        itemsContainer.getChildren().addAll(valueGroup);
    }

    private RadialMenuItem newValueRadialMenuItem(IconType type, Enum actionType) {
        return newValueRadialMenuItem(type, actionType, 0);
    }

    private RadialMenuItem newValueRadialMenuItem(IconType type, Enum actionType, double rotation) {

        Label label = getIconPassive(type);

        label.setRotate(rotation);

        final RadialMenuItem item = RadialMenuItemBuilder.create(actionType)
                .length(menuSize).backgroundFill(valueColor)
                .strokeFill(valueColor).backgroundMouseOnFill(valueHoverColor)
                .strokeMouseOnFill(valueHoverColor).innerRadius(innerRadius)
                .radius(radius).offset(0).clockwise(true).graphic(label)
                .backgroundVisible(true).strokeVisible(true).build();

        item.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(final MouseEvent event) {
                final RadialMenuItem valuItem = (RadialMenuItem) event
                        .getSource();
                final RadialMenuItem item = valueItemToItem.get(valuItem);
                SpiffyMenu.this.closeValueSelection(item);
            }

        });
        itemAndValueToIcon.put(item, label);
        return item;
    }

    private RadialMenuItem newRadialMenuItem(Label passive,
            Label active, Enum actionType) {

        final RadialMenuItem item = RadialMenuItemBuilder.create(actionType)
                .backgroundFill(baseColor).strokeFill(baseColor)
                .backgroundMouseOnFill(hoverColor)
                .strokeMouseOnFill(hoverColor).radius(radius)
                .innerRadius(innerRadius).length(menuSize).clockwise(true)
                .backgroundVisible(true).strokeVisible(true).offset(0).build();

        if (active != null) {
            item.setGraphic(new Group(passive, active));
            active.setOpacity(0.0);
        } else {
            item.setGraphic(new Group(passive));
        }
        items.add(item);
        itemAndValueToIcon.put(item, passive);
        itemAndValueToWhiteIcon.put(item, active);
        return item;
    }

    private void computeItemsStartAngle() {
        double angleOffset = initialAngle.get();
        for (final RadialMenuItem item : items) {
            item.setStartAngle(angleOffset);
            angleOffset = angleOffset + item.getLength();
        }
    }

    private void setInitialAngle(final double angle) {
        initialAngle.set(angle);
        computeItemsStartAngle();

    }

    public class SelectionEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(final MouseEvent event) {
            final RadialMenuItem newSelectedItem = (RadialMenuItem) event.getSource();

            performAction(newSelectedItem.getType());

            // Check if sub menu items are present
            if (!itemToGroupValue.containsKey(newSelectedItem)) {
                return;
            }

            if (selectedItem == newSelectedItem) {
                closeValueSelection(newSelectedItem);

            } else {
                openValueSelection(newSelectedItem);
            }
        }

        private void performAction(Enum type) {

            if (type instanceof MenuAction) {
                MenuAction action = (MenuAction) type;

                switch (action) {

                    case NEW:
                        hostCanvas.newFile();
                        break;
                    case OPEN:
                        hostCanvas.openFile();
                        break;
                    case SAVE:
                        hostCanvas.saveFile();
                        break;
                    case COPY:
                        hostCanvas.copyHubs();
                        break;
                    case PASTE:
                        hostCanvas.pasteHubs();
                        break;
                    case ZOOM_TO_FIT:
                        hostCanvas.zoomToFit();
                        break;
                    case GROUP:
                        hostCanvas.groupHubs();
                        break;
                    case ALIGN:
                        return;
                }

            } else if (type instanceof AlignType) {
                AlignType align = (AlignType) type;
                hostCanvas.align(align);
            }
            hideMenu();
        }
    }

    private void openValueSelection(final RadialMenuItem newSelectedItem) {
        selectedItem = newSelectedItem;

        notSelectedItemEffect.toFront();

        itemToGroupValue.get(selectedItem).setVisible(true);
        itemToGroupValue.get(selectedItem).toFront();
        selectedItem.toFront();

        openAnim = createOpenAnimation(selectedItem);
        openAnim.play();

    }

    private void closeValueSelection(final RadialMenuItem newSelectedItem) {
        openAnim.setAutoReverse(true);
        openAnim.setCycleCount(2);
        openAnim.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(final ActionEvent event) {
                newSelectedItem.setBackgroundFill(baseColor);
                newSelectedItem.setStrokeFill(baseColor);
                newSelectedItem.setBackgroundMouseOnFill(hoverColor);
                newSelectedItem.setStrokeMouseOnFill(hoverColor);
                notSelectedItemEffect.setVisible(false);
                itemToGroupValue.get(newSelectedItem).setVisible(false);
            }

        });
        openAnim.playFrom(Duration.millis(400));
        selectedItem = null;

    }

    private Transition createOpenAnimation(final RadialMenuItem newSelectedItem) {

        // Children slide animation
        final List<RadialMenuItem> children = itemToValues.get(newSelectedItem);

        double startAngleEnd = 0;
        final double startAngleBegin = newSelectedItem.getStartAngle();
        final ParallelTransition transition = new ParallelTransition();

        itemToGroupValue.get(newSelectedItem).setVisible(true);
        int internalCounter = 1;
        for (int i = 0; i < children.size(); i++) {
            final RadialMenuItem it = children.get(i);
            if (i % 2 == 0) {
                startAngleEnd = startAngleBegin + internalCounter
                        * it.getLength();
            } else {
                startAngleEnd = startAngleBegin - internalCounter
                        * it.getLength();
                internalCounter++;
            }

            final Animation itemTransition = new Timeline(new KeyFrame(
                    Duration.ZERO, new KeyValue(it.startAngleProperty(),
                            startAngleBegin)), new KeyFrame(
                    Duration.millis(400), new KeyValue(it.startAngleProperty(),
                    startAngleEnd)));

            transition.getChildren().add(itemTransition);

            final Label image = itemAndValueToIcon.get(it);
            image.setOpacity(0.0);
            final Timeline iconTransition = new Timeline(new KeyFrame(
                    Duration.millis(0),
                    new KeyValue(image.opacityProperty(), 0)), new KeyFrame(
                    Duration.millis(300), new KeyValue(image.opacityProperty(),
                    0)), new KeyFrame(Duration.millis(400),
                    new KeyValue(image.opacityProperty(), 1.0)));

            transition.getChildren().add(iconTransition);
        }

        // Selected item background color change
        final DoubleProperty backgroundColorAnimValue = new SimpleDoubleProperty();
        final ChangeListener<? super Number> listener = new ChangeListener<Number>() {

            @Override
            public void changed(final ObservableValue<? extends Number> arg0,
                    final Number arg1, final Number arg2) {
                final Color c = hoverColor.interpolate(selectionColor,
                        arg2.floatValue());

                newSelectedItem.setBackgroundFill(c);
                newSelectedItem.setStrokeFill(c);
                newSelectedItem.setBackgroundMouseOnFill(c);
                newSelectedItem.setStrokeMouseOnFill(c);
            }
        };

        backgroundColorAnimValue.addListener(listener);

        final Animation itemTransition = new Timeline(new KeyFrame(
                Duration.ZERO, new KeyValue(backgroundColorAnimValue, 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(
                        backgroundColorAnimValue, 1.0)));
        transition.getChildren().add(itemTransition);

        // Selected item image icon color change
        final FadeTransition selectedItemImageBlackFade = new FadeTransition();
        selectedItemImageBlackFade.setNode(itemAndValueToIcon.get(newSelectedItem));
        selectedItemImageBlackFade.setDuration(Duration.millis(400));
        selectedItemImageBlackFade.setFromValue(1);
        selectedItemImageBlackFade.setToValue(0);

        final FadeTransition selectedItemImageWhiteFade = new FadeTransition();
        selectedItemImageBlackFade.setNode(itemAndValueToWhiteIcon.get(newSelectedItem));
        selectedItemImageBlackFade.setDuration(Duration.millis(400));
        selectedItemImageBlackFade.setFromValue(0);
        selectedItemImageBlackFade.setToValue(1);

//        final FadeTransition selectedItemImageBlackFade = FadeTransitionBuilder
//                .create().node(itemAndValueToIcon.get(newSelectedItem))
//                .duration(Duration.millis(400)).fromValue(1.0).toValue(0.0)
//                .build();
//
//        final FadeTransition selectedItemImageWhiteFade = FadeTransitionBuilder
//                .create().node(itemAndValueToWhiteIcon.get(newSelectedItem))
//                .duration(Duration.millis(400)).fromValue(0).toValue(1.0)
//                .build();
        transition.getChildren().addAll(selectedItemImageBlackFade,
                selectedItemImageWhiteFade);

        // Unselected items fading
        final FadeTransition notSelectedTransition = new FadeTransition();
        selectedItemImageBlackFade.setNode(notSelectedItemEffect);
        selectedItemImageBlackFade.setDuration(Duration.millis(200));
        selectedItemImageBlackFade.setDelay(Duration.millis(200));
        selectedItemImageBlackFade.setFromValue(0);
        selectedItemImageBlackFade.setToValue(0.8);

//        final FadeTransition notSelectedTransition = FadeTransitionBuilder
//                .create().node(notSelectedItemEffect)
//                .duration(Duration.millis(200))
//                .delay(Duration.millis(200))
//                .fromValue(0).toValue(0.8).build();
        notSelectedItemEffect.setOpacity(0);
        notSelectedItemEffect.setVisible(true);

        transition.getChildren().add(notSelectedTransition);
        return transition;
    }

    private Label getIconActive(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.setStyle("-fx-font-family: FontAwesome;\n"
                + "-fx-font-size: 24;\n");
        label.setTextFill(baseColor);
        return label;
    }

    private Label getIconPassive(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.setStyle("-fx-font-family: FontAwesome;\n"
                + "-fx-font-size: 24;\n");
        label.setTextFill(selectionColor);
        return label;
    }

    private Label getIconBig(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.setPrefSize(60, 60);
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-font-family: FontAwesome;\n"
                + "-fx-font-size: 48;\n");
        label.setTextFill(selectionColor);
        return label;
    }

    private void handle_CloseMenu(MouseEvent e) {
//        this.setVisible(false);
        hideMenu();
    }

    public void hideMenu() {
        final FadeTransition fade = new FadeTransition();
        fade.setNode(this);
        fade.setDuration(Duration.millis(300));
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent arg0) {
                SpiffyMenu.this.setVisible(false);
            }
        });

//        final FadeTransition fade = FadeTransitionBuilder.create()
//                .node(this).fromValue(1).toValue(0)
//                .duration(Duration.millis(300))
//                .onFinished(new EventHandler<ActionEvent>() {
//
//                    @Override
//                    public void handle(final ActionEvent arg0) {
//                        SpiffyMenu.this.setVisible(false);
//                    }
//                }).build();
        final ParallelTransition transition = new ParallelTransition();
        transition.getChildren().add(fade);

//        final ParallelTransition transition = ParallelTransitionBuilder
//                .create().children(fade).build();
        transition.play();
    }

    public void toggleMenu(final double x, final double y) {
        if (this.isVisible()) {
            hideMenu();
        }
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setVisible(true);

        final FadeTransition fade = new FadeTransition();
        fade.setNode(this);
        fade.setDuration(Duration.millis(300));
        fade.setFromValue(0);
        fade.setToValue(1);

//        final FadeTransition fade = FadeTransitionBuilder.create()
//                .node(this).fromValue(0).toValue(1)
//                .duration(Duration.millis(300))
//                .build();
        final ParallelTransition transition = new ParallelTransition();
        transition.getChildren().add(fade);

//        final ParallelTransition transition = ParallelTransitionBuilder
//                .create().children(fade).build();
        transition.play();

    }
}

class SpiffyMenuCenter extends Region {

    private Region passive;
    private FadeTransition hideTransition;
    private FadeTransition showTransition;
    private Group showTransitionGroup = new Group();
    private Group hideTransitionGroup = new Group();

    public SpiffyMenuCenter() {
        getChildren().add(hideTransitionGroup);
        getChildren().add(showTransitionGroup);

        showTransition = new FadeTransition();
        showTransition.setNode(showTransitionGroup);
        showTransition.setDuration(Duration.millis(400));
        showTransition.setFromValue(0);
        showTransition.setToValue(1);

//        showTransition = FadeTransitionBuilder.create()
//                .duration(Duration.millis(400)).node(showTransitionGroup)
//                .fromValue(0.0).toValue(1.0).build();

        hideTransition = new FadeTransition();
        hideTransition.setNode(showTransitionGroup);
        hideTransition.setDuration(Duration.millis(400));
        hideTransition.setFromValue(1);
        hideTransition.setToValue(0);

//        hideTransition = FadeTransitionBuilder.create()
//                .duration(Duration.millis(400)).node(hideTransitionGroup)
//                .fromValue(1.0).toValue(0.0).build();
    }

    public void addCenterItem(final Region centerGraphic) {
        passive = centerGraphic;
        centerGraphic.setTranslateX(-centerGraphic.getPrefWidth() / 2.0);
        centerGraphic.setTranslateY(-centerGraphic.getPrefHeight() / 2.0);
        setPrefWidth(centerGraphic.getPrefWidth());
        setPrefHeight(centerGraphic.getPrefHeight());
    }

    public void displayCenter() {
        showTransitionGroup.getChildren().setAll(passive);
        showTransition.playFromStart();
    }

    public void hideCenter() {
        hideTransitionGroup.getChildren().setAll(passive);
        hideTransition.playFromStart();
    }
}

enum MenuAction {

    CLOSE_MENU,
    NEW,
    OPEN,
    SAVE,
    COPY,
    PASTE,
    ZOOM_TO_FIT,
    GROUP,
    ALIGN,
}
