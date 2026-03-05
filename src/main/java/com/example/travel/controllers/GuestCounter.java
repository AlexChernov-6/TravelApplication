package com.example.travel.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class GuestCounter extends VBox {
    private int count;
    private final boolean isAdult;
    private final NumberOfGuestsController controller;
    private Label countLabel;

    public GuestCounter(boolean isAdult, NumberOfGuestsController controller) {
        this.isAdult = isAdult;
        this.controller = controller;
        this.count = isAdult ? 2 : 0; // начальные значения

        setAlignment(Pos.CENTER);
        setSpacing(5);
        setPadding(new Insets(5));
        getStyleClass().add("guest-counter");
        setMaxHeight(Region.USE_PREF_SIZE);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.setMaxWidth(Region.USE_PREF_SIZE);

        Button minusBtn = createButton("/images/minus.png");
        Button plusBtn = createButton("/images/plus.png");
        countLabel = new Label(String.valueOf(count));
        countLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        minusBtn.setOnAction(e -> changeCount(-1));
        plusBtn.setOnAction(e -> changeCount(1));

        controls.getChildren().addAll(minusBtn, countLabel, plusBtn);

        getChildren().add(controls);
    }

    private Button createButton(String imagePath) {
        Button btn = new Button();
        btn.getStyleClass().add("minus-or-plus-button");
        ImageView img = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(imagePath))));
        img.setFitHeight(15);
        img.setFitWidth(15);
        btn.setGraphic(img);
        return btn;
    }

    private void changeCount(int delta) {
        int newCount = count + delta;
        if(isAdult) {
            if (newCount >= 1 && newCount <= 10) {
                count = newCount;
                countLabel.setText(String.valueOf(count));
                if(newCount == 10)
                    countLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                else
                    countLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                if (controller != null)
                    controller.onCountChanged(count, controller.childrenCount);
            }
        } else {
            if (newCount >= 0 && newCount <= 5) {
                count = newCount;
                countLabel.setText(String.valueOf(count));
                if (controller != null)
                    controller.onCountChanged(controller.adultsCount, count);
            }
        }
    }

    public void setCount(int count) {
        this.count = count;
        countLabel.setText(String.valueOf(count));
    }
}