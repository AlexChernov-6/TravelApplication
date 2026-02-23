package com.example.travel.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class GuestCounter extends HBox {
    private final Label countLB;
    private final boolean isAdults;
    private final NumberOfGuestsController controller;

    public GuestCounter(boolean isAdults, NumberOfGuestsController controller) {
        this.isAdults = isAdults;
        this.controller = controller;

        getStyleClass().add("guest-counter");
        setPrefHeight(40);
        setMaxHeight(40);
        setAlignment(Pos.CENTER);

        int initialCount = isAdults ? NumberOfGuestsController.adultsCount : NumberOfGuestsController.childrenCount;
        countLB = new Label(String.valueOf(initialCount));
        countLB.setAlignment(Pos.CENTER);
        countLB.setPrefWidth(40); // фиксированная ширина, чтобы кнопки не скакали
        countLB.setStyle("-fx-font-size: 16px");

        Button minusBtn = createButton(true);
        Button plusBtn = createButton(false);

        getChildren().addAll(minusBtn, countLB, plusBtn);
    }

    private Button createButton(boolean isMinus) {
        Button button = new Button();
        button.getStyleClass().add("minus-or-plus-button");

        String imageName = isMinus ? "/images/minus.png" : "/images/plus.png";
        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageName))));
        imageView.setFitHeight(25);
        imageView.setFitWidth(25);
        imageView.setPreserveRatio(true);
        button.setGraphic(imageView);

        button.setOnAction(e -> {
            int current = Integer.parseInt(countLB.getText());
            int newValue = current;

            if (isMinus) {
                newValue = current - 1;
                // Минимальное значение: для взрослых 1, для детей 0
                if (isAdults) {
                    if (newValue < 1) newValue = 1;
                } else {
                    if (newValue < 0) newValue = 0;
                }
            } else {
                newValue = current + 1;
                // Максимальное значение: взрослые до 10, дети до 5
                int max = isAdults ? 10 : 5;
                if (newValue > max) newValue = max;
            }

            if (newValue != current) {
                // Обновляем лейбл
                countLB.setText(String.valueOf(newValue));
                // Обновляем статические переменные
                if (isAdults) {
                    NumberOfGuestsController.adultsCount = newValue;
                } else {
                    NumberOfGuestsController.childrenCount = newValue;
                }
                // Сообщаем контроллеру, что нужно обновить главную кнопку
                controller.onCountChanged();
            }
        });

        return button;
    }

    // Метод для принудительной установки значения (при показе панели)
    public void setCount(int count) {
        countLB.setText(String.valueOf(count));
    }
}