package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.services.RoomService;
import com.example.travel.util.HelpFullClass;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.util.Objects;

import static com.example.travel.controllers.HotelCell.*;

public class HotelWindow extends ScrollPane {
    private Hotel selectedHotel;

    private Label hotelNameLB, hotelCountStar, ratingLB, addressLabel, actualMinPriceLB;
    private Text actualPriceLB;
    private VBox rootVB;

    private Pane shadowPane;
    private Button closeBtn;
    private WebView webView;

    public HotelWindow(Hotel hotel) {
        this.selectedHotel = hotel;
        rootVB = new VBox(15);

        Platform.runLater(() -> {
            new HelpFullClass().scrollPaneAnimation(this);
        });
        this.setContent(rootVB);
        setFitToWidth(true);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        getStyleClass().add("scroll-pane");

        createBackBtn();

        createHotelInformation();
    }

    private void createBackBtn() {
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

        Label btnText = new Label("К списку отелей");
        btnText.setStyle("-fx-font-size: 14px;");

        buttonHB.getChildren().addAll(btnIV, btnText);

        buttonHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                PopularDestinationsController.getOverlaySP().getChildren().remove(this);
            }
        });

        rootVB.getChildren().add(buttonHB);
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
            if(newVal >= 700) {
                rootAP.setPrefWidth(newVal);
                rootAP.setMinWidth(newVal);
                rootAP.setMaxWidth(newVal);
            }
        });

        overSP.heightProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue() - 100;
            rootAP.setPrefHeight(newVal);
            rootAP.setMinHeight(newVal);
            rootAP.setMaxHeight(newVal);
        });

        Platform.runLater(() -> {
            double newWidth = overSP.getWidth();
            rootAP.setPrefWidth(newWidth);
            rootAP.setMinWidth(newWidth);
            rootAP.setMaxWidth(newWidth);

            double newHeight = overSP.getHeight() - 100;
            rootAP.setPrefHeight(newHeight);
            rootAP.setMinHeight(newHeight);
            rootAP.setMaxHeight(newHeight);
        });

        GridPane hotelInfoHeader = new GridPane();
        hotelInfoHeader.setPadding(new Insets(20, 15, 15, 15));
        hotelInfoHeader.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
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

        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);

        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.NEVER);

        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(Priority.ALWAYS);

        hotelInfoHeader.getRowConstraints().addAll(row1, row2, row3);

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
            if (e.getButton() == MouseButton.PRIMARY) {
                if(selectedHotel.getLongitude() != null && selectedHotel.getLatitude() != null) {
                    shadowPane = new Pane();
                    shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
                    overSP.getChildren().add(shadowPane);

                    closeBtn = new Button();
                    closeBtn.setPrefHeight(30);
                    closeBtn.setPrefWidth(30);
                    StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
                    StackPane.setMargin(closeBtn, new Insets(20, 20, 0, 0));
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

                    webView = new WebView();

                    double startWidthWebView = overSP.getWidth() * 0.7;
                    double startHeightWebView = overSP.getHeight() * 0.8;

                    webView.setPrefWidth(startWidthWebView);
                    webView.setMaxWidth(startWidthWebView);
                    webView.setMinWidth(startWidthWebView);
                    webView.setPrefHeight(startHeightWebView);
                    webView.setMaxHeight(startHeightWebView);
                    webView.setMinHeight(startHeightWebView);

                    // Привязка размеров (как у вас)
                    overSP.widthProperty().addListener((ob, oldV, newV) -> {
                        double newVal = newV.doubleValue() * 0.7;
                        webView.setPrefWidth(newVal);
                        webView.setMaxWidth(newVal);
                        webView.setMinWidth(newVal);
                    });
                    overSP.heightProperty().addListener((ob, oldV, newV) -> {
                        double newVal = newV.doubleValue() * 0.8;
                        webView.setPrefHeight(newVal);
                        webView.setMaxHeight(newVal);
                        webView.setMinHeight(newVal);
                    });

                    WebEngine webEngine = webView.getEngine();

                    webEngine.load("http://localhost:8080/map.html");

                    // После успешной загрузки вызываем initMap
                    webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            String coords = String.format("%.6f", selectedHotel.getLongitude()).replace(",", ".")
                                    + "," + String.format("%.6f", selectedHotel.getLatitude()).replace(",", ".");
                            webEngine.executeScript("initMap('" + coords + "', '" + selectedHotel.getHotelName()
                                    +  "', '" + selectedHotel.getHotelAddress() + "');");
                        }
                    });

                    overSP.getChildren().add(webView);
                } else System.out.println("Координаты пусты");
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

        ViewingImages viewingImages = new ViewingImages(selectedHotel, hotelInfoHeader);
        GridPane.setColumnIndex(viewingImages, 0);
        GridPane.setColumnSpan(viewingImages, 3);
        GridPane.setRowIndex(viewingImages, 2);
        GridPane.setMargin(viewingImages, new Insets(20, 0, 0, 0));
        GridPane.setValignment(viewingImages, VPos.BOTTOM);
        hotelInfoHeader.widthProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue() - 30;
            viewingImages.setPrefWidth(newVal);
            viewingImages.setMaxWidth(newVal);
            viewingImages.setMinWidth(newVal);
        });

        hotelInfoHeader.getChildren().add(viewingImages);

        rootAP.getChildren().add(hotelInfoHeader);

        rootVB.getChildren().add(rootAP);
    }

    public void hide() {
        shadowPane.setVisible(false);
        closeBtn.setVisible(false);
        webView.setVisible(false);
    }

    private void createBodyInformation() {
        HBox bodyHB = new HBox(3);


    }
}
