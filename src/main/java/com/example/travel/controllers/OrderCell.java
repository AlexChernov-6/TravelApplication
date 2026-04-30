package com.example.travel.controllers;

import com.example.travel.models.Order;
import com.example.travel.util.ImageUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class OrderCell extends ListCell<Order> {
    private HBox generalHB;
    private Label statusOrderLB, dateAndTimeOrderLB, hotelLB, datesLB, actualMinPriceLB;
    private ImageView statusOrderIV;

    public OrderCell() {
        generalHB = new HBox(20);
        generalHB.setPadding(new Insets(10));
        generalHB.setAlignment(Pos.TOP_RIGHT);
        generalHB.setOnMouseEntered(e -> {
            generalHB.setStyle("-fx-background-color: rgba(245, 245, 245);");
        });
        generalHB.setOnMouseExited(e -> {
            generalHB.setStyle("");
        });

        statusOrderIV = new ImageView();
        statusOrderIV.setFitHeight(20);
        statusOrderIV.setFitWidth(20);
        statusOrderIV.setPreserveRatio(true);
        generalHB.getChildren().add(statusOrderIV);

        VBox centralVB = new VBox(10);
        centralVB.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(centralVB, Priority.ALWAYS);
        generalHB.getChildren().add(centralVB);

        statusOrderLB = new Label();
        centralVB.getChildren().add(statusOrderLB);

        dateAndTimeOrderLB = new Label();
        dateAndTimeOrderLB.setStyle("-fx-text-fill: rgba(180, 180, 180); -fx-font-size: 14px;");
        centralVB.getChildren().add(dateAndTimeOrderLB);

        hotelLB = new Label();
        hotelLB.setStyle("-fx-background-color: rgba(230, 230, 230); -fx-background-radius: 0 10 10 10; -fx-padding: 5 8;");
        centralVB.getChildren().add(hotelLB);

        datesLB = new Label();
        datesLB.setPrefWidth(150);
        generalHB.getChildren().add(datesLB);

        HBox discountPriceHB = new HBox(2);
        discountPriceHB.setPrefWidth(100);
        discountPriceHB.setAlignment(Pos.CENTER_RIGHT);
        generalHB.getChildren().add(discountPriceHB);

        actualMinPriceLB = new Label();
        actualMinPriceLB.getStyleClass().add("room-small-price");
        actualMinPriceLB.setStyle("-fx-text-fill: rgba(50, 50, 50); -fx-font-size: 21px;");

        ImageView imageRuble = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black-ruble.png"))));
        imageRuble.setFitWidth(22);
        imageRuble.setFitHeight(22);
        imageRuble.setPreserveRatio(true);

        discountPriceHB.getChildren().addAll(actualMinPriceLB, imageRuble);
    }

    @Override
    protected void updateItem(Order order, boolean empty) {
        super.updateItem(order, empty);

        if (empty || order == null) {
            setGraphic(null);
            setText(null);
        } else {
            DateTimeFormatter rusPattern = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            statusOrderLB.setText(order.isPaid() ? "Оплачен" : "Не оплачен");
            dateAndTimeOrderLB.setText(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
            hotelLB.setText("Путёвка в отель \"" + order.getRoom().getHotel().getHotelName() + "\"");
            datesLB.setText(order.getDateStart().format(rusPattern) + " - " + order.getDateEnd().format(rusPattern));

            StringBuilder stringBuilder = new StringBuilder();
            int count = 0;
            for (int j = String.format("%d", Math.round(order.getOrderCost())).length() - 1; j >= 0; j--) {
                stringBuilder.append(String.format("%d", Math.round(order.getOrderCost())).charAt(j));
                count++;
                if (count % 3 == 0) {
                    stringBuilder.append(' ');
                }
            }
            String resultPrice = stringBuilder.reverse().toString();

            actualMinPriceLB.setText(resultPrice);

            statusOrderIV.setImage(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(order.isPaid() ? "/images/is-paid.png" : "/images/is-not-paid.png"))));

            setGraphic(generalHB);
        }
    }
}
