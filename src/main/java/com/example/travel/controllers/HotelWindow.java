package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.services.RoomService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.Objects;

import static com.example.travel.controllers.HotelCell.*;

public class HotelWindow extends VBox {
    private Hotel selectedHotel;

    private Label hotelNameLB, hotelCountStar, ratingLB, addressLabel, actualMinPriceLB;
    private Text actualPriceLB;

    public HotelWindow(Hotel hotel) {
        this.selectedHotel = hotel;
        setSpacing(15);
        setStyle("-fx-background-color: white;");

        createBackBtn();

        createHotelInformation();
    }

    private void createBackBtn() {
        HBox buttonHB = new HBox(20);
        buttonHB.setAlignment(Pos.CENTER_LEFT);
        buttonHB.getStyleClass().add("set-hand-cursor");

        ImageView btnIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/left-arrow.png"))));
        btnIV.setFitHeight(20);
        btnIV.setFitWidth(20 / 1.5);
        btnIV.setPreserveRatio(true);

        Label btnText = new Label("К списку отелей");
        btnText.setStyle("-fx-font-size: 14px;");

        buttonHB.getChildren().addAll(btnIV, btnText);

        buttonHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                //Реализовать кнопку назад
                PopularDestinationsController.getOverlaySP().getChildren().remove(this);
            }
        });

        getChildren().add(buttonHB);
    }

    private void createHotelInformation() {
        AnchorPane rootAP = new AnchorPane();

        StackPane overSP = PopularDestinationsController.getOverlaySP();
        double startWidth = overSP.getWidth();
        double startHeight = overSP.getHeight() - 100;

        rootAP.setPrefWidth(startWidth);
        rootAP.setMinWidth(startWidth);
        rootAP.setMaxWidth(startWidth);
        rootAP.setPrefWidth(startHeight);
        rootAP.setMinWidth(startHeight);
        rootAP.setMaxWidth(startHeight);

        overSP.widthProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue();
            rootAP.setPrefWidth(newVal);
            rootAP.setMinWidth(newVal);
            rootAP.setMaxWidth(newVal);
        });

        overSP.heightProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue() - 100;
            rootAP.setPrefHeight(newVal);
            rootAP.setMinHeight(newVal);
            rootAP.setMaxHeight(newVal);
        });

        GridPane hotelInfoHeader = new GridPane();
        hotelInfoHeader.setPadding(new Insets(20, 15, 15, 15));
        hotelInfoHeader.setPrefHeight(100);
        AnchorPane.setTopAnchor(hotelInfoHeader, 20.0);
        AnchorPane.setLeftAnchor(hotelInfoHeader, 15.0);
        AnchorPane.setRightAnchor(hotelInfoHeader, 15.0);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setMinWidth(250);

        ColumnConstraints col2 = new ColumnConstraints(100);
        col2.setHgrow(Priority.NEVER);
        col2.setMinWidth(150);

        ColumnConstraints col3 = new ColumnConstraints(100);
        col3.setHgrow(Priority.NEVER);
        col3.setMinWidth(200);

        hotelInfoHeader.getColumnConstraints().addAll(col1, col2, col3);

        hotelInfoHeader.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints()
        );

        HBox hotelNameHB = new HBox();
        GridPane.setValignment(hotelNameHB, VPos.BOTTOM);
        hotelNameHB.setAlignment(Pos.TOP_LEFT);
        GridPane.setColumnIndex(hotelNameHB, 0);
        GridPane.setRowIndex(hotelNameHB, 0);

        hotelNameLB = new Label(selectedHotel.getHotelName());
        hotelNameLB.getStyleClass().add("hotel-name-label");

        hotelCountStar = new Label(" " + selectedHotel.getCountStars());
        hotelCountStar.getStyleClass().add("hotel-name-label");

        ImageView starImageView = new ImageView(STAR_IMAGE);
        starImageView.setFitHeight(15);
        starImageView.setFitWidth(15);
        starImageView.setPreserveRatio(true);

        hotelNameHB.getChildren().addAll(hotelNameLB, hotelCountStar, starImageView);

        hotelInfoHeader.getChildren().add(hotelNameHB);

        HBox ratingAndAddressHotel = new HBox(10);
        ratingAndAddressHotel.setAlignment(Pos.TOP_LEFT);
        GridPane.setColumnIndex(ratingAndAddressHotel, 0);
        GridPane.setRowIndex(ratingAndAddressHotel, 1);

        ratingLB = new Label(String.format("%.1f", selectedHotel.getHotelRating()));
        ratingLB.getStyleClass().add("hotel-rating-label");
        ratingLB.setAlignment(Pos.CENTER);
        ratingLB.setPadding(new Insets(2, 10, 2, 10));
        double newValue = Double.parseDouble(ratingLB.getText().replace(",", "."));

        if (newValue >= 4)
            ratingLB.setStyle("-fx-background-color: #0bb527;");
        else if (newValue < 4 && newValue >= 3)
            ratingLB.setStyle("-fx-background-color: #7fb50b;");
        else if (newValue < 3 && newValue >= 2)
            ratingLB.setStyle("-fx-background-color: #cbdb16;");
        else
            ratingLB.setStyle("-fx-background-color: #b00000;");

        HBox addressHB = new HBox(3);
        addressHB.setAlignment(Pos.CENTER_LEFT);
        addressHB.getStyleClass().add("set-hand-cursor");

        ImageView mapIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/map.png"))));
        mapIV.setFitWidth(11);
        mapIV.setFitHeight(11);
        mapIV.setPreserveRatio(true);

        Label addressLabel = new Label(selectedHotel.getHotelAddress());
        addressLabel.setStyle("-fx-text-fill: #b40acf");

        addressHB.getChildren().addAll(mapIV, addressLabel);

        addressHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                //Обработать нажатие на адрес
            }
        });

        ratingAndAddressHotel.getChildren().addAll(ratingLB, addressHB);

        hotelInfoHeader.getChildren().add(ratingAndAddressHotel);

        HBox discountPriceHB = new HBox(3);
        discountPriceHB.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnIndex(discountPriceHB, 1);
        GridPane.setRowIndex(discountPriceHB, 0);

        ImageView imageDiscount = new ImageView(DISCOUND_IMAGE);
        imageDiscount.setFitWidth(30);
        imageDiscount.setFitHeight(30);
        imageDiscount.setPreserveRatio(true);

        double minPrice = new RoomService().getMinRoomPriceByHotelId(selectedHotel.getIdHotel());
        actualMinPriceLB = new Label(String.format("%.2f", minPrice));
        actualMinPriceLB.getStyleClass().add("room-small-price");

        ImageView imageRuble = new ImageView(RUBLE_IMAGE);
        imageRuble.setFitWidth(25);
        imageRuble.setFitHeight(25);
        imageRuble.setPreserveRatio(true);

        discountPriceHB.getChildren().addAll(imageDiscount, actualMinPriceLB, imageRuble);

        hotelInfoHeader.getChildren().add(discountPriceHB);

        HBox actualPriceHB = new HBox();
        actualPriceHB.setAlignment(Pos.CENTER_LEFT);
        GridPane.setColumnIndex(actualPriceHB, 1);
        GridPane.setRowIndex(actualPriceHB, 1);

        actualPriceLB = new Text(String.format("%.2f", minPrice + minPrice * 0.05));
        actualPriceLB.getStyleClass().add("room-actual-price");

        ImageView imageSmallRuble = new ImageView(RUBLE_SMALL_IMAGE);
        imageSmallRuble.setFitWidth(20);
        imageSmallRuble.setFitHeight(20);
        imageSmallRuble.setPreserveRatio(true);

        actualPriceHB.getChildren().addAll(actualPriceLB, imageSmallRuble);

        hotelInfoHeader.getChildren().add(actualPriceHB);

        Button chooseRoom = new Button("Выбрать номер");
        GridPane.setColumnIndex(chooseRoom, 2);
        GridPane.setRowIndex(chooseRoom, 0);
        GridPane.setRowSpan(chooseRoom, 2);
        chooseRoom.setPrefWidth(300);
        chooseRoom.getStyleClass().add("show-result-button");
        chooseRoom.setOnAction(e -> {
            //Обработать нажатие кнопки
        });

        hotelInfoHeader.getChildren().add(chooseRoom);

        rootAP.getChildren().add(hotelInfoHeader);

        ViewingImages viewingImages = new ViewingImages(selectedHotel);
        AnchorPane.setTopAnchor(viewingImages, 110.0);
        AnchorPane.setLeftAnchor(viewingImages, 0.0);
        AnchorPane.setRightAnchor(viewingImages, 0.0);
        viewingImages.setPrefHeight(400);

        rootAP.getChildren().add(viewingImages);

        getChildren().add(rootAP);
    }
}
