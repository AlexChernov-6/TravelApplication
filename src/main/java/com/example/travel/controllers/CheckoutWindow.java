package com.example.travel.controllers;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

import static com.example.travel.controllers.PopularDestinationsController.*;

public class CheckoutWindow extends VBox {
    public CheckoutWindow() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        setStyle("-fx-background-color: rgba(230, 230, 230);");
        setPadding(new Insets(15, 20, 15, 20));
        setSpacing(15);

        overSP.getChildren().add(this);

        HBox buttonHB = new HBox(10);
        VBox.setMargin(buttonHB, new Insets(10, 0, 0, 10));
        buttonHB.setPadding(new Insets(5, 10, 5, 10));
        buttonHB.setAlignment(Pos.CENTER_LEFT);
        buttonHB.getStyleClass().add("set-hand-cursor");
        buttonHB.setMaxWidth(USE_PREF_SIZE);

        ImageView btnIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/left-arrow.png"))));
        btnIV.setFitHeight(20);
        btnIV.setFitWidth(20 / 1.5);
        btnIV.setPreserveRatio(true);

        Label btnText = new Label("Закрыть");
        btnText.setStyle("-fx-font-size: 14px; -fx-font-weight : bold;");

        buttonHB.getChildren().addAll(btnIV, btnText);

        buttonHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                overSP.getChildren().remove(this);
            }
        });

        getChildren().add(buttonHB);


    }
}
