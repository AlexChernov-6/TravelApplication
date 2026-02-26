package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.services.RoomService;
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

public class CustomSelectedBtn extends Button {
    private StackPane circleStackPane;
    private Circle ring;
    private Circle circle;
    private final RoomService roomService = new RoomService();

    public CustomSelectedBtn(PopularDestinationsController.SortedContext context, SorterWindow sorterWindow) {
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

        Label buttonText = new Label();
        buttonText.getStyleClass().add("sort-button-text");
        if(context == PopularDestinationsController.SortedContext.BY_DEFAULT) {
            buttonText.setText("По умолчанию");
            addFocused();
        }
        else if (context == PopularDestinationsController.SortedContext.MORE_EXPENSIVE)
            buttonText.setText("Дороже");
        else if (context == PopularDestinationsController.SortedContext.CHEAPER)
            buttonText.setText("Дешевле");

        rootHB.getChildren().addAll(circleStackPane, buttonText);

        setGraphic(rootHB);

        setOnMouseEntered( e -> {
            if(context != PopularDestinationsController.getSortedContext())
                ring.setStroke(Color.rgb(180, 10, 207));
        });

        setOnMouseExited(e -> {
            if(context != PopularDestinationsController.getSortedContext())
                ring.setStroke(Color.rgb(150, 150, 150));
        });

        setOnAction(e -> {
            PopularDestinationsController.setSortedContext(context);
            for(Node node : sorterWindow.getChildren()) {
                if (node instanceof CustomSelectedBtn)
                    ((CustomSelectedBtn) node).removeFocused();
            }

            addFocused();

            if (context == PopularDestinationsController.SortedContext.BY_DEFAULT) {
                PopularDestinationsController.sortHotels(Comparator.comparingDouble(Hotel::getHotelRating).reversed());
            }
            if (context == PopularDestinationsController.SortedContext.CHEAPER) {
                PopularDestinationsController.sortHotels(Comparator.comparingDouble(
                        h -> roomService.getMinRoomPriceByHotelId(h.getIdHotel())));
            }
            if (context == PopularDestinationsController.SortedContext.MORE_EXPENSIVE) {
                PopularDestinationsController.sortHotels(Comparator.comparingDouble(
                        (Hotel h) -> roomService.getMinRoomPriceByHotelId(h.getIdHotel())).reversed());
            }

            sorterWindow.hide();
        });
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
}
