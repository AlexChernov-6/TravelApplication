package com.example.travel.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Objects;

public class CustomCheckButton extends Button {

    private boolean isSelected = false;

    public CustomCheckButton(String text, List<String> assemblyList) {
        getStyleClass().add("custom-button");
        HBox rootHB = new HBox(10);
        rootHB.setAlignment(Pos.CENTER_LEFT);

        Button selectedButton = new Button();
        selectedButton.getStyleClass().add("button-star-selected");
        selectedButton.setMinWidth(22);
        selectedButton.setPrefWidth(22);
        selectedButton.setMaxWidth(22);
        selectedButton.setMinHeight(22);
        selectedButton.setPrefHeight(22);
        selectedButton.setMaxHeight(22);


        ImageView backgroundSelectedBtn = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/check-mark.png"))));
        backgroundSelectedBtn.setFitHeight(22);
        backgroundSelectedBtn.setFitWidth(22);

        setOnMouseEntered(e -> {
            if(!isSelected)
                selectedButton.setStyle("-fx-border-color: #b40acf;");
        });

        setOnMouseExited(e -> {
            if(!isSelected)
                selectedButton.setStyle("-fx-border-color: rgba(200,200,200);");
        });

        setOnAction(e -> {
            if(assemblyList.contains(text))
                assemblyList.remove(text);
            else
                assemblyList.add(text);

            isSelected = !isSelected;

            if(isSelected) {
                selectedButton.getStyleClass().add("button-star-selected-select");
                selectedButton.getStyleClass().remove("button-star-selected");
                selectedButton.setGraphic(backgroundSelectedBtn);
            } else {
                selectedButton.getStyleClass().add("button-star-selected");
                selectedButton.getStyleClass().remove("button-star-selected-select");
                selectedButton.setGraphic(null);
            }
        });

        Label buttonText = new Label();
        buttonText.getStyleClass().add("sort-button-text");
        buttonText.setText(text);

        rootHB.getChildren().addAll(selectedButton, buttonText);

        setGraphic(rootHB);
    }

    public boolean isSelected() {
        return isSelected;
    }
}
