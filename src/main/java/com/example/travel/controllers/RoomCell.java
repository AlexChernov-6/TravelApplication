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
    private Label countImageLB, nameRoom, countRoomSleepingPlacesLB, roomSquareLB, actualMinPriceLB, mealPlanLB2
            , refundPolicyLB2, paymentMethodLB2;
    private ImageView mealPlanIV, refundPolicyIV, paymentMethodIV;
    private Text actualPriceLB;

    public RoomCell() {
        setPadding(new Insets(0, 0, 20, 0));
        rootGP = new GridPane();
        rootGP.setPrefHeight(200);
        rootGP.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
        rootGP.setMinWidth(200 + 200 + 300 + 220);

        rootGP.maxWidthProperty().bind(widthProperty().subtract(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(200);
        col1.setHgrow(Priority.NEVER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.prefWidthProperty().bind(widthProperty().subtract(200 + 200 + 300));
        col2.setMinWidth(200);
        col2.setHgrow(Priority.ALWAYS);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setMinWidth(300);
        col3.setHgrow(Priority.NEVER);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setMinWidth(220);
        col4.setHgrow(Priority.NEVER);

        rootGP.getColumnConstraints().addAll(col1, col2, col3, col4);

        StackPane photosStackPane = new StackPane();
        photosStackPane.setPrefWidth(200);
        photosStackPane.setPrefHeight(200);

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
        countPhotoHB.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ImageView emptyImage = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/empty-image.png"))));
        emptyImage.setFitHeight(15);
        emptyImage.setFitHeight(15);
        emptyImage.setPreserveRatio(true);

        countPhotoHB.getChildren().add(emptyImage);

        countImageLB = new Label();
        countImageLB.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        countPhotoHB.getChildren().add(countImageLB);

        photosStackPane.getChildren().add(countPhotoHB);

        rootGP.getChildren().add(photosStackPane);

        VBox basicInformationVB = new VBox(10);
        GridPane.setColumnIndex(basicInformationVB, 1);
        basicInformationVB.setAlignment(Pos.TOP_LEFT);
        basicInformationVB.setPadding(new Insets(0, 10, 10, 10));

        nameRoom = new Label();
        nameRoom.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        basicInformationVB.getChildren().add(nameRoom);

        Label roomSleepingPlaces = new Label("Число доступных мест: ");
        roomSleepingPlaces.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        basicInformationVB.getChildren().add(roomSleepingPlaces);

        HBox countRoomSleepingPlacesHB = new HBox(8);
        countRoomSleepingPlacesHB.setAlignment(Pos.CENTER_LEFT);

        ImageView bedIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bed.png"))));
        bedIV.setFitHeight(20);
        bedIV.setFitWidth(20);
        bedIV.setPreserveRatio(true);

        countRoomSleepingPlacesLB = new Label();
        countRoomSleepingPlacesLB.setStyle("-fx-font-size: 18px;");

        countRoomSleepingPlacesHB.getChildren().addAll(bedIV, countRoomSleepingPlacesLB);

        basicInformationVB.getChildren().add(countRoomSleepingPlacesHB);

        VBox bottomVB = new VBox();
        bottomVB.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(bottomVB, Priority.ALWAYS);

        HBox bottomHB = new HBox(2);
        bottomVB.getChildren().add(bottomHB);

        HBox aboutTheRoomHB = new HBox(3);
        aboutTheRoomHB.setStyle("-fx-background-color: rgba(255,230,255, 0.8); -fx-background-radius: 12px;");
        aboutTheRoomHB.setPadding(new Insets(2, 8, 2, 8));
        aboutTheRoomHB.setAlignment(Pos.CENTER_LEFT);
        aboutTheRoomHB.setPrefHeight(20);

        Label aboutTheRoomLB = new Label("O номере");
        aboutTheRoomLB.setStyle("-fx-text-fill: rgba(176, 60, 176);");

        ImageView arrowRightIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/right-arrow-violet.png"))));
        arrowRightIV.setFitWidth(8);
        arrowRightIV.setFitHeight(8);
        arrowRightIV.setPreserveRatio(true);

        aboutTheRoomHB.getChildren().addAll(aboutTheRoomLB, arrowRightIV);

        bottomHB.getChildren().add(aboutTheRoomHB);

        roomSquareLB = new Label();
        roomSquareLB.setStyle("-fx-text-fill: rgba(170, 170, 170); -fx-background-color: rgba(240,240,240); -fx-background-radius: 12px;");
        roomSquareLB.setPadding(new Insets(2, 8, 2, 8));

        bottomHB.getChildren().add(roomSquareLB);

        basicInformationVB.getChildren().add(bottomVB);

        rootGP.getChildren().add(basicInformationVB);

        GridPane featuresGP = new GridPane();
        GridPane.setColumnIndex(featuresGP, 2);
        featuresGP.setPrefWidth(200);

        ColumnConstraints col11 = new ColumnConstraints();
        col11.setPrefWidth(70);
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

        mealPlanLB2 = new Label("Питание");
        mealPlanLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(mealPlanLB2, 2);

        featuresGP.getChildren().add(mealPlanLB2);

        refundPolicyLB2 = new Label("Возврат");
        GridPane.setRowIndex(refundPolicyLB2, 1);
        refundPolicyLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(refundPolicyLB2, 2);

        featuresGP.getChildren().add(refundPolicyLB2);

        paymentMethodLB2 = new Label("Оплата");
        GridPane.setRowIndex(paymentMethodLB2, 2);
        paymentMethodLB2.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        GridPane.setColumnIndex(paymentMethodLB2, 2);

        featuresGP.getChildren().add(paymentMethodLB2);

        rootGP.getChildren().add(featuresGP);

        VBox minPriceRoomVB = new VBox(5);
        GridPane.setColumnIndex(minPriceRoomVB, 3);
        GridPane.setRowIndex(minPriceRoomVB, 0);
        minPriceRoomVB.setAlignment(Pos.TOP_LEFT);
        minPriceRoomVB.setPrefWidth(220);

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

        VBox selectVB = new VBox();
        selectVB.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(selectVB, Priority.ALWAYS);

        Button selectBtn = new Button("К оформлению");
        selectBtn.getStyleClass().add("select-button");
        selectBtn.setPrefWidth(Double.MAX_VALUE);

        selectVB.getChildren().add(selectBtn);

        minPriceRoomVB.getChildren().add(selectVB);

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
        imageView.setImage(room.getImageByNumber(0));
        countImageLB.setText(String.format("%d фото", room.getRoomPhotos().length));
        nameRoom.setText(room.getRoomName());
        short roomCountRoomSleepingPlaces = room.getRoomSleepingPlaces();
        if (roomCountRoomSleepingPlaces == 1)
            countRoomSleepingPlacesLB.setText("1 место");
        else if (roomCountRoomSleepingPlaces > 1 && roomCountRoomSleepingPlaces <= 4)
            countRoomSleepingPlacesLB.setText(String.format("%d места", roomCountRoomSleepingPlaces));
        else
            countRoomSleepingPlacesLB.setText(String.format("%d мест", roomCountRoomSleepingPlaces));

        roomSquareLB.setText(String.format("%d м²", Math.round(room.getRoomSquare())));

        actualMinPriceLB.setText(String.format("%.2f", room.getRoomPrice()));
        actualPriceLB.setText(String.format("%.2f", room.getRoomPrice() + room.getRoomPrice() * 0.05));

        mealPlanLB2.setText(room.getMealPlan().toString());
        refundPolicyLB2.setText(room.getRefundPolicy().toString());
        paymentMethodLB2.setText(room.getPaymentMethod().toString());

        if(mealPlanLB2.getText().equals("Без питания"))
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/no-food.png"))));
        else
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/food.png"))));

        if(refundPolicyLB2.getText().equals("Платная отмена"))
            refundPolicyIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/paid-cancellation.png"))));
        else
            refundPolicyIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/free-cancellation.png"))));

        paymentMethodIV.setImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/payment.png"))));
    }
}
