package com.example.travel.controllers;

import com.example.travel.models.HotelFeature;
import com.example.travel.models.Room;
import com.example.travel.models.RoomFeature;
import com.example.travel.services.RoomFeatureRelationService;
import com.example.travel.services.RoomFeatureService;
import com.example.travel.util.HelpFullClass;
import com.example.travel.util.ImageUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;

import static com.example.travel.controllers.FilterWindow.ensureVisible;
import static com.example.travel.controllers.HotelCell.*;
import static com.example.travel.util.ImageUtils.round;

public class RoomCard extends VBox {
    private GridPane rootGP;
    private ImageView imageView;
    private Label countImageLB, nameRoom, countRoomSleepingPlacesLB, roomSquareLB, actualMinPriceLB, mealPlanLB2
            , refundPolicyLB2, paymentMethodLB2;
    private ImageView mealPlanIV, refundPolicyIV, paymentMethodIV;
    private Text actualPriceLB;

    private Room room;
    private Button prevImageBtn, nextImageBtn;
    private ImageView imageViewRoom;

    private double widthWin = 600.0;
    private Pane shadowPane;
    private ScrollPane infoRoomScrollPane;
    private Button closeBtn;

    public RoomCard() {
        rootGP = new GridPane();
        rootGP.setPrefHeight(200);
        rootGP.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
        rootGP.setMinWidth(200 + 200 + 300 + 220);

        // Привязка ширины
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
        round(imageView, 30, 30, 30, 30);
        imageView.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                if(infoRoomScrollPane == null)
                    createWinAboutTheRoom();
                show();
            }
        });

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

        // Основная информация
        VBox basicInformationVB = new VBox(10);
        GridPane.setColumnIndex(basicInformationVB, 1);
        basicInformationVB.setAlignment(Pos.TOP_LEFT);
        GridPane.setMargin(basicInformationVB, new Insets(10));

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
        aboutTheRoomHB.setStyle("-fx-background-color: rgba(255,230,255, 0.8); -fx-background-radius: 12px; -fx-cursor: hand;");
        aboutTheRoomHB.setPadding(new Insets(2, 8, 2, 8));
        aboutTheRoomHB.setAlignment(Pos.CENTER_LEFT);
        aboutTheRoomHB.setPrefHeight(20);
        aboutTheRoomHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                if(infoRoomScrollPane == null)
                    createWinAboutTheRoom();
                show();
            }
        });

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

        // Особенности
        GridPane featuresGP = new GridPane();
        GridPane.setColumnIndex(featuresGP, 2);
        GridPane.setMargin(featuresGP, new Insets(10));
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

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(40);

        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(40);

        RowConstraints row3 = new RowConstraints();
        row3.setPrefHeight(40);

        featuresGP.getRowConstraints().addAll(
                row1,
                row2,
                row3
        );

        Label mealPlanLB = new Label("Питание");
        mealPlanLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(mealPlanLB);

        Label refundPolicyLB = new Label("Возврат");
        GridPane.setRowIndex(refundPolicyLB, 1);
        refundPolicyLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(refundPolicyLB);

        Label paymentMethodLB = new Label("Оплата");
        GridPane.setRowIndex(paymentMethodLB, 2);
        paymentMethodLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        featuresGP.getChildren().add(paymentMethodLB);

        mealPlanIV = new ImageView();
        GridPane.setColumnIndex(mealPlanIV, 1);
        mealPlanIV.setFitHeight(30);
        mealPlanIV.setFitWidth(30);

        featuresGP.getChildren().add(mealPlanIV);

        refundPolicyIV = new ImageView();
        GridPane.setRowIndex(refundPolicyIV, 1);
        GridPane.setColumnIndex(refundPolicyIV, 1);
        refundPolicyIV.setFitHeight(30);
        refundPolicyIV.setFitWidth(30);

        featuresGP.getChildren().add(refundPolicyIV);

        paymentMethodIV = new ImageView();
        GridPane.setRowIndex(paymentMethodIV, 2);
        GridPane.setColumnIndex(paymentMethodIV, 1);
        paymentMethodIV.setFitHeight(30);
        paymentMethodIV.setFitWidth(30);

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

        // Цена и кнопка
        VBox minPriceRoomVB = new VBox(5);
        GridPane.setColumnIndex(minPriceRoomVB, 3);
        GridPane.setRowIndex(minPriceRoomVB, 0);
        minPriceRoomVB.setAlignment(Pos.TOP_LEFT);
        minPriceRoomVB.setPrefWidth(220);
        GridPane.setMargin(minPriceRoomVB, new Insets(10));

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
        selectBtn.setOnAction(e -> {
            new CheckoutWindow();
        });

        selectVB.getChildren().add(selectBtn);

        minPriceRoomVB.getChildren().add(selectVB);

        rootGP.getChildren().add(minPriceRoomVB);

        getChildren().add(rootGP);
    }

    public void updateRoom(Room room) {
        if (room == null) return;

        this.room = room;

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

        if (mealPlanLB2.getText().equals("Без питания"))
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/no-food.png"))));
        else
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/food.png"))));

        if (refundPolicyLB2.getText().equals("Платная отмена"))
            refundPolicyIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/paid-cancellation.png"))));
        else
            refundPolicyIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/free-cancellation.png"))));

        paymentMethodIV.setImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/payment.png"))));
    }

    private void createWinAboutTheRoom() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();
        shadowPane = new Pane();
        shadowPane.setVisible(false);
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        overSP.getChildren().add(shadowPane);

        closeBtn = new Button();
        closeBtn.setPrefHeight(30);
        closeBtn.setPrefWidth(30);
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(15, 15, 0, 0));
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(event -> {
            hide();
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeBtn.setGraphic(closeImg);

        overSP.getChildren().add(closeBtn);

        infoRoomScrollPane = new ScrollPane();
        infoRoomScrollPane.setVisible(false);
        infoRoomScrollPane.setMaxWidth(widthWin + 10);
        infoRoomScrollPane.maxHeightProperty().bind(overSP.heightProperty().subtract(70));
        StackPane.setAlignment(infoRoomScrollPane, Pos.BOTTOM_CENTER);
        infoRoomScrollPane.setFitToWidth(true);
        infoRoomScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        infoRoomScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        infoRoomScrollPane.getStyleClass().addAll("scroll-pane", "scroll-pane-transparent");
        Platform.runLater(() -> {
            new HelpFullClass().scrollPaneAnimation(infoRoomScrollPane);
        });

        overSP.getChildren().add(infoRoomScrollPane);

        VBox infoRoomVB = new VBox(5);
        infoRoomVB.setPadding(new Insets(0, 0, 5, 0));
        infoRoomVB.setStyle("-fx-background-color: rgba(230, 230, 230); -fx-background-radius: 15 15 0 0;");
        infoRoomVB.setMaxWidth(widthWin);
        infoRoomVB.prefHeightProperty().bind(infoRoomScrollPane.heightProperty());

        infoRoomScrollPane.setContent(infoRoomVB);

        StackPane photoSP = new StackPane();
        photoSP.setStyle("-fx-background-radius: 15px;");

        imageViewRoom = new ImageView();
        imageViewRoom.setImage(room.getImageByNumber(room.getCurrentImageIndex()));
        imageViewRoom.setFitWidth(widthWin - 1.6);
        imageViewRoom.setPreserveRatio(true);
        round(imageViewRoom, 30, 30, 30, 30);

        Platform.runLater(() -> {
            photoSP.setMaxHeight(imageViewRoom.getLayoutBounds().getHeight());
            photoSP.setMaxWidth(infoRoomVB.getWidth());
        });

        prevImageBtn = new Button();
        prevImageBtn.getStyleClass().add("scroll-button");
        prevImageBtn.setPrefHeight(20);
        prevImageBtn.setPrefWidth(20);
        StackPane.setMargin(prevImageBtn, new Insets(0, 0, 0, 10));
        StackPane.setAlignment(prevImageBtn, Pos.CENTER_LEFT);
        prevImageBtn.setVisible(false);
        prevImageBtn.setOnAction(e -> {
            if (room != null) {
                int newIndex = room.getCurrentImageIndex() - 1;
                room.setCurrentImageIndex(newIndex);
                updateVisibleButton();
            }
        });

        prevImageBtn.setOnMouseEntered(event -> {
            prevImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.7);");
            prevImageBtn.setPrefHeight(27);
            prevImageBtn.setPrefWidth(27);
        });

        prevImageBtn.setOnMouseExited(event -> {
            prevImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
            prevImageBtn.setPrefHeight(20);
            prevImageBtn.setPrefWidth(20);
        });

        ImageView prevImageIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/left-arrow.png"))));
        prevImageIV.setFitHeight(10);
        prevImageIV.setFitWidth(10);
        prevImageIV.setPreserveRatio(true);

        prevImageBtn.setGraphic(prevImageIV);

        nextImageBtn = new Button();
        nextImageBtn.getStyleClass().add("scroll-button");
        nextImageBtn.setPrefHeight(20);
        nextImageBtn.setPrefWidth(20);
        StackPane.setMargin(nextImageBtn, new Insets(0, 10, 0, 0));
        StackPane.setAlignment(nextImageBtn, Pos.CENTER_RIGHT);
        nextImageBtn.setVisible(false);
        nextImageBtn.setOnAction(e -> {
            if (room != null) {
                int newIndex = room.getCurrentImageIndex() + 1;
                room.setCurrentImageIndex(newIndex);
                updateVisibleButton();
            }
        });

        nextImageBtn.setOnMouseEntered(event -> {
            nextImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.7);");
            nextImageBtn.setPrefHeight(27);
            nextImageBtn.setPrefWidth(27);
        });

        nextImageBtn.setOnMouseExited(event -> {
            nextImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
            nextImageBtn.setPrefHeight(20);
            nextImageBtn.setPrefWidth(20);
        });

        ImageView nextImageIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/right-arrow.png"))));
        nextImageIV.setFitHeight(10);
        nextImageIV.setFitWidth(10);
        nextImageIV.setPreserveRatio(true);

        nextImageBtn.setGraphic(nextImageIV);

        photoSP.setOnMouseEntered(e -> {
            updateVisibleButton();
        });

        photoSP.setOnMouseExited(e -> {
            prevImageBtn.setVisible(false);
            nextImageBtn.setVisible(false);
        });

        photoSP.getChildren().addAll(imageViewRoom, prevImageBtn, nextImageBtn);

        infoRoomVB.getChildren().add(photoSP);

        VBox descriptionVB = new VBox(10);
        descriptionVB.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15;");

        Label nameRoom = new Label(room.getRoomName());
        nameRoom.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        descriptionVB.getChildren().add(nameRoom);

        Label descriptionRoom = new Label(room.getRoomDescription());
        descriptionRoom.setWrapText(true);
        descriptionRoom.setStyle("-fx-font-size: 18px;");
        descriptionVB.getChildren().add(descriptionRoom);

        Label squareRoom = new Label(String.format("Площадь: %d м²", Math.round(room.getRoomSquare())));
        squareRoom.setStyle("-fx-font-size: 19px;");
        descriptionVB.getChildren().add(squareRoom);

        infoRoomVB.getChildren().add(descriptionVB);

        FlowPane roomFeaturesContainer = new FlowPane();
        roomFeaturesContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15;");
        roomFeaturesContainer.setHgap(3);
        roomFeaturesContainer.setVgap(4);
        roomFeaturesContainer.setAlignment(Pos.BOTTOM_LEFT);
        roomFeaturesContainer.setPadding(new Insets(0, 0, 10, 10));

        List<RoomFeature> features = new RoomFeatureRelationService().getAllRoomFeatureByRoomId(room.getIdRooms());

        for (RoomFeature feature : features) {
            Label label = new Label(feature.getFeatureName());
            label.getStyleClass().add("feature-label-big");
            roomFeaturesContainer.getChildren().add(label);
        }

        infoRoomVB.getChildren().add(roomFeaturesContainer);

        VBox numberSettingsVB = new VBox(10);
        numberSettingsVB.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15;");
        infoRoomVB.getChildren().add(numberSettingsVB);

        Label numberSettingsLB = new Label("Настройки номера");
        numberSettingsLB.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        numberSettingsVB.getChildren().add(numberSettingsLB);

        VBox coundSleepingPlacesVB = new VBox(7);
        numberSettingsVB.getChildren().add(coundSleepingPlacesVB);

        Label roomSleepingPlaces = new Label("Число доступных мест: ");
        roomSleepingPlaces.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        coundSleepingPlacesVB.getChildren().add(roomSleepingPlaces);

        HBox countRoomSleepingPlacesHB = new HBox(8);
        countRoomSleepingPlacesHB.setAlignment(Pos.CENTER_LEFT);

        ImageView bedIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/bed.png"))));
        bedIV.setFitHeight(20);
        bedIV.setFitWidth(20);
        bedIV.setPreserveRatio(true);

        Label countRoomSleepingPlacesLB = new Label();
        countRoomSleepingPlacesLB.setStyle("-fx-font-size: 16px;");

        countRoomSleepingPlacesHB.getChildren().addAll(bedIV, countRoomSleepingPlacesLB);

        coundSleepingPlacesVB.getChildren().add(countRoomSleepingPlacesHB);

        short roomCountRoomSleepingPlaces = room.getRoomSleepingPlaces();
        if (roomCountRoomSleepingPlaces == 1)
            countRoomSleepingPlacesLB.setText("1 место");
        else if (roomCountRoomSleepingPlaces > 1 && roomCountRoomSleepingPlaces <= 4)
            countRoomSleepingPlacesLB.setText(String.format("%d места", roomCountRoomSleepingPlaces));
        else
            countRoomSleepingPlacesLB.setText(String.format("%d мест", roomCountRoomSleepingPlaces));

        VBox foodVB = new VBox(7);
        numberSettingsVB.getChildren().add(foodVB);

        Label mealPlanLB = new Label("Питание");
        mealPlanLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        foodVB.getChildren().add(mealPlanLB);

        HBox foodHB = new HBox(5);
        foodVB.getChildren().add(foodHB);

        ImageView mealPlanIV = new ImageView();
        mealPlanIV.setFitHeight(22);
        mealPlanIV.setFitWidth(22);

        Label mealPlanLB2 = new Label("Питание");
        mealPlanLB2.setStyle("-fx-font-size: 16px;");

        foodHB.getChildren().addAll(mealPlanIV, mealPlanLB2);

        mealPlanLB2.setText(room.getMealPlan().toString());

        if (mealPlanLB2.getText().equals("Без питания"))
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/no-food.png"))));
        else
            mealPlanIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/food.png"))));

        VBox refundVB = new VBox(7);
        numberSettingsVB.getChildren().add(refundVB);

        Label refundLB = new Label("Возврат");
        refundLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        refundVB.getChildren().add(refundLB);

        HBox refundHB = new HBox(5);
        refundVB.getChildren().add(refundHB);

        ImageView refundIV = new ImageView();
        refundIV.setFitHeight(22);
        refundIV.setFitWidth(22);

        Label refundLB2 = new Label(room.getRefundPolicy().toString());
        refundLB2.setStyle("-fx-font-size: 16px;");

        refundHB.getChildren().addAll(refundIV, refundLB2);

        if (refundLB2.getText().equals("Платная отмена"))
            refundIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/paid-cancellation.png"))));
        else
            refundIV.setImage(
                    new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/free-cancellation.png"))));

        VBox paymentVB = new VBox(7);
        numberSettingsVB.getChildren().add(paymentVB);

        Label paymentLB = new Label("Оплата");
        paymentLB.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        paymentVB.getChildren().add(paymentLB);

        HBox paymentHB = new HBox(5);
        paymentVB.getChildren().add(paymentHB);

        ImageView paymentMethodIV = new ImageView();
        paymentMethodIV.setFitHeight(22);
        paymentMethodIV.setFitWidth(22);

        Label paymentLB2 = new Label(room.getPaymentMethod().toString());
        paymentLB2.setStyle("-fx-font-size: 16px;");

        paymentHB.getChildren().addAll(paymentMethodIV, paymentLB2);

        paymentMethodIV.setImage(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/payment.png"))));

        HBox bottomHB = new HBox();
        bottomHB.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-padding: 15;");
        infoRoomVB.getChildren().add(bottomHB);
        bottomHB.setAlignment(Pos.CENTER_LEFT);

        VBox minPriceRoomVB = new VBox(5);
        minPriceRoomVB.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(minPriceRoomVB, Priority.ALWAYS);

        HBox discountPriceHB = new HBox(3);
        discountPriceHB.setAlignment(Pos.CENTER_LEFT);

        ImageView imageDiscount = new ImageView(DISCOUND_IMAGE);
        imageDiscount.setFitWidth(30);
        imageDiscount.setFitHeight(30);
        imageDiscount.setPreserveRatio(true);

        Label actualMinPriceLB = new Label(String.format("%.2f", room.getRoomPrice()));
        actualMinPriceLB.getStyleClass().add("room-small-price");

        ImageView imageRuble = new ImageView(RUBLE_IMAGE);
        imageRuble.setFitWidth(25);
        imageRuble.setFitHeight(25);
        imageRuble.setPreserveRatio(true);

        discountPriceHB.getChildren().addAll(imageDiscount, actualMinPriceLB, imageRuble);

        minPriceRoomVB.getChildren().add(discountPriceHB);

        HBox actualPriceHB = new HBox();
        actualPriceHB.setAlignment(Pos.CENTER_LEFT);

        Text actualPriceLB = new Text(String.format("%.2f", room.getRoomPrice() + room.getRoomPrice() * 0.05));
        actualPriceLB.getStyleClass().add("room-actual-price");

        ImageView imageSmallRuble = new ImageView(RUBLE_SMALL_IMAGE);
        imageSmallRuble.setFitWidth(20);
        imageSmallRuble.setFitHeight(20);
        imageSmallRuble.setPreserveRatio(true);

        actualPriceHB.getChildren().addAll(actualPriceLB, imageSmallRuble);

        minPriceRoomVB.getChildren().add(actualPriceHB);

        bottomHB.getChildren().add(minPriceRoomVB);

        Button toBook = new Button("Забронировать");
        toBook.setPrefWidth(250);
        toBook.getStyleClass().add("show-result-button");
        toBook.prefHeightProperty().bind(bottomHB.heightProperty().subtract(40));
        toBook.setOnAction(e -> {
            new CheckoutWindow();
        });

        bottomHB.getChildren().add(toBook);

        overSP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!infoRoomScrollPane.isVisible()) return;

            Point2D pointInWindow = infoRoomScrollPane.screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && infoRoomScrollPane.contains(pointInWindow))
                return;
            else
                hide();
        });
    }

    private void updateVisibleButton() {
        if (room == null) return;
        int index = room.getCurrentImageIndex();
        int max = room.getRoomPhotos().length - 1;
        prevImageBtn.setVisible(index > 0);
        nextImageBtn.setVisible(index < max);

        imageViewRoom.setImage(room.getImageByNumber(room.getCurrentImageIndex()));

        imageViewRoom.requestFocus();
    }

    private void hide() {
        shadowPane.setVisible(false);
        infoRoomScrollPane.setVisible(false);
        closeBtn.setVisible(false);
    }

    private void show() {
        shadowPane.setVisible(true);
        infoRoomScrollPane.setVisible(true);
        closeBtn.setVisible(true);
    }
}