package com.example.travel.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Objects;

public class FilterWindow extends AnchorPane {
    private final StackPane overlaySP;
    private Pane shadowPane;
    private VBox bodyVB;

    public FilterWindow(StackPane overlaySP) {
        this.overlaySP = overlaySP;

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

        bodyVB = new VBox();
        bodyVB.setPadding(new Insets(0, 15, 0, 15));
        bodySP.setContent(bodyVB);

        createPriceWithNight();

        getChildren().add(bodySP);
    }

    private void createPriceWithNight() {
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(),
                new ColumnConstraints()
        );

        RowConstraints first = new RowConstraints();
        first.setPercentHeight(20);

        RowConstraints second = new RowConstraints();
        second.setPercentHeight(45);

        RowConstraints third = new RowConstraints();
        third.setPercentHeight(35);

        gridPane.getRowConstraints().addAll(first, second, third);

        gridPane.setMaxHeight(Region.USE_PREF_SIZE);

        Label label = new Label("Цена за ночь");
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(label, 0);
        GridPane.setRowIndex(label, 0);


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
