package com.example.travel.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Objects;

public class CustomCheckButton extends Button {

    private boolean isSelected = false;
    private Label buttonText;
    private HBox rootHB;

    private EventHandler<ActionEvent> secondEvent;

    public CustomCheckButton(String text, List<String> assemblyList) {
        getStyleClass().add("custom-button");
        rootHB = new HBox(10);
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

        addEventHandler(ActionEvent.ACTION, e -> {
            if(assemblyList != null) {
                if (assemblyList.contains(text))
                    assemblyList.remove(text);
                else
                    assemblyList.add(text);
            }

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

        buttonText = new Label();
        buttonText.getStyleClass().add("sort-button-text");
        buttonText.setText(text);

        rootHB.getChildren().addAll(selectedButton, buttonText);

        setGraphic(rootHB);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void updateText(Node node) {
        rootHB.getChildren().remove(buttonText);
        rootHB.getChildren().add(node);
    }

    public void addSecondAction(EventHandler<ActionEvent> event) {
        if(secondEvent != null)
            removeEventHandler(ActionEvent.ACTION, event);

        addEventHandler(ActionEvent.ACTION, event);
    }
}
