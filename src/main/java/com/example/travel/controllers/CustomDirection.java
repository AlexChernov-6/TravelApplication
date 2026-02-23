package com.example.travel.controllers;

import com.example.travel.models.Direction;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class CustomDirection extends Button {
    private final Direction direction;
    public CustomDirection(Direction direction) {
        this.direction = direction;

        setStyle("-fx-cursor: hand;");
        getStyleClass().add("custom-button");

        VBox rootVB = new VBox();

        ImageView imageView = new ImageView(direction.getPhotoCityImage());
        imageView.setFitWidth(270);
        imageView.setFitHeight(270);

        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageView.setClip(clip);

        Label label = new Label(direction.getCity());
        label.getStyleClass().add("custom-directions-label");
        VBox.setMargin(label, new Insets(5, 0, 0, 0));

        rootVB.getChildren().addAll(imageView, label);

        setGraphic(rootVB);
    }

    public String getCityName() {
        return direction.getCity();
    }
}
