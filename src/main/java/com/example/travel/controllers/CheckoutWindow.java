package com.example.travel.controllers;


import com.example.travel.models.Room;
import com.example.travel.models.RoomFeature;
import com.example.travel.services.RoomFeatureRelationService;
import com.example.travel.util.HelpFullClass;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.travel.controllers.FilterWindow.ensureVisible;
import static com.example.travel.controllers.HotelCell.*;
import static com.example.travel.util.HelpFullClass.getRussianMonthName;
import static com.example.travel.util.ImageUtils.round;

public class CheckoutWindow extends ScrollPane {
    private Label infoLB, hotelNameLB, hotelCountStar, ratingLB, addressLabel;

    private double widthWin = 600.0;

    private Room room;

    private Pane shadowPane;
    private Button closeBtn;
    private WebView webView;

    private ScrollPane infoRoomScrollPane;

    private ImageView imageViewRoom;

    private Button prevImageBtn, nextImageBtn;

    private GridPane gridPane;

    private final CustomRadioParent customRadioParent = new CustomRadioParent();

    private final Map<VBox, CustomRadioButton> radioButtonMap = new HashMap<>();

    private int countNightI = 1;

    public CheckoutWindow(Room room) {
        this.room = room;
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        VBox parentVB = new VBox();
        parentVB.setStyle("-fx-background-color: rgba(230, 230, 230);");
        parentVB.setPadding(new Insets(15, 20, 15, 20));
        parentVB.setSpacing(10);
        parentVB.prefWidthProperty().bind(PopularDestinationsController.getOverlaySP().widthProperty().subtract(10));
        parentVB.prefHeightProperty().bind(PopularDestinationsController.getOverlaySP().heightProperty().subtract(10));

        setContent(parentVB);
        setFitToWidth(true);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("scroll-pane");
        //new HelpFullClass().scrollPaneAnimation(this);

        prefWidthProperty().bind(PopularDestinationsController.getOverlaySP().widthProperty());
        prefHeightProperty().bind(PopularDestinationsController.getOverlaySP().heightProperty());

        overSP.getChildren().add(this);

        HBox buttonHB = new HBox(10);
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
            if (e.getButton() == MouseButton.PRIMARY) {
                overSP.getChildren().remove(this);
            }
        });

        parentVB.getChildren().add(buttonHB);

        VBox paymentVB = new VBox();
        parentVB.getChildren().add(paymentVB);

        Label paymentLB = new Label("Оплата");
        paymentLB.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        infoLB = new Label();
        infoLB.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(180, 180, 180);");
        LocalDate startDate;
        LocalDate endDate;
        if (CustomCalendar.getStartSelectedBtn() != null && CustomCalendar.getEndSelectedBtn() != null) {
            startDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            endDate = (LocalDate) CustomCalendar.getEndSelectedBtn().getUserData();
        } else if (CustomCalendar.getStartSelectedBtn() != null) {
            startDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            endDate = startDate.plusDays(1);
        } else {
            startDate = LocalDate.now().plusDays(room.getHotel().getDaysFromApplicationToCheckIn());
            endDate = startDate.plusDays(1);
        }

        String countGustsStr;
        int total = NumberOfGuestsController.totalStatic;
        if (total == 1)
            countGustsStr = "1 гость";
        else if (total >= 2 && total <= 4)
            countGustsStr = total + " гостя";
        else
            countGustsStr = total + " гостей";
        infoLB.setText(getTextByUserData(startDate) + " - " + getTextByUserData(endDate) + " · "
                + countNight(startDate, endDate) + " · " + countGustsStr);

        paymentVB.getChildren().addAll(paymentLB, infoLB);

        HBox osnHB = new HBox(20);
        parentVB.getChildren().add(osnHB);

        VBox leftVB = new VBox();
        leftVB.setStyle("-fx-background-color: rgba(230, 230, 230);");
        leftVB.setSpacing(10);
        HBox.setHgrow(leftVB, Priority.ALWAYS);
        osnHB.getChildren().add(leftVB);

        VBox hotelInfoHeader = new VBox();
        hotelInfoHeader.setPadding(new Insets(15, 20, 15, 20));
        hotelInfoHeader.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        leftVB.getChildren().add(hotelInfoHeader);

        HBox hotelNameHB = new HBox();

        hotelNameLB = new Label(room.getHotel().getHotelName());
        hotelNameLB.getStyleClass().add("hotel-name-label");

        hotelCountStar = new Label(" " + room.getHotel().getCountStars());
        hotelCountStar.getStyleClass().add("hotel-name-label");

        ImageView starImageView = new ImageView(STAR_IMAGE);
        starImageView.setFitHeight(15);
        starImageView.setFitWidth(15);
        starImageView.setPreserveRatio(true);

        hotelNameHB.getChildren().addAll(hotelNameLB, hotelCountStar, starImageView);

        hotelInfoHeader.getChildren().add(hotelNameHB);

        HBox ratingAndAddressHotel = new HBox(10);
        ratingAndAddressHotel.setAlignment(Pos.TOP_LEFT);

        ratingLB = new Label(String.format("%.1f", room.getHotel().getHotelRating()));
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

        addressLabel = new Label(room.getHotel().getHotelAddress());
        addressLabel.setStyle("-fx-text-fill: #b40acf");

        addressHB.getChildren().addAll(mapIV, addressLabel);

        addressHB.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (room.getHotel().getLongitude() != null && room.getHotel().getLatitude() != null) {
                    if (overSP.getChildren().stream().filter(node ->
                            node.getUserData() != null && node.getUserData().equals("mapWebViewCheckoutWin")).toList().isEmpty())
                        createMap();
                    showMap();
                } else System.out.println("Координаты пусты");
            }
        });

        ratingAndAddressHotel.getChildren().addAll(ratingLB, addressHB);

        hotelInfoHeader.getChildren().add(ratingAndAddressHotel);

        HBox roomHB = new HBox(10);
        roomHB.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        leftVB.getChildren().add(roomHB);

        StackPane photosStackPane = new StackPane();
        photosStackPane.setPrefWidth(200);
        photosStackPane.setMaxWidth(200);
        photosStackPane.setMaxHeight(200);
        photosStackPane.setPrefHeight(200);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setImage(room.getImageByNumber(0));
        round(imageView, 30, 30, 30, 30);
        imageView.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (infoRoomScrollPane == null)
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

        Label countImageLB = new Label(String.format("%d фото", room.getRoomPhotos().length));
        countImageLB.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        countPhotoHB.getChildren().add(countImageLB);

        photosStackPane.getChildren().add(countPhotoHB);

        roomHB.getChildren().add(photosStackPane);

        VBox roomVB = new VBox(10);
        roomHB.getChildren().add(roomVB);
        roomVB.setPadding(new Insets(10));
        HBox.setHgrow(roomVB, Priority.ALWAYS);

        Label roomNameLB = new Label(room.getRoomName());
        roomNameLB.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        roomVB.getChildren().add(roomNameLB);

        HBox checkInAndCheckOutHB = new HBox(5);
        roomVB.getChildren().add(checkInAndCheckOutHB);

        checkInAndCheckOutHB.getChildren().addAll(createShapeCheckInDates("Заселение", startDate, room.getCheckInTime())
                , createShapeCheckInDates("Выезд", endDate, room.getCheckOutTime()));

        VBox bottomVB = new VBox();
        bottomVB.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(bottomVB, Priority.ALWAYS);
        roomVB.getChildren().add(bottomVB);

        HBox aboutTheRoomHB = new HBox(3);
        aboutTheRoomHB.setStyle("-fx-background-color: rgba(255,230,255, 0.8); -fx-background-radius: 12px; -fx-cursor: hand;");
        aboutTheRoomHB.setPadding(new Insets(3, 10, 3, 10));
        aboutTheRoomHB.setAlignment(Pos.CENTER_LEFT);
        aboutTheRoomHB.setPrefHeight(30);
        aboutTheRoomHB.setMaxWidth(USE_PREF_SIZE);
        aboutTheRoomHB.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (infoRoomScrollPane == null)
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

        bottomVB.getChildren().add(aboutTheRoomHB);

        int i = 1;
        for (; i <= NumberOfGuestsController.adultsCountStatic; i++)
            leftVB.getChildren().add(createGuestProfile(i, "Взрослый"));

        for (; i <= NumberOfGuestsController.totalStatic; i++)
            leftVB.getChildren().add(createGuestProfile(i, "Ребёнок"));

        updateBuyer();

        VBox rightVB = new VBox(10);
        rightVB.setMaxHeight(USE_PREF_SIZE);
        rightVB.setPrefWidth(350);
        rightVB.setMinWidth(350);
        rightVB.setPadding(new Insets(15, 20, 15, 20));
        rightVB.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
        osnHB.getChildren().add(rightVB);

        GridPane topGP = new GridPane();
        topGP.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
        topGP.getRowConstraints().addAll(new RowConstraints(), new RowConstraints());
        topGP.setPrefWidth(310);
        topGP.setMinWidth(310);
        topGP.setMaxWidth(310);
        rightVB.getChildren().add(topGP);

        Label resLB = new Label("Итого");
        resLB.setStyle("-fx-font-weight: bold; -fx-font-size: 22px;");
        GridPane.setValignment(resLB, VPos.CENTER);
        GridPane.setHalignment(resLB, HPos.LEFT);
        topGP.getChildren().add(resLB);

        HBox discountPriceHB = new HBox(2);
        GridPane.setColumnIndex(discountPriceHB, 1);
        discountPriceHB.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(discountPriceHB, Priority.ALWAYS);
        topGP.getChildren().add(discountPriceHB);

        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (int j = String.format("%d", Math.round(total * room.getRoomPrice() * countNightI)).length() - 1; j >= 0; j--) {
            stringBuilder.append(String.format("%d", Math.round(total * room.getRoomPrice() * countNightI)).charAt(j));
            count++;
            if (count % 3 == 0 && i != 0) {
                stringBuilder.append(' ');
            }
        }
        String resultPrice = stringBuilder.reverse().toString();

        Label actualMinPriceLB = new Label(resultPrice);
        actualMinPriceLB.getStyleClass().add("room-small-price");
        actualMinPriceLB.setStyle("-fx-text-fill: rgba(50, 50, 50); -fx-font-size: 21px;");

        ImageView imageRuble = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black-ruble.png"))));
        imageRuble.setFitWidth(22);
        imageRuble.setFitHeight(22);
        imageRuble.setPreserveRatio(true);

        discountPriceHB.getChildren().addAll(actualMinPriceLB, imageRuble);

        String[] massStr = infoLB.getText().split(" · ");
        Label infLb = new Label(massStr[1] + ", " + massStr[2]);
        infLb.setStyle(infoLB.getStyle());
        GridPane.setValignment(infLb, VPos.CENTER);
        GridPane.setHalignment(infLb, HPos.LEFT);
        GridPane.setRowIndex(infLb, 1);
        topGP.getChildren().add(infLb);

        HBox discountPriceHB2 = new HBox(2);
        GridPane.setColumnIndex(discountPriceHB2, 1);
        GridPane.setRowIndex(discountPriceHB2, 1);
        discountPriceHB2.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(discountPriceHB2, Priority.ALWAYS);
        topGP.getChildren().add(discountPriceHB2);

        Label actualMinPriceLB2 = new Label(resultPrice);
        actualMinPriceLB2.getStyleClass().add("room-small-price");
        actualMinPriceLB2.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: normal;");

        ImageView imageRuble2 = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black-ruble.png"))));
        imageRuble2.setFitWidth(16);
        imageRuble2.setFitHeight(16);
        imageRuble2.setPreserveRatio(true);

        discountPriceHB2.getChildren().addAll(actualMinPriceLB2, imageRuble2);

        HBox paymentHB = new HBox(10);
        paymentHB.setStyle("-fx-background-color: rgba(245,245,245); -fx-background-radius: 30px;");
        VBox.setMargin(paymentHB, new Insets(30, 0, 10, 0));
        paymentHB.setPadding(new Insets(7, 15, 7, 15));
        paymentHB.setAlignment(Pos.CENTER_LEFT);
        rightVB.getChildren().add(paymentHB);

        ImageView img = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/black-payment.png"))));
        img.setFitWidth(28);
        img.setFitHeight(28);
        img.setPreserveRatio(true);
        paymentHB.getChildren().add(img);

        Label label = new Label("Оплата банковской картой");
        label.setStyle("-fx-font-size: 15px;");
        paymentHB.getChildren().add(label);

        Button paymentBtn = new Button("Оплатить");
        paymentBtn.setPrefWidth(310);
        paymentBtn.getStyleClass().add("show-result-button");
        paymentBtn.setOnAction(e -> {

        });

        rightVB.getChildren().add(paymentBtn);

        HBox bottomHB = new HBox();
        rightVB.getChildren().add(bottomHB);

        CustomCheckButton customCheckButton = new CustomCheckButton("Я согласен с Единым регламентом сервиса Wildberries Travel и ознакомлен с Политикой обработки персональных данных", null);
        customCheckButton.fire();
        customCheckButton.addSecondAction(e -> {
            paymentBtn.setDisable(!customCheckButton.isSelected());
        });
        bottomHB.getChildren().add(customCheckButton);

        TextFlow textFlow = new TextFlow();
        textFlow.setMaxHeight(50);
        textFlow.setMinHeight(50);
        textFlow.setPrefHeight(50);
        customCheckButton.updateText(textFlow);

        Text prefix = new Text("Я согласен с ");
        prefix.getStyleClass().add("hint-label-registration-text");

        Text ruleLink = new Text("Единым регламентом сервиса Travel");
        ruleLink.getStyleClass().add("documents-button-text");
        ruleLink.setUnderline(true);
        ruleLink.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                HelpFullClass.openWebPage("https://zelmex.ru/");
                e.consume();
            }
        });

        ruleLink.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                e.consume();
            }
        });

        Text middle = new Text(" и ознакомлен с ");
        middle.getStyleClass().add("hint-label-registration-text");

        Text policyLink = new Text("Политикой обработки персональных данных");
        policyLink.getStyleClass().add("documents-button-text");
        policyLink.setUnderline(true);
        policyLink.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                HelpFullClass.openWebPage("https://zelmex.ru/");
                e.consume();
            }
        });

        policyLink.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                e.consume();
            }
        });

        textFlow.getChildren().addAll(prefix, ruleLink, middle, policyLink);
    }

    private void createMap() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        if (shadowPane == null) {
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
        webView.setUserData("mapWebViewCheckoutWin");

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

        // После успешной загрузки вызываем initMap
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                String cords = String.format("%.6f", room.getHotel().getLatitude()).replace(",", ".")
                        + "," + String.format("%.6f", room.getHotel().getLongitude()).replace(",", ".");
                webEngine.executeScript("initMap('" + cords + "', '" + room.getHotel().getHotelName()
                        + "', '" + room.getHotel().getHotelAddress() + "');");
            }
        });
    }

    private void hideMap() {
        shadowPane.setVisible(false);
        closeBtn.setVisible(false);
        webView.setVisible(false);
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

        overSP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!infoRoomScrollPane.isVisible()) return;

            Point2D pointInWindow = infoRoomScrollPane.screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && infoRoomScrollPane.contains(pointInWindow))
                return;
            else
                hide();
        });
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

    private void updateVisibleButton() {
        if (room == null) return;
        int index = room.getCurrentImageIndex();
        int max = room.getRoomPhotos().length - 1;
        prevImageBtn.setVisible(index > 0);
        nextImageBtn.setVisible(index < max);

        imageViewRoom.setImage(room.getImageByNumber(room.getCurrentImageIndex()));

        imageViewRoom.requestFocus();
    }

    private static VBox createShapeCheckInDates(String action, LocalDate date, LocalTime checkTime) {
        VBox resVB = new VBox();
        resVB.setPrefWidth(200);
        resVB.setPadding(new Insets(5, 10, 5, 10));
        resVB.setStyle("-fx-background-color: rgba(240, 240, 240); -fx-background-radius: 12px;");

        Label actionLB = new Label(action);
        actionLB.setStyle("-fx-text-fill: rgba(180, 180, 180); -fx-font-weight: bold;");
        resVB.getChildren().add(actionLB);

        Label infoActionLB = new Label(getTextByUserData(date) + ", "
                + checkTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        infoActionLB.setStyle("-fx-font-size: 18px;");
        resVB.getChildren().add(infoActionLB);

        return resVB;
    }

    private static String getTextByUserData(LocalDate date) {
        return date.getDayOfMonth() + " " + getRussianMonthName(date.getMonthValue()).substring(0, 3).toLowerCase();
    }

    private String countNight(LocalDate startDate, LocalDate endDate) {
        countNightI = 0;

        if (startDate.getYear() == endDate.getYear())
            countNightI = endDate.getDayOfYear() - startDate.getDayOfYear();
        else
            countNightI = startDate.lengthOfYear() - startDate.getDayOfYear() + endDate.getDayOfYear();

        if (countNightI != 11 && String.format("%d", countNightI).endsWith("1"))
            return String.format("%d ночь", countNightI);

        if (String.format("%d", countNightI).endsWith("2") || String.format("%d", countNightI).endsWith("3") || String.format("%d", countNightI).endsWith("4"))
            return String.format("%d ночи", countNightI);

        return String.format("%d ночей", countNightI);
    }

    private Node createGuestProfile(int countGuest, String typeGuest) {
        VBox rootVB = new VBox();
        rootVB.setStyle("-fx-background-color: rgba(245, 245, 245); -fx-background-radius: 15px;");

        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white; -fx-background-radius: 15px;");
        gridPane.setPadding(new Insets(20));
        rootVB.getChildren().add(gridPane);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        gridPane.getColumnConstraints().addAll(col1, col2);

        for (int i = 1; i <= 6; i++)
            gridPane.getRowConstraints().add(new RowConstraints());

        HBox topLeftHB = new HBox(8);
        topLeftHB.setAlignment(Pos.CENTER_LEFT);

        Label countGuestLB = new Label(String.format("%d", countGuest));
        countGuestLB.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 90px; -fx-font-size: 16px; -fx-font-weight: bold;");
        topLeftHB.getChildren().add(countGuestLB);
        countGuestLB.setPrefWidth(24.5);
        countGuestLB.setPrefHeight(18);
        countGuestLB.setAlignment(Pos.CENTER);


        Label typeGuestLB = new Label(typeGuest);
        typeGuestLB.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        topLeftHB.getChildren().add(typeGuestLB);

        if (typeGuest.equals("Взрослый")) {
            VBox topVB = new VBox(3);
            topVB.getChildren().add(topLeftHB);

            CustomRadioButton btn = new CustomRadioButton(customRadioParent, countGuest == 1);
            btn.setTextBtn("Является покупателем");
            btn.addStyleButtonText("bold-text");
            btn.addSecondAction(e -> {
                updateBuyer();
            });

            radioButtonMap.put(rootVB, btn);

            topVB.getChildren().add(btn);

            gridPane.getChildren().add(topVB);
        } else {
            gridPane.getChildren().add(topLeftHB);
        }

        Button throwOff = new Button("Сбросить");
        throwOff.setUserData(rootVB);
        gridPane.getChildren().add(throwOff);
        throwOff.getStyleClass().add("throw-off");
        GridPane.setColumnIndex(throwOff, 1);
        GridPane.setHalignment(throwOff, HPos.RIGHT);
        throwOff.setOnAction(e -> {
            for (Node node : ((Pane) throwOff.getUserData()).getChildren()) {
                if (node instanceof Pane) {
                    for (Node childPane : ((Pane) node).getChildren()) {
                        if (childPane instanceof Pane) {
                            for (Node childNode : ((Pane) childPane).getChildren()) {
                                if (childNode instanceof TextField && ((TextField) childNode).isEditable()) {
                                    childNode.setStyle("");
                                    ((TextField) childNode).setText("");
                                    if(childNode.getUserData() != null)
                                        ((Label) childNode.getUserData()).setText("");
                                } else if (childNode instanceof ComboBox<?>) {
                                    ((ComboBox<?>) childNode).setValue(null);
                                    if(childNode.getUserData() != null)
                                        ((Label) childNode.getUserData()).setText("");
                                }
                            }
                        }
                    }
                }
            }
        });

        TextField firstNameTF = new TextField();
        setStateInputControl(0, 1, firstNameTF, "Фамилия");

        VBox inputControlVB = new VBox();
        GridPane.setRowIndex(inputControlVB, 2);
        GridPane.setColumnIndex(inputControlVB, 0);
        GridPane.setMargin(inputControlVB, new Insets(10));
        inputControlVB.setAlignment(Pos.BOTTOM_CENTER);
        gridPane.getChildren().add(inputControlVB);

        Label hintLB = new Label("Пол");
        hintLB.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintLB, new Insets(0, 0, 0, 5));
        hintLB.prefWidthProperty().bind(firstNameTF.widthProperty());
        hintLB.setVisible(false);
        hintLB.setManaged(false);

        ComboBox<String> genderCB = new ComboBox<>();
        genderCB.setUserData(hintLB);
        genderCB.setPromptText("Пол");
        genderCB.setEditable(true);
        genderCB.getEditor().setEditable(false);
        genderCB.getStyleClass().setAll("combo-box-guest-state");
        genderCB.prefWidthProperty().bind(firstNameTF.widthProperty());
        genderCB.getEditor().setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (genderCB.getValue() == null) {
                    genderCB.getEditor().setText(genderCB.getPromptText());
                    genderCB.getEditor().setStyle("-fx-text-fill: rgba(180,180,180);");
                }

                if (genderCB.isShowing()) {
                    genderCB.hide();
                } else {
                    genderCB.show();
                }
                e.consume();
            }
        });
        genderCB.getItems().addAll("Мужской", "Женский");
        genderCB.valueProperty().addListener((obs, old, val) -> {
            if (val == null) {
                genderCB.getEditor().setText(genderCB.getPromptText());
                genderCB.getEditor().setStyle("-fx-text-fill: rgba(180,180,180);");
            } else {
                genderCB.getEditor().setText(val);
                genderCB.getEditor().setStyle("-fx-text-fill: black;");
            }
        });
        genderCB.setValue(null);

        inputControlVB.getChildren().add(genderCB);

        hintLB.textProperty().addListener((ob, oldV, newV) -> {
            if (newV.isEmpty()) {
                hintLB.setVisible(false);
                hintLB.setManaged(false);
                hintLB.setStyle("");
                genderCB.setStyle("");
            } else {
                hintLB.setVisible(true);
                hintLB.setManaged(true);
                hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
                genderCB.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });

        TextField citizenshipTF = new TextField();
        setStateInputControl(0, 3, citizenshipTF, "Гражданство");
        citizenshipTF.setText("Россия");
        citizenshipTF.setEditable(false);

        TextField passportTF = new TextField();
        setStateInputControl(0, 4, passportTF, "Серия и номер");
        passportTF.setOnMouseEntered(e -> {
            passportTF.setPromptText("__ _______");
        });
        passportTF.setOnMouseExited(e -> {
            passportTF.setPromptText("Серия и номер");
        });

        TextField nameTF = new TextField();
        setStateInputControl(1, 1, nameTF, "Имя");

        TextField birthdayTF = new TextField();
        setStateInputControl(1, 2, birthdayTF, "Дата рождения");
        birthdayTF.setOnMouseEntered(e -> {
            birthdayTF.setPromptText("__.__.____");
        });
        birthdayTF.setOnMouseExited(e -> {
            birthdayTF.setPromptText("Дата рождения");
        });

        TextField docTypeTF = new TextField();
        setStateInputControl(1, 3, docTypeTF, "Тип документа");
        docTypeTF.setText("Паспорт гражданина РФ");
        docTypeTF.setEditable(false);

        TextField validityPeriodTF = new TextField();
        setStateInputControl(1, 4, validityPeriodTF, "Срок действия");
        validityPeriodTF.setOnMouseEntered(e -> {
            validityPeriodTF.setPromptText("__.__.____");
        });
        validityPeriodTF.setOnMouseExited(e -> {
            validityPeriodTF.setPromptText("Срок действия");
        });

        return rootVB;
    }

    private void setStateInputControl(int col, int row, TextInputControl node, String promptText) {
        VBox inputControlVB = new VBox();
        inputControlVB.setAlignment(Pos.BOTTOM_LEFT);
        GridPane.setRowIndex(inputControlVB, row);
        GridPane.setColumnIndex(inputControlVB, col);
        GridPane.setMargin(inputControlVB, new Insets(10));

        Label hintLB = new Label();
        hintLB.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintLB, new Insets(0, 0, 0, 5));
        hintLB.prefWidthProperty().bind(node.widthProperty());
        hintLB.textProperty().addListener((ob, oldV, newV) -> {
            if (newV.isEmpty()) {
                hintLB.setStyle("");
                hintLB.setManaged(false);
                hintLB.setVisible(false);
                node.setStyle("");
            } else {
                hintLB.setManaged(true);
                hintLB.setVisible(true);
                hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
                node.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });
        inputControlVB.getChildren().add(hintLB);
        hintLB.setVisible(false);
        hintLB.setManaged(false);

        node.setPromptText(promptText);
        node.setUserData(hintLB);
        node.getStyleClass().add("input-guest-state-control");
        node.textProperty().addListener((ob, oldV, newV) -> {
            if (!newV.isEmpty())
                node.setStyle("-fx-text-fill: black;");
            else {
                hintLB.setText("Поле обязательно для заполнения");
            }
        });
        inputControlVB.getChildren().add(node);

        gridPane.getChildren().add(inputControlVB);
    }

    private void updateBuyer() {
        for (Map.Entry<VBox, CustomRadioButton> entry : radioButtonMap.entrySet()) {
            VBox key = entry.getKey();
            key.getChildren().removeIf(node -> node instanceof HBox);

            if (entry.getValue().getSelected()) {
                HBox bottomHB = new HBox(10);
                bottomHB.setPadding(new Insets(25));
                key.getChildren().add(bottomHB);

                VBox inputControlVB = new VBox();
                inputControlVB.setAlignment(Pos.BOTTOM_CENTER);
                inputControlVB.prefWidthProperty().bind(bottomHB.widthProperty().divide(2).subtract(5));
                bottomHB.getChildren().add(inputControlVB);

                Label hintLB = new Label();
                hintLB.getStyleClass().add("hint-label-registration");
                VBox.setMargin(hintLB, new Insets(0, 0, 0, 5));
                hintLB.prefWidthProperty().bind(inputControlVB.widthProperty());
                inputControlVB.getChildren().add(hintLB);
                hintLB.setVisible(false);
                hintLB.setManaged(false);

                TextField emailTF = new TextField();
                emailTF.setPromptText("Email *");
                emailTF.getStyleClass().add("input-guest-state-control");
                emailTF.prefWidthProperty().bind(inputControlVB.widthProperty());
                emailTF.textProperty().addListener((ob, oldV, newV) -> {
                    if (!newV.isEmpty())
                        emailTF.setStyle("-fx-text-fill: black;");
                    else
                        emailTF.setStyle("");
                });
                inputControlVB.getChildren().add(emailTF);

                hintLB.textProperty().addListener((ob, oldV, newV) -> {
                    if (newV.isEmpty()) {
                        hintLB.setStyle("");
                        hintLB.setManaged(false);
                        hintLB.setVisible(false);
                        emailTF.setStyle("");
                    } else {
                        hintLB.setManaged(true);
                        hintLB.setVisible(true);
                        hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
                        emailTF.setStyle("-fx-border-color: rgba(130,0,0);");
                    }
                });

                VBox inputControlVB2 = new VBox();
                inputControlVB2.setAlignment(Pos.BOTTOM_CENTER);
                inputControlVB2.prefWidthProperty().bind(bottomHB.widthProperty().divide(2).subtract(5));
                bottomHB.getChildren().add(inputControlVB2);

                Label hintLB2 = new Label();
                hintLB2.getStyleClass().add("hint-label-registration");
                VBox.setMargin(hintLB2, new Insets(0, 0, 0, 5));
                hintLB2.prefWidthProperty().bind(inputControlVB2.widthProperty());
                inputControlVB2.getChildren().add(hintLB2);
                hintLB2.setVisible(false);
                hintLB2.setManaged(false);

                TextField phoneTF = new TextField();
                phoneTF.setPromptText("Номер телефона");
                phoneTF.getStyleClass().add("input-guest-state-control");
                phoneTF.prefWidthProperty().bind(inputControlVB2.widthProperty());
                phoneTF.textProperty().addListener((ob, oldV, newV) -> {
                    if (!newV.isEmpty())
                        phoneTF.setStyle("-fx-text-fill: black;");
                    else
                        phoneTF.setStyle("");
                });
                inputControlVB2.getChildren().add(phoneTF);

                hintLB2.textProperty().addListener((ob, oldV, newV) -> {
                    if (newV.isEmpty()) {
                        hintLB2.setManaged(false);
                        hintLB2.setStyle("");
                        phoneTF.setStyle("");
                    } else {
                        hintLB2.setManaged(true);
                        hintLB2.setVisible(true);
                        hintLB2.setStyle("-fx-text-fill: rgba(130,0,0);");
                        phoneTF.setStyle("-fx-border-color: rgba(130,0,0);");
                    }
                });
            }
        }
    }
}
