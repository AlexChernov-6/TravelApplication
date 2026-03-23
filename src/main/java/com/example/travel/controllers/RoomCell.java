package com.example.travel.controllers;

import com.example.travel.models.Room;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

import static com.example.travel.controllers.HotelCell.*;

public class RoomCell extends ListCell<Room> {
    private GridPane rootGP;
    private ImageView imageView;
    private Label countImageLB, nameRoom, countRoomSleepingPlacesLB, roomSquareLB, actualMinPriceLB;
    private ImageView mealPlanIV, refundPolicyIV, paymentMethodIV;
    private Text actualPriceLB;

    public RoomCell() {
        rootGP = new GridPane();
        rootGP.setPrefHeight(200);
        rootGP.setMinWidth(200 + 250 + 120 + 200);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(200);
        col1.setHgrow(Priority.NEVER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(250);
        col2.setHgrow(Priority.ALWAYS);


        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPrefWidth(120);
        col3.setHgrow(Priority.NEVER);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPrefWidth(200);
        col4.setHgrow(Priority.NEVER);

        StackPane photosStackPane = new StackPane();

        imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);

        Rectangle clip = new Rectangle();
        clip.setWidth(200);
        clip.setHeight(200);
        clip.setArcWidth(30);
        clip.setArcHeight(30);

        imageView.setClip(clip);

        photosStackPane.getChildren().add(imageView);

        HBox countPhotoHB = new HBox(3);
        countPhotoHB.setPadding(new Insets(2, 3, 2, 3));
        StackPane.setAlignment(countPhotoHB, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(countPhotoHB, new Insets(0, 10, 10, 0));
        countPhotoHB.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-radius: 12px;");

        ImageView emptyImage = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/default-image-directions.png"))));
        emptyImage.setFitHeight(10);
        emptyImage.setFitHeight(10);
        emptyImage.setPreserveRatio(true);

        countPhotoHB.getChildren().add(emptyImage);

        countImageLB = new Label();
        countImageLB.setStyle("-fx-font-size: 9px; -fx-font-weight: bold;");

        countPhotoHB.getChildren().add(countImageLB);

        photosStackPane.getChildren().add(countPhotoHB);

        rootGP.getChildren().add(photosStackPane);

        VBox basicInformationVB = new VBox(10);
        GridPane.setColumnIndex(basicInformationVB, 1);
        basicInformationVB.setAlignment(Pos.TOP_LEFT);

        nameRoom = new Label();
        nameRoom.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        basicInformationVB.getChildren().add(nameRoom);

        Label roomSleepingPlaces = new Label("Число доступных мест: ");
        roomSleepingPlaces.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        basicInformationVB.getChildren().add(roomSleepingPlaces);

        HBox countRoomSleepingPlacesHB = new HBox(8);

        ImageView bedIV = new ImageView();
        bedIV.setFitHeight(12);
        bedIV.setFitWidth(12);
        bedIV.setPreserveRatio(true);

        countRoomSleepingPlacesLB = new Label();
        countRoomSleepingPlacesLB.setStyle("-fx-font-size: 18px;");

        countRoomSleepingPlacesHB.getChildren().addAll(bedIV, countRoomSleepingPlacesLB);

        basicInformationVB.getChildren().add(countRoomSleepingPlacesHB);

        HBox bottomHB = new HBox(2);
        VBox.setVgrow(bottomHB, Priority.ALWAYS);
        bottomHB.setAlignment(Pos.BOTTOM_LEFT);
        bottomHB.setPadding(new Insets(2, 5, 2, 5));

        HBox aboutTheRoomHB = new HBox(3);
        aboutTheRoomHB.setStyle("-fx-background-color: rgba(255,230,255, 0.8); -fx-background-radius: 12px;");

        Label aboutTheRoomLB = new Label("O номере");
        aboutTheRoomLB.setStyle("-fx-text-fill: rgba(176, 60, 176);");

        ImageView arrowRightIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/right-arrow-violet.png"))));
        arrowRightIV.setFitWidth(5);
        arrowRightIV.setFitHeight(5);
        arrowRightIV.setPreserveRatio(true);

        aboutTheRoomHB.getChildren().addAll(aboutTheRoomLB, arrowRightIV);

        bottomHB.getChildren().add(aboutTheRoomHB);

        roomSquareLB = new Label(" м²");
        roomSquareLB.setStyle("-fx-text-fill: rgba(170, 170, 170); -fx-background-color: rgba(240,240,240); -fx-background-radius: 12px;");

        bottomHB.getChildren().add(roomSquareLB);

        basicInformationVB.getChildren().add(bottomHB);

        rootGP.getChildren().add(basicInformationVB);

        GridPane featuresGP = new GridPane();
        GridPane.setColumnIndex(featuresGP, 2);

        ColumnConstraints col11 = new ColumnConstraints();
        col11.setPrefWidth(50);
        col11.setHgrow(Priority.NEVER);

        ColumnConstraints col12 = new ColumnConstraints();
        col12.setPrefWidth(40);
        col12.setHgrow(Priority.NEVER);

        featuresGP.getColumnConstraints().addAll(
                col11,
                col12,
                new ColumnConstraints()
        );

        featuresGP.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints(),
                new RowConstraints()
        );

        Label mealPlanLB = new Label("Питание");
        mealPlanLB.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(mealPlanLB);

        Label refundPolicyLB = new Label("Возврат");
        GridPane.setRowIndex(refundPolicyLB, 1);
        refundPolicyLB.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(refundPolicyLB);

        Label paymentMethodLB = new Label("Оплата");
        GridPane.setRowIndex(paymentMethodLB, 2);
        paymentMethodLB.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(paymentMethodLB);

        mealPlanIV = new ImageView();
        GridPane.setColumnIndex(mealPlanIV, 1);
        mealPlanIV.setFitHeight(20);
        mealPlanIV.setFitWidth(20);

        featuresGP.getChildren().add(mealPlanIV);

        refundPolicyIV = new ImageView();
        GridPane.setRowIndex(refundPolicyIV, 1);
        GridPane.setColumnIndex(refundPolicyIV, 1);
        refundPolicyIV.setFitHeight(20);
        refundPolicyIV.setFitWidth(20);

        featuresGP.getChildren().add(refundPolicyIV);

        paymentMethodIV = new ImageView();
        GridPane.setRowIndex(paymentMethodIV, 2);
        GridPane.setColumnIndex(paymentMethodIV, 1);
        paymentMethodIV.setFitHeight(20);
        paymentMethodIV.setFitWidth(20);

        featuresGP.getChildren().add(paymentMethodIV);

        Label mealPlanLB2 = new Label("Питание");
        mealPlanLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(mealPlanLB2, 2);

        featuresGP.getChildren().add(mealPlanLB);

        Label refundPolicyLB2 = new Label("Возврат");
        GridPane.setRowIndex(refundPolicyLB2, 1);
        refundPolicyLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(refundPolicyLB2, 2);

        featuresGP.getChildren().add(refundPolicyLB2);

        Label paymentMethodLB2 = new Label("Оплата");
        GridPane.setRowIndex(paymentMethodLB2, 2);
        paymentMethodLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(paymentMethodLB2, 2);

        featuresGP.getChildren().add(paymentMethodLB2);

        rootGP.getChildren().add(featuresGP);

        VBox minPriceRoomVB = new VBox(5);
        minPriceRoomVB.setPadding(new Insets(10));
        GridPane.setColumnIndex(minPriceRoomVB, 2);
        GridPane.setRowIndex(minPriceRoomVB, 0);
        minPriceRoomVB.setAlignment(Pos.TOP_LEFT);

        HBox discountPriceHB = new HBox(3);
        discountPriceHB.setAlignment(Pos.CENTER_LEFT);

        ImageView imageDiscount = new ImageView(DISCOUND_IMAGE);
        imageDiscount.setFitWidth(30);
        imageDiscount.setFitHeight(30);
        imageDiscount.setPreserveRatio(true);

        actualMinPriceLB = new Label("discount_price");
        actualMinPriceLB.getStyleClass().add("room-small-price");

        ImageView imageRuble = new ImageView(RUBLE_IMAGE);
        imageRuble.setFitWidth(25);
        imageRuble.setFitHeight(25);
        imageRuble.setPreserveRatio(true);

        discountPriceHB.getChildren().addAll(imageDiscount, actualMinPriceLB, imageRuble);

        minPriceRoomVB.getChildren().add(discountPriceHB);

        HBox actualPriceHB = new HBox();
        actualPriceHB.setAlignment(Pos.CENTER_LEFT);

        actualPriceLB = new Text("actual_price");
        actualPriceLB.getStyleClass().add("room-actual-price");

        ImageView imageSmallRuble = new ImageView(RUBLE_SMALL_IMAGE);
        imageSmallRuble.setFitWidth(20);
        imageSmallRuble.setFitHeight(20);
        imageSmallRuble.setPreserveRatio(true);

        actualPriceHB.getChildren().addAll(actualPriceLB, imageSmallRuble);

        minPriceRoomVB.getChildren().add(actualPriceHB);

        Button selectBtn = new Button("Выбрать");
        VBox.setVgrow(selectBtn, Priority.ALWAYS);
        selectBtn.setAlignment(Pos.BOTTOM_CENTER);
        selectBtn.getStyleClass().add("select-button");
        selectBtn.setPrefWidth(Double.MAX_VALUE);
        VBox.setMargin(selectBtn, new Insets(0, 30, 30, 10));

        minPriceRoomVB.getChildren().add(selectBtn);

        rootGP.getChildren().add(minPriceRoomVB);
    }

    @Override
    protected void updateItem(Room room, boolean empty) {
        super.updateItem(room, empty);

        if(empty || room == null) {
            setGraphic(null);
            setText(null);
        } else {
            updateStateCell(room);
            setGraphic(rootGP);
        }
    }

    private void updateStateCell(Room room) {

    }
}
