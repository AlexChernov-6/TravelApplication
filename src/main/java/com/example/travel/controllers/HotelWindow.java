package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.models.Review;
import com.example.travel.models.Room;
import com.example.travel.services.ReviewService;
import com.example.travel.services.RoomService;
import com.example.travel.util.HelpFullClass;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.travel.controllers.HotelCell.*;
import static com.example.travel.util.HelpFullClass.getRussianMonthName;

public class HotelWindow extends ScrollPane {
    private Hotel selectedHotel;

    private Label hotelNameLB, hotelCountStar, ratingLB, addressLabel, actualMinPriceLB;
    private Text actualPriceLB;
    private final VBox rootVB;
    private ViewingImages viewingImages;

    private Pane shadowPane;
    private Button closeBtn, closeCommentsBtn;
    private WebView webView;

    private Label ratLB, ratString, countRev, nameUser, reviewDate, reviewComment;

    private ListView<Review> reviewListView;

    public HotelWindow() {
        rootVB = new VBox(15);
        rootVB.setPadding(new Insets(0, 0, 20, 0));

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

        createReviews();
    }

    protected void updateSelectedHotel(Hotel hotel) {
        this.selectedHotel = hotel;

        hotelNameLB.setText(selectedHotel.getHotelName());
        hotelCountStar.setText(" " + selectedHotel.getCountStars());
        ratingLB.setText(String.format("%.1f", selectedHotel.getHotelRating()));

        double newValue = Double.parseDouble(ratingLB.getText().replace(",", "."));

        if (newValue >= 4)
            ratingLB.setStyle("-fx-background-color: #0bb527;");
        else if (newValue < 4 && newValue >= 3)
            ratingLB.setStyle("-fx-background-color: #7fb50b;");
        else if (newValue < 3 && newValue >= 2)
            ratingLB.setStyle("-fx-background-color: #cbdb16;");
        else
            ratingLB.setStyle("-fx-background-color: #b00000;");

        addressLabel.setText(selectedHotel.getHotelAddress());

        double minPrice = new RoomService().getMinRoomPriceByHotelId(selectedHotel.getIdHotel());
        actualMinPriceLB.setText(String.format("%.2f", minPrice));

        actualPriceLB.setText(String.format("%.2f", minPrice + minPrice * 0.05));

        viewingImages.updateSelectedHotel(hotel);

        ratLB.setText(String.format("%.1f", selectedHotel.getHotelRating()));

        if (newValue >= 4) {
            ratLB.setStyle("-fx-text-fill: #0bb527; -fx-font-size: 26px; -fx-font-weight: bold;");
            ratString.setStyle("-fx-text-fill: #0bb527; -fx-font-size: 16px; -fx-font-weight: bold;");
            ratString.setText("Безупречно");
        } else if (newValue < 4 && newValue >= 3) {
            ratLB.setStyle("-fx-text-fill: #7fb50b; -fx-font-size: 26px; -fx-font-weight: bold;");
            ratString.setStyle("-fx-text-fill: #7fb50b; -fx-font-size: 16px; -fx-font-weight: bold;");
            ratString.setText("Хорошо");
        } else if (newValue < 3 && newValue >= 2) {
            ratLB.setStyle("-fx-text-fill: #cbdb16; -fx-font-size: 26px; -fx-font-weight: bold;");
            ratString.setStyle("-fx-text-fill: #cbdb16; -fx-font-size: 16px; -fx-font-weight: bold;");
            ratString.setText("Нормально");
        } else {
            ratLB.setStyle("-fx-text-fill: #b00000; -fx-font-size: 26px; -fx-font-weight: bold;");
            ratString.setStyle("-fx-text-fill: #b00000; -fx-font-size: 16px;");
        }

        countRev.setText("Число оценок: " + hotel.getCountRatings());

        Review bestReview = new ReviewService().getBestReview(hotel.getIdHotel());

        nameUser.setText(bestReview.getUser().getUserSecondName());
        LocalDate lDT = bestReview.getCratedAt().toLocalDate();
        reviewDate.setText(lDT.getDayOfMonth() + " " + getRussianMonthName(lDT.getMonth().getValue()).toLowerCase()
                + " " + lDT.getYear());
        reviewComment.setText(bestReview.getComment());
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
                setVisible(false);
            }
        });

        rootVB.getChildren().add(buttonHB);
    }

    private void createHotelInformation() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        GridPane hotelInfoHeader = new GridPane();
        hotelInfoHeader.setPadding(new Insets(20, 15, 15, 15));
        hotelInfoHeader.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        VBox.setMargin(hotelInfoHeader, new Insets(0, 15, 0, 15));

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

        RowConstraints row4 = new RowConstraints();

        hotelInfoHeader.getRowConstraints().addAll(row1, row2, row3, row4);

        HBox hotelNameHB = new HBox();
        GridPane.setValignment(hotelNameHB, VPos.BOTTOM);
        hotelNameHB.setAlignment(Pos.TOP_LEFT);
        GridPane.setColumnIndex(hotelNameHB, 0);
        GridPane.setRowIndex(hotelNameHB, 0);

        hotelNameLB = new Label();
        hotelNameLB.getStyleClass().add("hotel-name-label");

        hotelCountStar = new Label();
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

        ratingLB = new Label();
        ratingLB.getStyleClass().add("hotel-rating-label");
        ratingLB.setAlignment(Pos.CENTER);
        ratingLB.setPadding(new Insets(2, 10, 2, 10));

        HBox addressHB = new HBox(3);
        addressHB.setAlignment(Pos.CENTER_LEFT);
        addressHB.getStyleClass().add("set-hand-cursor");

        ImageView mapIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/map.png"))));
        mapIV.setFitWidth(11);
        mapIV.setFitHeight(11);
        mapIV.setPreserveRatio(true);

        addressLabel = new Label();
        addressLabel.setStyle("-fx-text-fill: #b40acf");

        addressHB.getChildren().addAll(mapIV, addressLabel);

        addressHB.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if(selectedHotel.getLongitude() != null && selectedHotel.getLatitude() != null) {
                    if(overSP.getChildren().stream().filter(node ->
                            node.getUserData() != null && node.getUserData().equals("mapWebView")).toList().isEmpty())
                        createMap();
                    showMap();
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

        actualMinPriceLB = new Label();
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

        actualPriceLB = new Text();
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

        viewingImages = new ViewingImages(hotelInfoHeader);
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

        Platform.runLater(hotelInfoHeader::layout);

        rootVB.getChildren().add(hotelInfoHeader);
    }

    private void createReviews() {
        HBox reviewsHB = new HBox(0.5);
        VBox.setMargin(reviewsHB, new Insets(0, 15, 0, 15));
        reviewsHB.setStyle("-fx-cursor: hand;");
        reviewsHB.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                if (reviewListView == null)
                    createListComments();

                showReviews();
            }
        });

        GridPane leftGP = new GridPane();
        leftGP.setPadding(new Insets(10));
        leftGP.setStyle("-fx-background-color: white; -fx-background-radius: 12 0 0 12;");
        leftGP.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints()
        );

        leftGP.setPrefWidth(300);

        GridPane ratGP = new GridPane();
        ratGP.getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints()
        );
        ratGP.getColumnConstraints().addAll(
                new ColumnConstraints(),
                new ColumnConstraints()
        );

        ratLB = new Label("0.0");
        GridPane.setRowSpan(ratLB, 2);
        GridPane.setValignment(ratLB, VPos.TOP);

        ratString = new Label("Неизвестно");
        GridPane.setColumnIndex(ratString, 1);
        GridPane.setValignment(ratString, VPos.BOTTOM);
        ratString.setPadding(new Insets(2, 5, 2, 5));

        countRev = new Label("Число оценок: ...");
        GridPane.setValignment(countRev, VPos.TOP);
        countRev.getStyleClass().add("hotel-count-rating-label");
        countRev.setPadding(new Insets(2, 5, 2, 5));
        GridPane.setColumnIndex(countRev, 1);
        GridPane.setRowIndex(countRev, 1);

        ratGP.getChildren().addAll(ratLB, ratString, countRev);

        leftGP.getChildren().add(ratGP);

        Button allReviewsBtn = new Button("Все отзывы");
        GridPane.setRowIndex(allReviewsBtn, 1);
        GridPane.setColumnSpan(allReviewsBtn, 2);
        GridPane.setValignment(allReviewsBtn, VPos.BOTTOM);
        allReviewsBtn.getStyleClass().add("select-button");
        GridPane.setMargin(allReviewsBtn, new Insets(10, 0, 0, 10));
        allReviewsBtn.setPrefWidth(280);
        allReviewsBtn.setOnAction(e -> {
            if (reviewListView == null)
                createListComments();

            showReviews();
        });

        leftGP.getChildren().add(allReviewsBtn);

        reviewsHB.getChildren().add(leftGP);

        VBox rightVB = new VBox();
        rightVB.setStyle("-fx-background-color: white; -fx-background-radius: 0 12 12 0;");
        rightVB.setPadding(new Insets(10));
        rightVB.prefWidthProperty().bind(reviewsHB.widthProperty().subtract(200));

        nameUser = new Label("Имя пользователя неизвестно");
        nameUser.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        reviewDate = new Label("00 месяц 0000");
        reviewDate.setStyle("-fx-text-fill: rgba(170, 170, 170);");

        reviewComment = new Label();
        reviewComment.setStyle("-fx-font-size: 14px");
        reviewComment.setWrapText(true);
        VBox.setMargin(reviewComment, new Insets(5, 0, 0, 0));

        rightVB.getChildren().addAll(nameUser, reviewDate, reviewComment);

        reviewsHB.getChildren().add(rightVB);

        rootVB.getChildren().add(reviewsHB);
    }

    private void createListComments() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();
        if(shadowPane == null) {
            shadowPane = new Pane();
            shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
            shadowPane.setVisible(false);
            overSP.getChildren().add(shadowPane);
        }

        closeCommentsBtn = new Button();
        closeCommentsBtn.setVisible(false);
        closeCommentsBtn.setPrefHeight(30);
        closeCommentsBtn.setPrefWidth(30);
        StackPane.setAlignment(closeCommentsBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeCommentsBtn, new Insets(20, 20, 0, 0));
        closeCommentsBtn.getStyleClass().add("close-button");
        closeCommentsBtn.setOnAction(event -> {
            hideReviews();
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeCommentsBtn.setGraphic(closeImg);

        overSP.getChildren().add(closeCommentsBtn);

        reviewListView = new ListView<>();
        reviewListView.setVisible(false);
        reviewListView.setCellFactory(cell -> new ReviewCell());
        reviewListView.getStyleClass().addAll("list-view", "scroll-pane");
        reviewListView.setSelectionModel(null);
        Platform.runLater(() -> {
            new HelpFullClass().scrollPaneAnimation(reviewListView);
        });

        reviewListView.getItems().addAll(new ReviewService().getAllReviewByHotelId(selectedHotel.getIdHotel()).stream()
                .filter(review -> review.getComment() != null).sorted((rev1, rev2)
                        -> rev2.getRating() - rev1.getRating()).toList());
        reviewListView.maxWidthProperty().bind(overSP.widthProperty().divide(1.3));
        reviewListView.maxHeightProperty().bind(overSP.heightProperty().subtract(60));

        overSP.getChildren().add(reviewListView);
    }

    private void hideReviews() {
        closeCommentsBtn.setVisible(false);
        reviewListView.setVisible(false);
        shadowPane.setVisible(false);
    }

    private void showReviews() {
        closeCommentsBtn.setVisible(true);
        reviewListView.setVisible(true);
        shadowPane.setVisible(true);

        reviewListView.getItems().clear();
        reviewListView.getItems().addAll(new ReviewService().getAllReviewByHotelId(selectedHotel.getIdHotel()).stream()
                .filter(review -> review.getComment() != null).sorted((rev1, rev2)
                        -> rev2.getRating() - rev1.getRating()).toList());
    }

    private void createListRooms() {
        Label countRoom = new Label();
        countRoom.setStyle("-fx-font-size:26px; -fx-font-weight: bold;");
        rootVB.getChildren().add(countRoom);

        ListView<Room> roomListView = new ListView<>();
        roomListView.setSelectionModel(null);
        roomListView.getStyleClass().add("list-view");
        roomListView.setCellFactory(cell -> new RoomCell());

        rootVB.getChildren().add(roomListView);

        roomListView.getItems().addAll(new RoomService().getAllRowByHotelId());
    }

    private void createMap() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        if(shadowPane == null) {
            shadowPane = new Pane();
            shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
            shadowPane.setVisible(false);
            overSP.getChildren().add(shadowPane);
        }

        closeBtn = new Button();
        closeBtn.setVisible(false);
        closeBtn.setPrefHeight(30);
        closeBtn.setPrefWidth(30);
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(20, 20, 0, 0));
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(event -> {
            hideMap();
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeBtn.setGraphic(closeImg);

        overSP.getChildren().add(closeBtn);

        webView = new WebView();
        webView.setVisible(false);
        webView.setUserData("mapWebView");

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

        overSP.getChildren().add(webView);
    }

    private void showMap() {
        shadowPane.setVisible(true);
        closeBtn.setVisible(true);
        webView.setVisible(true);

        WebEngine webEngine = webView.getEngine();

        webEngine.load("http://localhost:8080/map.html");

        // После успешной загрузки вызываем initMap
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                String cords = String.format("%.6f", selectedHotel.getLatitude()).replace(",", ".")
                        + "," + String.format("%.6f", selectedHotel.getLongitude()).replace(",", ".");
                webEngine.executeScript("initMap('" + cords + "', '" + selectedHotel.getHotelName()
                        +  "', '" + selectedHotel.getHotelAddress() + "');");
            }
        });
    }

    private void hideMap() {
        shadowPane.setVisible(false);
        closeBtn.setVisible(false);
        webView.setVisible(false);
    }
}
