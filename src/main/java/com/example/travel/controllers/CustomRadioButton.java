package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Comparator;

public class CustomRadioButton extends Button {
    private StackPane circleStackPane;
    private Circle ring;
    private Circle circle;
    private Boolean isSelected;
    private Label buttonText;
    private double rat;

    public CustomRadioButton(CustomRadioParent parent, double rat, boolean isStartValue) {
        this.isSelected = isStartValue;
        this.rat = rat;
        parent.getCustomRadioButtonList().add(this);
        getStyleClass().add("custom-button");

        HBox rootHB = new HBox(10);
        rootHB.setAlignment(Pos.CENTER);

        circleStackPane = new StackPane();
        circleStackPane.setPrefHeight(19);
        circleStackPane.setPrefWidth(19);
        circleStackPane.setPadding(new Insets(0));

        ring = new Circle(8);
        ring.setFill(null);
        ring.setStroke(Color.rgb(150, 150, 150));
        ring.setStrokeWidth(2);

        circleStackPane.getChildren().add(ring);

        buttonText = new Label();
        buttonText.getStyleClass().add("sort-button-text");
        if (rat > 0)
            buttonText.setText("Выше " + String.format("%.1f", rat));
        else
            buttonText.setText("Любой рейтинг");

        rootHB.getChildren().addAll(circleStackPane, buttonText);

        setGraphic(rootHB);

        setOnMouseEntered( e -> {
            if(!isSelected)
                ring.setStroke(Color.rgb(180, 10, 207));
        });

        setOnMouseExited(e -> {
            if(!isSelected)
                ring.setStroke(Color.rgb(150, 150, 150));
        });

        setOnAction(e -> {
            for(CustomRadioButton node : parent.getCustomRadioButtonList()) {
                node.removeFocused();
                node.setSelected(false);
            }
            addFocused();
            isSelected = true;
        });

        if (isStartValue)
            addFocused();
    }

    protected void removeFocused() {
        ring.setStroke(Color.rgb(150, 150, 150));
        if(circle != null)
            circleStackPane.getChildren().remove(circle);
    }

    protected void addFocused() {
        if (circle == null) {
            circle = new Circle(5);
            circle.setFill(Color.rgb(180, 10, 207));
            circle.setStrokeWidth(0);
        }

        circleStackPane.getChildren().add(circle);

        ring.setStroke(Color.rgb(180, 10, 207));
    }

    public void addStyleButtonText(String styleClass) {
        buttonText.getStyleClass().add(styleClass);
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public double getRat() {
        return rat;
    }
}
