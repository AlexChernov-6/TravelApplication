package com.example.travel.controllers;

import com.example.travel.services.DirectionService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FilterWindow extends AnchorPane {
    private final StackPane overlaySP;
    private Pane shadowPane;
    private VBox bodyVB;
    private long maxPriceDirection;
    private boolean isProgrammaticFromChange = false;
    private boolean isProgrammaticBeforeChange = false;

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
        ScrollPane bodySP = new ScrollPane();
        AnchorPane.setTopAnchor(bodySP, 50.0);
        AnchorPane.setLeftAnchor(bodySP, 0.0);
        AnchorPane.setRightAnchor(bodySP, 0.0);
        AnchorPane.setBottomAnchor(bodySP, 80.0);
        bodySP.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        bodySP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        bodyVB = new VBox(1);
        bodyVB.setStyle("-fx-background-color: gray;");
        bodySP.setContent(bodyVB);
        bodyVB.prefWidthProperty().bind(bodySP.widthProperty().subtract(10));

        createPriceWithNight();

        getChildren().add(bodySP);
    }

    private void createPriceWithNight() {
        VBox priceWithNightVB = new VBox(15);
        priceWithNightVB.setPrefHeight(100);
        priceWithNightVB.setStyle("-fx-background-color: white; -fx-padding: 15px;");

        Label label = new Label("Цена за ночь");
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        HBox priceRange = new HBox(10);

        RangeSlider rangeSlider = new RangeSlider();

        TextField fromPrice = new TextField();
        fromPrice.setPromptText("От");
        fromPrice.getStyleClass().add("text-field-from-or-before");
        HBox.setHgrow(fromPrice, Priority.ALWAYS);
        fromPrice.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        fromPrice.focusedProperty().addListener((ob, oldV, newV) -> {
            if (fromPrice.getText().isEmpty() && newV) {
                fromPrice.setText(String.format("%d", 0));
            }
            if ((fromPrice.getText().isEmpty() || Long.parseLong(fromPrice.getText()) == 0) && !newV) {
                fromPrice.setText("");
            } else if (Long.parseLong(fromPrice.getText()) > maxPriceDirection && !newV) {
                fromPrice.setText(String.format("%d", maxPriceDirection));
            }

            try {
                double value = Integer.parseInt(fromPrice.getText());
                // Ограничиваем значение диапазоном
                value = Math.max(rangeSlider.getMin(), Math.min(rangeSlider.getHighValue(), value));
                rangeSlider.setLowValue(value);
            } catch (NumberFormatException e) {
                // игнорируем
            }
        });

        TextField beforePrice = new TextField();
        beforePrice.setPromptText("До");
        beforePrice.getStyleClass().add("text-field-from-or-before");
        HBox.setHgrow(beforePrice, Priority.ALWAYS);
        beforePrice.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
        beforePrice.focusedProperty().addListener((ob, oldV, newV) -> {
            if (beforePrice.getText().isEmpty() && newV) {
                beforePrice.setText(String.format("%d", 0));
            }
            if ((beforePrice.getText().isEmpty() || Long.parseLong(beforePrice.getText()) == 0) && !newV) {
                beforePrice.setText("");
            } else if (Long.parseLong(beforePrice.getText()) > maxPriceDirection && !newV) {
                beforePrice.setText(String.format("%d", maxPriceDirection));
            }

            try {
                double value = Integer.parseInt(fromPrice.getText());
                // Ограничиваем значение диапазоном
                value = Math.max(rangeSlider.getMin(), Math.min(rangeSlider.getHighValue(), value));
                rangeSlider.setHighValue(value);
            } catch (NumberFormatException e) {
                // игнорируем
            }
        });

        priceRange.getChildren().addAll(fromPrice, beforePrice);

        priceWithNightVB.getChildren().addAll(label, priceRange);

        rangeSlider.setOrientation(Orientation.HORIZONTAL);
        rangeSlider.setShowTickMarks(false);
        rangeSlider.setBlockIncrement(1500);
        rangeSlider.setMin(0);
        rangeSlider.setMax(maxPriceDirection);

        fromPrice.setText(String.format("%d", Math.round(rangeSlider.getLowValue())));
        beforePrice.setText(String.format("%d", Math.round(rangeSlider.getHighValue())));

        rangeSlider.lowValueProperty().addListener((obs, oldVal, newVal) -> {
            if (!fromPrice.isFocused())
                fromPrice.setText(String.format("%d", newVal.intValue()));
        });

        rangeSlider.highValueProperty().addListener((obs, oldVal, newVal) -> {
            if (!beforePrice.isFocused())
                beforePrice.setText(String.format("%d", newVal.intValue()));
        });

        priceWithNightVB.getChildren().add(rangeSlider);

        bodyVB.getChildren().add(priceWithNightVB);

        for(int i = 1; i <= 100; i++) {
            Label label1 = new Label("fffffffffffffffffffff");
            bodyVB.getChildren().add(label1);
        }
    }

    private void createLow() {
        Button resetBtn = new Button("Сбросить");
        AnchorPane.setLeftAnchor(resetBtn, 15.0);
        AnchorPane.setBottomAnchor(resetBtn, 15.0);
        resetBtn.setPrefHeight(50);
        resetBtn.setPrefWidth(150);
        resetBtn.getStyleClass().add("reset-button");

        Button showResultBtn = new Button("Показать");
        AnchorPane.setRightAnchor(showResultBtn, 15.0);
        AnchorPane.setBottomAnchor(showResultBtn, 15.0);
        showResultBtn.setPrefHeight(50);
        showResultBtn.setPrefWidth(150);
        showResultBtn.getStyleClass().add("show-result-button");

        getChildren().addAll(resetBtn, showResultBtn);
    }
}
