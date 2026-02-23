package com.example.travel.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CustomButton extends Button {
    public CustomButton(Image image, String text) {
        getStyleClass().add("custom-button");

        VBox rootVB = new VBox(2); // небольшой отступ между иконкой и текстом
        rootVB.setAlignment(Pos.CENTER);

        ImageView iconImage = new ImageView(image);
        iconImage.setPreserveRatio(true);
        iconImage.setFitWidth(25);
        iconImage.setFitHeight(25);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 10; -fx-text-fill: white;"); // мелкий шрифт для кнопки

        rootVB.getChildren().addAll(iconImage, label);
        setGraphic(rootVB);
    }
}