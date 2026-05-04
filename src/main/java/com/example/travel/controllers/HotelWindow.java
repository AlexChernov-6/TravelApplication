package com.example.travel.controllers;

import com.example.travel.TravelApplication;
import com.example.travel.models.*;
import com.example.travel.services.DirectionService;
import com.example.travel.services.ReviewService;
import com.example.travel.services.RoomFeatureRelationService;
import com.example.travel.services.RoomService;
import com.example.travel.util.HelpFullClass;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.example.travel.controllers.FilterWindow.ensureVisible;
import static com.example.travel.controllers.HotelCell.*;
import static com.example.travel.controllers.PopularDestinationsController.*;
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

    private Label ratLB, ratString, countRev, nameUser, reviewDate, reviewComment, countRoom;

    private ListView<Review> reviewListView;
    private VBox roomsContainer;

    private SorterWindow sorterWindow;
    private SortedList<Review> sortedList;
    private ObservableList<Review> roomObservableList;

    private VBox contVB;

    public HotelWindow() {
        rootVB = new VBox(15);
        rootVB.setPadding(new Insets(0, 0, 20, 0));

        setContent(rootVB);

        createBackBtn();

        createHotelInformation();

        createReviews();

        Platform.runLater(this::createListComments);

        createListRooms();
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

        // Заполнение номеров
        roomsContainer.getChildren().clear();

        List<Room> rooms = new RoomService().getAllRowByHotelId(selectedHotel.getIdHotel()).stream()
                .filter(room -> room.getRoomSleepingPlaces() >= NumberOfGuestsController.totalStatic
                        && (room.getRoomPrice() >= (FilterWindow.fromPrice != null ? FilterWindow.fromPrice : 0)
                        && room.getRoomPrice() <= (FilterWindow.beforePrice != null ? FilterWindow.beforePrice : Math.round(new DirectionService()
                        .getMaxRoomPriceByDirectionId(oldPressedDirection.getIdDirection()))))
                        && checkPaymentMethod(room) && checkCancellation(room) && checkRoomFeature(room))
                .sorted((r1, r2) -> r1.getRoomSleepingPlaces() - r2.getRoomSleepingPlaces())
                .toList();

        for (Room room : rooms) {
            RoomCard roomCard = new RoomCard();
            roomCard.updateRoom(room);
            roomsContainer.getChildren().add(roomCard);
        }

        countRoom.setText("Найдено подходящих вариантов: " + rooms.size());
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
                Platform.runLater(() -> {
                    Double vPos = vPosScrollPaneWithHotelsLW.get(oldPressedDirection);
                    if (vPos != null) {
                        restoreScrollPosition(vPos);
                    }
                });
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
        chooseRoom.setPrefWidth(Double.MAX_VALUE);
        chooseRoom.getStyleClass().add("show-result-button");
        chooseRoom.setOnAction(e -> {
            ensureVisible(this, countRoom);
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

        TravelApplication.stageRoot.maximizedProperty().addListener((ob, oldV, newV) -> {
            Platform.runLater(() -> {
                double newVal = hotelInfoHeader.getWidth() - 30;
                viewingImages.setPrefWidth(newVal);
                viewingImages.setMaxWidth(newVal);
                viewingImages.setMinWidth(newVal);
            });
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

        VBox leftVB = new VBox(10);
        leftVB.setPadding(new Insets(10));
        leftVB.setStyle("-fx-background-color: white; -fx-background-radius: 12 0 0 12;");
        leftVB.setPrefWidth(200);

        HBox ratHB = new HBox();
        ratHB.setPrefWidth(180);

        ratLB = new Label("0.0");

        VBox countRewVB = new VBox();

        ratString = new Label("Неизвестно");
        ratString.setPadding(new Insets(2, 5, 2, 5));

        countRev = new Label("Число оценок: ...");
        countRev.getStyleClass().add("hotel-count-rating-label");
        countRev.setPadding(new Insets(2, 5, 2, 5));

        countRewVB.getChildren().addAll(ratString, countRev);

        ratHB.getChildren().addAll(ratLB, countRewVB);

        leftVB.getChildren().add(ratHB);

        Label allReviewsBtn = new Label("Все отзывы");
        allReviewsBtn.getStyleClass().add("select-button");
        allReviewsBtn.setPrefWidth(180);
        allReviewsBtn.setAlignment(Pos.CENTER);
        allReviewsBtn.setPadding(new Insets(7, 0, 7, 0));
        allReviewsBtn.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                if (reviewListView == null)
                    createListComments();

                showReviews();
                e.consume();
            }
        });

        leftVB.getChildren().add(allReviewsBtn);

        reviewsHB.getChildren().add(leftVB);

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

        contVB = new VBox(10);
        contVB.setVisible(false);
        contVB.maxWidthProperty().bind(overSP.widthProperty().divide(1.3));
        contVB.maxHeightProperty().bind(overSP.heightProperty().subtract(60));

        Button sortBtn = new Button();
        sortBtn.setPrefHeight(35);
        sortBtn.getStyleClass().add("custom-button");
        sortBtn.setPadding(new Insets(0));
        sortBtn.setOnAction(e -> {
            if (sorterWindow == null) {
                sorterWindow = new SorterWindow();
                sorterWindow.setShape(createShape());
                sorterWindow.show(sortBtn);
            } else {
                if (!sorterWindow.getPopup().isShowing())
                    sorterWindow.show(sortBtn);
                else
                    sorterWindow.hide();
            }
        });

        HBox backgroundHBSort = new HBox();
        backgroundHBSort.getStyleClass().add("sort-button-hbox");
        backgroundHBSort.setAlignment(Pos.CENTER);
        backgroundHBSort.setPadding(new Insets(10));

        ImageView imageSort = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/sort.png")))
        );
        imageSort.setFitHeight(20);
        imageSort.setFitWidth(20);
        imageSort.setPreserveRatio(true);

        backgroundHBSort.getChildren().add(imageSort);
        sortBtn.setGraphic(backgroundHBSort);

        contVB.getChildren().add(sortBtn);

        reviewListView = new ListView<>();
        VBox.setVgrow(reviewListView, Priority.ALWAYS);
        reviewListView.setCellFactory(cell -> new ReviewCell());
        reviewListView.getStyleClass().addAll("list-view", "scroll-pane");
        reviewListView.setSelectionModel(null);
        Platform.runLater(() -> {
            new HelpFullClass().scrollPaneAnimation(reviewListView);
        });

        roomObservableList = FXCollections.observableList(
                new ReviewService().getAllReviewByHotelId(selectedHotel.getIdHotel()));

        sortedList = new SortedList<>(roomObservableList, (rev1, rev2) -> rev2.getRating() - rev1.getRating());

        reviewListView.setItems(sortedList);

        contVB.getChildren().add(reviewListView);

        overSP.getChildren().add(contVB);

        overSP.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(!isVisible()) return;

            Point2D pointInSortBtn= sortBtn.screenToLocal(event.getScreenX(), event.getScreenY());
            Point2D pointInListView = reviewListView.screenToLocal(event.getScreenX(), event.getScreenY());

            if ((pointInSortBtn != null && sortBtn.contains(pointInSortBtn))
                    || (pointInListView != null && reviewListView.contains(pointInListView)))
                return;
            else
                if(sorterWindow != null) {
                    if (!sorterWindow.isVisible1())
                        hideReviews();
                    else
                        sorterWindow.setVisible1(false);
                } else
                    hideReviews();
        });
    }

    private void hideReviews() {
        closeCommentsBtn.setVisible(false);
        contVB.setVisible(false);
        shadowPane.setVisible(false);
    }

    private void showReviews() {
        closeCommentsBtn.setVisible(true);
        contVB.setVisible(true);
        shadowPane.setVisible(true);

        roomObservableList.setAll(new ReviewService().getAllReviewByHotelId(selectedHotel.getIdHotel()));
    }

    private void createListRooms() {
        countRoom = new Label();
        countRoom.setStyle("-fx-font-size:26px; -fx-font-weight: bold;");
        VBox.setMargin(countRoom, new Insets(0, 15, 0, 17));
        rootVB.getChildren().add(countRoom);

        roomsContainer = new VBox(15); // отступ между ячейками
        roomsContainer.setPadding(new Insets(0, 5, 0, 15));
        rootVB.getChildren().add(roomsContainer);
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

        overSP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!webView.isVisible()) return;

            Point2D pointInWindow = webView.screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && webView.contains(pointInWindow))
                return;
            else
                hideMap();
        });

        overSP.getChildren().add(webView);
    }

    private void showMap() {
        shadowPane.setVisible(true);
        closeBtn.setVisible(true);
        webView.setVisible(true);

        WebEngine webEngine = webView.getEngine();

        webEngine.load("http://localhost:8080/map.html");

        // После успешной загрузки вызываем initMa
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

    private boolean checkCancellation(Room room) {
        if (FilterWindow.mapCancellation.values().stream().filter(b -> b).toList().isEmpty())
            return true;

        return FilterWindow.mapCancellation.get(room.getRefundPolicy()) != null
                && FilterWindow.mapCancellation.get(room.getRefundPolicy());
    }

    private boolean checkPaymentMethod(Room room) {
        if (FilterWindow.mapPaymentMethods.values().stream().filter(b -> b).toList().isEmpty())
            return true;

        return FilterWindow.mapPaymentMethods.get(room.getPaymentMethod()) != null
                && FilterWindow.mapPaymentMethods.get(room.getPaymentMethod());
    }

    private boolean checkRoomFeature(Room room) {
        if(FilterWindow.roomFeatures.isEmpty())
            return  true;

        RoomFeatureRelationService service = new RoomFeatureRelationService();
        List<RoomFeature> allRoomFeatureByRoomId = service.getAllRoomFeatureByRoomId(room.getIdRooms());

        List<RoomFeature> result = allRoomFeatureByRoomId.stream()
                .filter(roomFeature -> FilterWindow.roomFeatures.contains(roomFeature.getFeatureName())).toList();

        return result.size() == FilterWindow.roomFeatures.size();
    }

    private Node[] createShape() {
        CustomRadioParent sortedReviews = new CustomRadioParent();

        CustomRadioButton higher = new CustomRadioButton(sortedReviews, true);
        higher.setTextBtn("Сначала более высокие");
        higher.addSecondAction(event -> {
            sortedList.setComparator((rev1, rev2) -> rev2.getRating() - rev1.getRating());
            sorterWindow.hide();
        });

        CustomRadioButton below = new CustomRadioButton(sortedReviews, false);
        below.setTextBtn("Сначала более низкие");
        below.addSecondAction(event -> {
            sortedList.setComparator((rev1, rev2) -> rev1.getRating() - rev2.getRating());
            sorterWindow.hide();
        });

        CustomRadioButton newer = new CustomRadioButton(sortedReviews, false);
        newer.setTextBtn("Сначала более новые");
        newer.addSecondAction(event -> {
            sortedList.setComparator((rev1, rev2) -> rev2.getCratedAt().compareTo(rev1.getCratedAt()));
            sorterWindow.hide();
        });

        return new Node[] {higher, below, newer};
    }
}