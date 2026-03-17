package com.example.travel.controllers;

import com.example.travel.models.*;
import com.example.travel.services.*;
import com.example.travel.util.HelpFullClass;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import org.controlsfx.control.RangeSlider;

import java.util.*;
import java.util.function.Predicate;

import static com.example.travel.controllers.PopularDestinationsController.updatePredicateFilteredHotels;

public class FilterWindow extends AnchorPane {
    private final StackPane overlaySP;
    private Pane shadowPane;
    private VBox bodyVB;
    private long maxPriceDirection;
    private final HashMap<RefundPolicy, Boolean> mapCancellation = new HashMap<>();
    private final HashMap<Object, Button> mapModelButtons = new HashMap<>();

    private final HashMap<PaymentMethod, Boolean> mapPaymentMethods = new HashMap<>();
    private ScrollPane bodySP;
    protected ObjectProperty<Predicate<Hotel>> filterPredicate = new SimpleObjectProperty<>();

    private long fromPrice, beforePrice;

    private final List<Integer> countStartList = new ArrayList<>();

    private CustomRadioParent parent;

    private final List<String> hotelFeatures = new ArrayList<>();
    private final List<String> roomFeatures = new ArrayList<>();

    private TextField fromPriceTF, beforePriceTF;

    private VBox countStarsVB;

    private CustomRadioButton anyRating;

    private List<CustomCheckButton> allCheckButtons = new ArrayList<>();

    public FilterWindow(StackPane overlaySP) {
        this.overlaySP = overlaySP;

        this.maxPriceDirection = Math.round(new DirectionService()
                .getMaxRoomPriceByDirectionId(PopularDestinationsController.oldPressedDirection.getIdDirection()));

        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        overlaySP.getChildren().add(shadowPane);

        setSizeWindow();

        setStyle("-fx-background-color: white;");
        getStyleClass().add("popup");

        setPrefHeight(300);
        setPrefWidth(500);

        createHeader();

        createBody();

        createLow();

        overlaySP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!isVisible()) return;

            Point2D pointInWindow = screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && contains(pointInWindow))
                return;
            else
                hide();
        });

        overlaySP.getChildren().add(this);
    }

    public void show() {
        setSizeWindow();
        shadowPane.setVisible(true);
        setVisible(true);
        this.maxPriceDirection = Math.round(new DirectionService()
                .getMaxRoomPriceByDirectionId(PopularDestinationsController.oldPressedDirection.getIdDirection()));
    }

    public void hide() {
        shadowPane.setVisible(false);
        setVisible(false);
    }

    public void setSizeWindow() {
        setMaxWidth(overlaySP.getWidth() / 2.2);
        setMaxHeight(overlaySP.getHeight() - 50);
    }

    private void createHeader() {
        Label filtersLB = new Label("Фильтры");
        filtersLB.getStyleClass().add("filters-label");
        filtersLB.setPrefHeight(40);
        AnchorPane.setTopAnchor(filtersLB, 5.0);
        AnchorPane.setLeftAnchor(filtersLB, 20.0);

        Button closeBtn = new Button();
        closeBtn.setPrefHeight(30);
        closeBtn.setPrefWidth(30);
        AnchorPane.setTopAnchor(closeBtn, 10.0);
        AnchorPane.setRightAnchor(closeBtn, 15.0);
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(event -> {
            hide();
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeBtn.setGraphic(closeImg);

        getChildren().addAll(filtersLB, closeBtn);
    }

    private void createBody() {
        bodySP = new ScrollPane();
        AnchorPane.setTopAnchor(bodySP, 50.0);
        AnchorPane.setLeftAnchor(bodySP, 0.0);
        AnchorPane.setRightAnchor(bodySP, 0.0);
        AnchorPane.setBottomAnchor(bodySP, 80.0);
        bodySP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bodySP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        bodySP.getStyleClass().add("scroll-pane");
        new HelpFullClass().scrollPaneAnimation(bodySP);

        bodyVB = new VBox();
        bodySP.setContent(bodyVB);
        bodyVB.prefWidthProperty().bind(bodySP.widthProperty().subtract(10));

        createPriceWithNight();

        createCountStart();

        createRating();

        createFilterButton("Условия отмены", mapCancellation, new RefundPolicyService().getAllRow());

        createFilterButton("Оплата и бронирование", mapPaymentMethods, new PaymentMethodService().getAllRow());

        createFeature("Удобства и услуги", new HotelFeatureService().getAllRow(), hotelFeatures);

        createFeature("Удобства в номерах", new RoomFeatureService().getAllRow(), roomFeatures);

        getChildren().add(bodySP);
    }

    private void createPriceWithNight() {
        VBox priceWithNightVB = new VBox(15);
        priceWithNightVB.setStyle("-fx-background-color: white; -fx-padding: 5px 15px;");

        Label label = new Label("Цена за ночь");
        label.getStyleClass().add("hint-label");

        HBox priceRange = new HBox(10);

        RangeSlider rangeSlider = new RangeSlider();

        fromPriceTF = new TextField();
        fromPriceTF.setPromptText("От");
        fromPriceTF.getStyleClass().add("text-field-from-or-before");
        HBox.setHgrow(fromPriceTF, Priority.ALWAYS);
        fromPriceTF.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            String oldText = change.getControlText();
            if (newText.matches("\\d*")) {
                if (newText.isEmpty()) {
                    fromPrice = 0;
                    if(fromPriceTF.isFocused()) {
                        change.setText("0");
                        change.setCaretPosition(1);
                        change.setAnchor(1);
                    }
                }
                else {
                    if(oldText.equals("0") && newText.startsWith("0") && newText.length() > 1) {
                        String digit = newText.substring(1);
                        change.setText(digit);
                        change.setRange(0, oldText.length());
                        change.setCaretPosition(digit.length());
                        change.setAnchor(digit.length());
                        fromPrice = Long.parseLong(digit);
                        return change;
                    }
                    fromPrice = Long.parseLong(newText);
                    return change;
                }
                return change;
            }
            return null;
        }));
        fromPriceTF.focusedProperty().addListener((ob, oldV, newV) -> {
            if (fromPriceTF.getText().isEmpty() && newV)
                fromPriceTF.setText(String.format("%d", 0));

            if ((fromPriceTF.getText().isEmpty() || Long.parseLong(fromPriceTF.getText()) == 0) && !newV) {
                fromPriceTF.setText("");
            } else if (Long.parseLong(fromPriceTF.getText()) > maxPriceDirection && !newV) {
                fromPriceTF.setText(String.format("%d", maxPriceDirection));
            }

            try {
                double value = Integer.parseInt(fromPriceTF.getText());
                // Ограничиваем значение диапазоном
                value = Math.max(rangeSlider.getMin(), value);
                rangeSlider.setLowValue(value);
            } catch (NumberFormatException e) {
                // игнорируем
            }
        });

        beforePriceTF = new TextField();
        beforePriceTF.setPromptText("До");
        beforePriceTF.getStyleClass().add("text-field-from-or-before");
        HBox.setHgrow(beforePriceTF, Priority.ALWAYS);
        beforePriceTF.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            String oldText = change.getControlText();
            if (newText.matches("\\d*")) {
                if (newText.isEmpty()) {
                    beforePrice = maxPriceDirection;
                    if(beforePriceTF.isFocused()) {
                        change.setText("0");
                        change.setCaretPosition(1);
                        change.setAnchor(1);
                    }
                }
                else {
                    if(oldText.equals("0") && newText.startsWith("0") && newText.length() > 1) {
                        String digit = newText.substring(1);
                        change.setText(digit);
                        change.setRange(0, oldText.length());
                        change.setCaretPosition(digit.length());
                        change.setAnchor(digit.length());
                        beforePrice = Long.parseLong(digit);
                        return change;
                    }
                    beforePrice = Long.parseLong(newText);
                    return change;
                }
                return change;
            }
            return null;
        }));
        beforePriceTF.focusedProperty().addListener((ob, oldV, newV) -> {
            if (beforePriceTF.getText().isEmpty() && newV) {
                beforePriceTF.setText(String.format("%d", maxPriceDirection));
            }
            if ((beforePriceTF.getText().isEmpty() || Long.parseLong(beforePriceTF.getText()) == maxPriceDirection) && !newV) {
                beforePriceTF.setText("");
            } else if (Long.parseLong(beforePriceTF.getText()) > maxPriceDirection && !newV) {
                beforePriceTF.setText(String.format("%d", maxPriceDirection));
            }

            try {
                double value = Long.parseLong(beforePriceTF.getText());
                // Ограничиваем значение диапазоном
                value = Math.min(rangeSlider.getMax(), value);
                rangeSlider.setHighValue(value);
            } catch (NumberFormatException e) {
                // игнорируем
            }
        });

        priceRange.getChildren().addAll(fromPriceTF, beforePriceTF);

        priceWithNightVB.getChildren().addAll(label, priceRange);

        rangeSlider.setOrientation(Orientation.HORIZONTAL);
        rangeSlider.setShowTickMarks(false);
        rangeSlider.setMin(0);
        rangeSlider.setMax(maxPriceDirection);
        rangeSlider.setHighValue(maxPriceDirection);
        rangeSlider.getStyleClass().add("range-slider");

        rangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> {
            if (!fromPriceTF.isFocused()) {
                fromPriceTF.setText(String.format("%d", newVal.intValue()));
                if (newVal.doubleValue() == rangeSlider.getMin())
                    fromPriceTF.setText("");
            }
        });

        rangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> {
            if (!beforePriceTF.isFocused()) {
                beforePriceTF.setText(String.format("%d", newVal.intValue()));
                if (newVal.doubleValue() == rangeSlider.getMax())
                    beforePriceTF.setText("");
            }
        });

        priceWithNightVB.getChildren().add(rangeSlider);

        bodyVB.getChildren().add(priceWithNightVB);

        createLine(priceWithNightVB);
    }

    private void createCountStart() {
        countStarsVB = new VBox(15);
        countStarsVB.setStyle("-fx-background-color: white; -fx-padding: 5px 15px;");

        Label label = new Label("Количество звёзд");
        label.getStyleClass().add("hint-label");

        countStarsVB.getChildren().add(label);

        for (int i = 5; i >= 0; i--) {
            CountStarButton countStarButton = new CountStarButton(i, this);
            countStarsVB.getChildren().add(countStarButton);
        }

        bodyVB.getChildren().add(countStarsVB);

        createLine(countStarsVB);
    }

    private void createRating() {
        VBox ratingVB = new VBox(15);
        ratingVB.setStyle("-fx-background-color: white; -fx-padding: 5px 15px;");

        Label label = new Label("Рейтинг отелей");
        label.getStyleClass().add("hint-label");

        ratingVB.getChildren().add(label);

        parent = new CustomRadioParent();

        CustomRadioButton aboveFore = new CustomRadioButton(parent, 4, false);
        aboveFore.addStyleButtonText("above-fore");

        CustomRadioButton aboveThree = new CustomRadioButton(parent, 3, false);
        aboveThree.addStyleButtonText("above-three");

        CustomRadioButton aboveTwo = new CustomRadioButton(parent, 2, false);
        aboveTwo.addStyleButtonText("above-two");

        anyRating = new CustomRadioButton(parent, 0, true);
        anyRating.addStyleButtonText("any-rating");

        ratingVB.getChildren().addAll(aboveFore, aboveThree, aboveTwo, anyRating);

        bodyVB.getChildren().add(ratingVB);

        createLine(ratingVB);
    }

    private <T> void createFilterButton(String hintTxt, HashMap<T, Boolean> map, List<T> list) {
        VBox rootVB = new VBox(15);
        rootVB.setStyle("-fx-background-color: white; -fx-padding: 5px 15px;");

        Label label = new Label(hintTxt);
        label.getStyleClass().add("hint-label");

        rootVB.getChildren().add(label);

        HBox horizontalBox = new HBox(10);

        for (T entity : list) {
            Button button = new Button(entity.toString());
            mapModelButtons.put(entity, button);
            button.getStyleClass().add("reset-button");
            button.setStyle("-fx-font-size: 15px;");
            TilePane.setAlignment(button, Pos.TOP_LEFT);
            map.put(entity, false);
            button.setOnAction(e -> {
                boolean value = map.get(entity);
                map.replace(entity, value, !value);
                if (map.get(entity)) {
                    button.getStyleClass().add("show-result-button");
                    button.getStyleClass().remove("reset-button");
                } else {
                    button.getStyleClass().add("reset-button");
                    button.getStyleClass().remove("show-result-button");
                }
            });
            horizontalBox.getChildren().add(button);
        }

        rootVB.getChildren().add(horizontalBox);

        bodyVB.getChildren().add(rootVB);

        createLine(rootVB);
    }

    private <T> void createFeature(String hintTxt, List<T> list, List<String> assemblyList) {
        VBox rootVB = new VBox(15);
        rootVB.setStyle("-fx-background-color: white; -fx-padding: 5px 15px;");

        Label label = new Label(hintTxt);
        label.getStyleClass().add("hint-label");

        rootVB.getChildren().add(label);

        for (int i = 0; i < list.size(); i++) {
            CustomCheckButton button = new CustomCheckButton(list.get(i).toString(), assemblyList);
            allCheckButtons.add(button);
            rootVB.getChildren().add(button);
            if (i >= 5) {
                button.setManaged(false);
                button.setVisible(false);
            }
        }

        if (list.size() > 5) {
            Button showMany = new Button("Показать больше");
            showMany.prefWidthProperty().bind(rootVB.widthProperty());
            showMany.getStyleClass().add("show-many-button");
            showMany.setOnAction(e -> {
                if (showMany.getText().equals("Показать больше")) {
                    rootVB.getChildren().remove(showMany);
                    rootVB.getChildren().forEach(node -> {
                        node.setManaged(true);
                        node.setVisible(true);
                    });
                    rootVB.getChildren().add(showMany);
                    showMany.setText("Показать меньше");
                } else {
                    rootVB.getChildren().remove(showMany);
                    for (int i = rootVB.getChildren().size() - 1; i > 5; i--) {
                        Node node = rootVB.getChildren().get(i);
                        node.setManaged(false);
                        node.setVisible(false);
                    }
                    rootVB.getChildren().add(showMany);
                    showMany.setText("Показать больше");
                    ensureVisible(bodySP, label);
                }
            });
            rootVB.getChildren().add(showMany);
        }

        bodyVB.getChildren().add(rootVB);

        createLine(rootVB);
    }

    private void createLow() {
        Button resetBtn = new Button("Сбросить");
        AnchorPane.setLeftAnchor(resetBtn, 15.0);
        AnchorPane.setBottomAnchor(resetBtn, 15.0);
        resetBtn.setPrefHeight(50);
        resetBtn.setPrefWidth(150);
        resetBtn.getStyleClass().add("reset-button");
        resetBtn.setOnAction(e -> {
            resetAllState();
        });

        Button showResultBtn = new Button("Показать");
        AnchorPane.setRightAnchor(showResultBtn, 15.0);
        AnchorPane.setBottomAnchor(showResultBtn, 15.0);
        showResultBtn.setPrefHeight(50);
        showResultBtn.setPrefWidth(150);
        showResultBtn.getStyleClass().add("show-result-button");
        showResultBtn.setOnAction(e -> {
            filteredData();
            hide();
        });

        getChildren().addAll(resetBtn, showResultBtn);
    }

    private static void ensureVisible(ScrollPane scrollPane, Node node) {
        Bounds viewport = scrollPane.getViewportBounds();
        double contentHeight = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal()).getHeight();
        double nodeMinY = node.localToScene(node.getBoundsInLocal()).getMinY();
        double nodeMaxY = node.localToScene(node.getBoundsInLocal()).getMaxY();

        double vValueDelta = 0;
        double vValueCurrent = scrollPane.getVvalue();

        if (nodeMaxY < 0) {
            // currently located above (remember, top left is (0,0))
            vValueDelta = (nodeMinY - viewport.getHeight()) / contentHeight;
        } else if (nodeMinY > viewport.getHeight()) {
            // currently located below
            vValueDelta = (nodeMinY + viewport.getHeight()) / contentHeight;
        }
        scrollPane.setVvalue(vValueCurrent + vValueDelta);
    }

    private void createLine(VBox parent) {
        Line line = new javafx.scene.shape.Line();
        line.setStroke(Color.rgb(170, 170, 170, 0.5));
        line.setStrokeLineJoin(StrokeLineJoin.ROUND);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(3);
        line.endXProperty().bind(parent.widthProperty().subtract(40));
        parent.getChildren().add(line);
    }

    private void filteredData() {
        FilteredList<Hotel> filteredList = PopularDestinationsController.getFilteredHotels();
        filterPredicate.bind(Bindings.createObjectBinding(() -> hotel -> checkPrice(hotel) && checkStar(hotel)
                && checkRat(hotel) && checkCancellation(hotel) && checkPaymentMethod(hotel)
                && checkHotelFeature(hotel) && checkRoomFeature(hotel)));

        PopularDestinationsController.filteres.put("FilterWindow", filterPredicate.get());
        updatePredicateFilteredHotels();
    }

    private boolean checkPrice(Hotel hotel) {
        RoomService service = new RoomService();
        double minPrice = service.getMinRoomPriceByHotelId(hotel.getIdHotel());
        return minPrice >= fromPrice && minPrice <= beforePrice;
    }

    private boolean checkStar(Hotel hotel) {
        if(countStartList.isEmpty())
            return true;
        else return countStartList.contains(Short.toUnsignedInt(hotel.getCountStars()));
    }

    private boolean checkRat(Hotel hotel) {
        double selectedRat = 0.0;
        for(CustomRadioButton button : parent.getCustomRadioButtonList()) {
            if (button.getSelected())
                selectedRat = button.getRat();
        }
        return hotel.getHotelRating() >= selectedRat;
    }

    private boolean checkCancellation(Hotel hotel) {
        if (mapCancellation.values().stream().filter(b -> b).toList().isEmpty())
            return true;

        RoomService roomService = new RoomService();
        List<Room> res = roomService.getAllRowByHotelId(hotel.getIdHotel()).stream()
                .filter(r -> mapCancellation.get(r.getRefundPolicy()) != null
                        && mapCancellation.get(r.getRefundPolicy())).toList();
        return !res.isEmpty();
    }

    private boolean checkPaymentMethod(Hotel hotel) {
        if (mapPaymentMethods.values().stream().filter(b -> b).toList().isEmpty())
            return true;

        RoomService roomService = new RoomService();
        List<Room> res = roomService.getAllRowByHotelId(hotel.getIdHotel()).stream()
                .filter(r -> mapPaymentMethods.get(r.getPaymentMethod()) != null
                        && mapPaymentMethods.get(r.getPaymentMethod())).toList();
        return !res.isEmpty();
    }

    private boolean checkHotelFeature(Hotel hotel) {
        if(hotelFeatures.isEmpty())
            return true;

        HotelFeatureRelationService service = new HotelFeatureRelationService();
        List<HotelFeature> allHotelFeatureByHotelId = service.getAllHotelFeatureByHotelId(hotel.getIdHotel());

        List<HotelFeature> result = allHotelFeatureByHotelId.stream()
                .filter(hotelFeature -> hotelFeatures.contains(hotelFeature.getFeatureName())).toList();

        return result.size() == hotelFeatures.size();
    }

    private boolean checkRoomFeature(Hotel hotel) {
        if(roomFeatures.isEmpty())
            return  true;

        RoomFeatureRelationService service = new RoomFeatureRelationService();
        List<RoomFeature> allRoomFeatureByHotelId = service.getAllRoomFeatureByHotelId(hotel.getIdHotel());

        List<RoomFeature> result = allRoomFeatureByHotelId.stream()
                .filter(roomFeature -> roomFeatures.contains(roomFeature.getFeatureName())).toList();

        return result.size() == roomFeatures.size();
    }

    public List<Integer> getCountStartList() {
        return countStartList;
    }

    private void resetAllState() {
        fromPriceTF.setText(String.format("%d", 0));
        beforePriceTF.setText(String.format("%d", maxPriceDirection));

        for(Node node : countStarsVB.getChildren()) {
            if(node instanceof CountStarButton)
                ((CountStarButton) node).removeSelected();
        }

        anyRating.fire();

        for(Map.Entry<RefundPolicy, Boolean> entry : mapCancellation.entrySet()) {
            if(entry.getValue())
                mapModelButtons.get(entry.getKey()).fire();
        }

        for(Map.Entry<PaymentMethod, Boolean> entry : mapPaymentMethods.entrySet()) {
            if(entry.getValue())
                mapModelButtons.get(entry.getKey()).fire();
        }

        for (CustomCheckButton btn : allCheckButtons) {
            if (btn.isSelected()) {
                btn.fire();
            }
        }

        filteredData();
        hide();
    }
}
