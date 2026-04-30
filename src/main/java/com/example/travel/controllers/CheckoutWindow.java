package com.example.travel.controllers;


import com.example.travel.models.*;
import com.example.travel.services.GuestService;
import com.example.travel.services.OrderService;
import com.example.travel.services.RoomFeatureRelationService;
import com.example.travel.services.UserService;
import com.example.travel.util.InputControlMaskFormatter;
import com.example.travel.util.HelpFullClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.example.travel.controllers.FilterWindow.ensureVisible;
import static com.example.travel.controllers.HotelCell.*;
import static com.example.travel.controllers.RegistrationWindow.*;
import static com.example.travel.util.HelpFullClass.getRussianMonthName;
import static com.example.travel.util.ImageUtils.round;

public class CheckoutWindow extends ScrollPane {
    private Label infoLB, hotelNameLB, hotelCountStar, ratingLB, addressLabel;

    private double widthWin = 600.0;

    private Room room;

    private Pane shadowPane;
    private Button closeBtn, closeBtnPayment;
    private WebView webView, paymentWebView;

    private ScrollPane infoRoomScrollPane;

    private ImageView imageViewRoom;

    private Button prevImageBtn, nextImageBtn;

    private GridPane gridPane;

    private final CustomRadioParent customRadioParent = new CustomRadioParent();

    private final Map<VBox, CustomRadioButton> radioButtonMap = new HashMap<>();

    private final List<Node> inputControls = new ArrayList<>();

    private int countNightI = 1;

    private Order order;

    private boolean engineIsEmpty = true;

    private List<Guest> guests = new ArrayList<>();

    private String buyerEmail, buyerPhone;

    private LocalDate startDate;
    private LocalDate endDate;
    private double orderCost;

    private ScheduledExecutorService sheduler;

    private Map<Node, String> passpertDataMap = new HashMap<>();

    private ScheduledFuture<?> resetPaymentTask;
    private ChangeListener<Worker.State> loadListener;

    private String enteredEmail, enteredPhone;

    private enum InputControlContext {
        FIRST_NAME_OR_NAME,
        BIRTHDAY,
        PASSPORT,
        VALID_UNTIL
    }

    public CheckoutWindow(Room room) {
        this.room = room;
        StackPane overSP = PopularDestinationsController.getOverlaySP();
        sheduler = Executors.newSingleThreadScheduledExecutor();

        VBox parentVB = new VBox();
        parentVB.setStyle("-fx-background-color: rgba(230, 230, 230);");
        parentVB.setPadding(new Insets(15, 20, 15, 20));
        parentVB.setSpacing(10);
        parentVB.prefWidthProperty().bind(PopularDestinationsController.getOverlaySP().widthProperty().subtract(10));
        parentVB.prefHeightProperty().bind(PopularDestinationsController.getOverlaySP().heightProperty().subtract(10));

        setContent(parentVB);
        setFitToWidth(true);
        setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollBarPolicy.NEVER);
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
        orderCost = total * room.getRoomPrice() * countNightI;
        for (int j = String.format("%d", Math.round(orderCost)).length() - 1; j >= 0; j--) {
            stringBuilder.append(String.format("%d", Math.round(orderCost)).charAt(j));
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
            for (Node node : inputControls) {
                if (node instanceof ComboBox<?>) {
                    if (((ComboBox<?>) node).getValue() == null || ((ComboBox<?>) node).getValue().equals("Пол"))
                        ((Label) node.getUserData()).setText("Поле обязательно для заполнения");
                } else if (node instanceof TextField) node.requestFocus();
            }
            label.requestFocus();
            for (Node node : inputControls) {
                if (node instanceof TextField && ((TextField) node).getText().contains("_")) {
                    ((Label) node.getUserData()).setText("Укажите достоверные данные");
                    ensureVisible(this, node);
                    return;
                }
                if (((Label) node.getUserData()).isVisible()) {
                    ensureVisible(this, node);
                    return;
                }
            }

            if (CONFIG_MANAGER.getUserId() != 0) {
                if (paymentWebView == null)
                    createPaymentWin();
                showPaymentWin(orderCost, "Путёвка в отель \"" + room.getHotel().getHotelName() + "\"");
            } else {
                RegistrationWindow registrationWindow = new RegistrationWindow();
                registrationWindow.show();
            }
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
        infoRoomScrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        infoRoomScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
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
        List<Node> inputControlsForBtn = new ArrayList<>();
        rootVB.setUserData(inputControlsForBtn);
        rootVB.setStyle("-fx-background-color: rgba(245, 245, 245); -fx-background-radius: 15px;");

        Guest guest = new Guest();
        guests.add(guest);

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
                for (Guest guest1 : guests)
                    guest1.setBuyerBoolean(false);

                guest.setBuyerBoolean(true);
                updateBuyer();
            });

            if (countGuest == 1)
                guest.setBuyerBoolean(true);

            radioButtonMap.put(rootVB, btn);

            topVB.getChildren().add(btn);

            gridPane.getChildren().add(topVB);
        } else {
            gridPane.getChildren().add(topLeftHB);
        }

        Button throwOff = new Button("Сбросить");
        gridPane.getChildren().add(throwOff);
        throwOff.getStyleClass().add("throw-off");
        GridPane.setColumnIndex(throwOff, 1);
        GridPane.setHalignment(throwOff, HPos.RIGHT);
        throwOff.setOnAction(e -> {
            for (Node childNode : inputControlsForBtn) {
                if (childNode instanceof TextField && ((TextField) childNode).isEditable()) {
                    ((TextField) childNode).setText("");
                    if (childNode.getUserData() != null)
                        ((Label) childNode.getUserData()).setText("");
                } else if (childNode instanceof ComboBox<?>) {
                    ((ComboBox<?>) childNode).setValue(null);
                    if (childNode.getUserData() != null)
                        ((Label) childNode.getUserData()).setText("");
                }
            }
        });

        TextField firstNameTF = new TextField();
        setStateInputControl(0, 1, firstNameTF, "Фамилия", InputControlContext.FIRST_NAME_OR_NAME);
        inputControls.add(firstNameTF);
        inputControlsForBtn.add(firstNameTF);
        firstNameTF.textProperty().addListener((ob, oldV, newV) -> {
            guest.setGuestFirstName(newV);
        });

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
        inputControlVB.getChildren().add(hintLB);

        VBox inputControlSecondVB = new VBox();
        inputControlSecondVB.getStyleClass().add("input-guest-state-control");
        inputControlVB.getChildren().add(inputControlSecondVB);

        ComboBox<String> genderCB = new ComboBox<>();
        genderCB.setUserData(hintLB);
        genderCB.setPromptText("Пол");
        genderCB.setEditable(true);
        genderCB.getEditor().setEditable(false);
        genderCB.getStyleClass().setAll("combo-box-guest-state");
        inputControls.add(genderCB);
        inputControlsForBtn.add(genderCB);
        genderCB.prefWidthProperty().bind(firstNameTF.widthProperty());
        genderCB.getEditor().setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (genderCB.getValue() == null) {
                    genderCB.getEditor().setText(genderCB.getPromptText());
                    genderCB.getEditor().setStyle("-fx-text-fill: rgba(180,180,180);");
                    hintLB.setText("Поле обязательно для заполнения");
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

        Label lb = new Label(genderCB.getPromptText());
        lb.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
        VBox.setMargin(lb, new Insets(5, 0, 0, 10));
        lb.setVisible(false);
        lb.setManaged(false);
        inputControlSecondVB.getChildren().add(lb);

        inputControlSecondVB.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (genderCB.isShowing()) {
                    genderCB.hide();
                } else {
                    genderCB.show();
                }
            }
        });

        genderCB.valueProperty().addListener((obs, old, val) -> {
            if (val == null || val.equals("Пол")) {
                genderCB.getEditor().setText(genderCB.getPromptText());
                genderCB.getEditor().setStyle("-fx-text-fill: rgba(180,180,180);");
                lb.setVisible(false);
                lb.setManaged(false);
            } else {
                genderCB.getEditor().setText(val);
                genderCB.getEditor().setStyle("-fx-text-fill: black;");
                hintLB.setText("");
                lb.setVisible(true);
                lb.setManaged(true);
            }
            guest.setGuestGender(val);
        });
        genderCB.setValue(null);

        inputControlSecondVB.getChildren().add(genderCB);

        hintLB.textProperty().addListener((ob, oldV, newV) -> {
            if (newV.isEmpty()) {
                hintLB.setVisible(false);
                hintLB.setManaged(false);
                hintLB.setStyle("");
                inputControlSecondVB.setStyle("");
            } else {
                hintLB.setVisible(true);
                hintLB.setManaged(true);
                hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
                inputControlSecondVB.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });

        TextField citizenshipTF = new TextField();
        citizenshipTF.setUserData("note Editable");
        setStateInputControl(0, 3, citizenshipTF, "Гражданство", InputControlContext.FIRST_NAME_OR_NAME);
        citizenshipTF.setText("Россия");
        citizenshipTF.setEditable(false);
        inputControls.add(citizenshipTF);
        inputControlsForBtn.add(citizenshipTF);
        guest.setGuestCitizenship(citizenshipTF.getText());

        TextField passportTF = new TextField();
        setStateInputControl(0, 4, passportTF, "Серия и номер", InputControlContext.PASSPORT);
        passportTF.setOnMouseEntered(e -> {
            passportTF.setPromptText("____ ______");
        });
        passportTF.setOnMouseExited(e -> {
            passportTF.setPromptText("Серия и номер");
        });
        inputControls.add(passportTF);
        inputControlsForBtn.add(passportTF);
        passportTF.textProperty().addListener((ob, oldV, newV) -> {
            guest.setGuestPassport(newV);
        });

        TextField nameTF = new TextField();
        setStateInputControl(1, 1, nameTF, "Имя", InputControlContext.FIRST_NAME_OR_NAME);
        inputControls.add(nameTF);
        inputControlsForBtn.add(nameTF);
        nameTF.textProperty().addListener((ob, oldV, newV) -> {
            guest.setGuestName(newV);
        });

        TextField birthdayTF = new TextField();
        setStateInputControl(1, 2, birthdayTF, "Дата рождения", InputControlContext.BIRTHDAY);
        birthdayTF.setOnMouseEntered(e -> {
            birthdayTF.setPromptText("__.__.____");
        });
        birthdayTF.setOnMouseExited(e -> {
            birthdayTF.setPromptText("Дата рождения");
        });
        inputControls.add(birthdayTF);
        inputControlsForBtn.add(birthdayTF);
        birthdayTF.textProperty().addListener((ob, oldV, newV) -> {
            guest.setGuestBirthday(newV);
        });

        TextField docTypeTF = new TextField();
        docTypeTF.setUserData("note Editable");
        setStateInputControl(1, 3, docTypeTF, "Тип документа", InputControlContext.FIRST_NAME_OR_NAME);
        docTypeTF.setText("Паспорт гражданина РФ");
        docTypeTF.setEditable(false);
        inputControls.add(docTypeTF);
        inputControlsForBtn.add(docTypeTF);
        guest.setGuestTypeDocument(docTypeTF.getText());

        TextField validityPeriodTF = new TextField();
        setStateInputControl(1, 4, validityPeriodTF, "Годен до...", InputControlContext.VALID_UNTIL);
        validityPeriodTF.setOnMouseEntered(e -> {
            validityPeriodTF.setPromptText("__.__.____");
        });
        validityPeriodTF.setOnMouseExited(e -> {
            validityPeriodTF.setPromptText("Годен до...");
        });
        inputControls.add(validityPeriodTF);
        inputControlsForBtn.add(validityPeriodTF);

        return rootVB;
    }

    private void setStateInputControl(int col, int row, TextInputControl node, String promptText, InputControlContext controlContext) {
        VBox inputControlVB = new VBox();
        inputControlVB.setAlignment(Pos.BOTTOM_LEFT);
        GridPane.setRowIndex(inputControlVB, row);
        GridPane.setColumnIndex(inputControlVB, col);
        GridPane.setMargin(inputControlVB, new Insets(10));

        VBox inputControlSecondVB = new VBox();
        inputControlSecondVB.getStyleClass().add("input-guest-state-control");
        inputControlVB.getChildren().add(inputControlSecondVB);

        Label hintLB = new Label();
        hintLB.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintLB, new Insets(0, 0, 0, 5));
        hintLB.prefWidthProperty().bind(node.widthProperty());
        hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
        hintLB.setVisible(false);
        hintLB.setManaged(false);
        hintLB.textProperty().addListener((ob, oldV, newV) -> {
            if (newV.isEmpty()) {
                hintLB.setManaged(false);
                hintLB.setVisible(false);
                inputControlSecondVB.setStyle("");
            } else {
                hintLB.setManaged(true);
                hintLB.setVisible(true);
                inputControlSecondVB.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });
        inputControlVB.getChildren().addFirst(hintLB);

        node.setPromptText(promptText);
        node.getStyleClass().add("style-input-control");

        Label lb = new Label(node.getPromptText());
        lb.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
        VBox.setMargin(lb, new Insets(5, 0, 0, 10));
        if (node.getUserData() != null && node.getUserData().equals("note Editable")) {
            lb.setVisible(true);
            lb.setManaged(true);
        } else {
            lb.setVisible(false);
            lb.setManaged(false);
        }
        inputControlSecondVB.getChildren().add(lb);

        node.setUserData(hintLB);

        node.focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                lb.setVisible(true);
                lb.setManaged(true);
            } else {
                if (node.getText() == null || node.getText().isEmpty()) {
                    lb.setVisible(false);
                    lb.setManaged(false);
                }
            }
        });

        node.textProperty().addListener((obs, old, val) -> {
            if (val.isEmpty() && !node.isFocused()) {
                lb.setVisible(false);
                lb.setManaged(false);
            }
            if (promptText != null && promptText.equals("Серия и номер")) {
                if (!val.isEmpty() && !val.contains("_")) {
                    if (passpertDataMap.containsValue(val)) {
                        hintLB.setText("Гость с таким паспортом в этом заказе уже существует");
                    } else {
                        passpertDataMap.put(node, val);
                    }
                } else passpertDataMap.put(node, val);
            }
        });


        if (controlContext == InputControlContext.FIRST_NAME_OR_NAME) {
            node.setTextFormatter(new TextFormatter<>(change -> {
                String oldText = change.getControlText();
                String inputText = change.getText();

                if (!inputText.isEmpty()) {
                    String filtered = inputText.chars()
                            .filter(c -> isAllowed((char) c))
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();

                    if (filtered.isEmpty()) {
                        if (inputText.length() == 1) {
                            hintLB.setText("Доступны только буквы русского алфавита");
                        }
                        return null;
                    } else hintLB.setText("");
                    change.setText(filtered);
                }

                if (change.isDeleted()) {
                    return validateAndCapitalize(change, oldText, change.getControlNewText(), node);
                }

                String newTextFull = change.getControlNewText();
                return validateAndCapitalize(change, oldText, newTextFull, node);
            }));
        } else if (controlContext == InputControlContext.BIRTHDAY) {
            InputControlMaskFormatter maskBirthday = new InputControlMaskFormatter();
            maskBirthday.apply(node, InputControlMaskFormatter.MaskContext.DATE_MASK);
        } else if (controlContext == InputControlContext.VALID_UNTIL) {
            InputControlMaskFormatter maskValidUtil = new InputControlMaskFormatter();
            maskValidUtil.apply(node, InputControlMaskFormatter.MaskContext.DATE_MASK);
        } else if (controlContext == InputControlContext.PASSPORT) {
            InputControlMaskFormatter maskValidUtil = new InputControlMaskFormatter();
            maskValidUtil.apply(node, InputControlMaskFormatter.MaskContext.PASSPORT_MASK);
        }


        inputControlSecondVB.getChildren().add(node);

        gridPane.getChildren().add(inputControlVB);
    }

    private void updateBuyer() {
        for (Map.Entry<VBox, CustomRadioButton> entry : radioButtonMap.entrySet()) {
            VBox key = entry.getKey();
            List<Node> inputControlsForBtn = (List<Node>) key.getUserData();
            key.getChildren().removeIf(node -> node.getUserData() != null && node.getUserData().equals("bottomHB"));

            if (entry.getValue().getSelected()) {
                User currUser = null;
                if (CONFIG_MANAGER.getUserId() != 0) {
                    currUser = new UserService().getRowById(CONFIG_MANAGER.getUserId());
                }
                HBox bottomHB = new HBox(10);
                bottomHB.setUserData("bottomHB");
                bottomHB.setPadding(new Insets(25));
                key.getChildren().add(bottomHB);

                VBox inputControlVB1 = new VBox();
                inputControlVB1.setAlignment(Pos.BOTTOM_CENTER);
                inputControlVB1.prefWidthProperty().bind(bottomHB.widthProperty().divide(2).subtract(5));
                bottomHB.getChildren().add(inputControlVB1);

                VBox inputControlSecondVB1 = new VBox();
                inputControlSecondVB1.getStyleClass().add("input-guest-state-control");
                inputControlVB1.getChildren().add(inputControlSecondVB1);

                Label hintLB1 = new Label();
                hintLB1.getStyleClass().add("hint-label-registration");
                VBox.setMargin(hintLB1, new Insets(0, 0, 0, 5));
                hintLB1.prefWidthProperty().bind(inputControlVB1.widthProperty());
                inputControlVB1.getChildren().addFirst(hintLB1);
                hintLB1.setVisible(false);
                hintLB1.setManaged(false);
                hintLB1.setStyle("-fx-text-fill: rgba(130,0,0);");

                TextField emailTF = new TextField();
                if (enteredEmail == null || enteredEmail.isEmpty()) {
                    if (currUser != null && (currUser.getUserEmail() != null || !currUser.getUserEmail().isEmpty()))
                        emailTF.setText(currUser.getUserEmail());
                } else emailTF.setText(enteredEmail);
                emailTF.setPromptText("Email *");
                emailTF.getStyleClass().add("style-input-control");
                emailTF.setUserData(hintLB1);
                inputControls.add(emailTF);
                inputControlsForBtn.add(emailTF);
                emailTF.prefWidthProperty().bind(inputControlVB1.widthProperty());

                Label lb1 = new Label(emailTF.getPromptText());
                lb1.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
                VBox.setMargin(lb1, new Insets(5, 0, 0, 10));
                lb1.setVisible(false);
                lb1.setManaged(false);
                inputControlSecondVB1.getChildren().add(lb1);

                emailTF.focusedProperty().addListener((obs, old, val) -> {
                    if (emailTF.getText().isEmpty())
                        hintLB1.setText("Поле обязательно для заполнения");

                    if (val) {
                        lb1.setVisible(true);
                        lb1.setManaged(true);
                        if (hintLB1.getText().isEmpty())
                            hintLB1.setText(validateEmail(emailTF.getText()));
                    } else {
                        if (emailTF.getText() == null || emailTF.getText().isEmpty()) {
                            lb1.setVisible(false);
                            lb1.setManaged(false);
                        }
                    }
                });

                emailTF.textProperty().addListener((ob, oldV, newV) -> {
                    if (validateEmail(newV) != null) {
                        hintLB1.setText(validateEmail(newV));
                    } else {
                        hintLB1.setText("");
                    }

                    if (newV.isEmpty() && !emailTF.isFocused()) {
                        lb1.setVisible(false);
                        lb1.setManaged(false);
                    }
                    buyerEmail = newV;
                    enteredEmail = newV;
                });
                inputControlSecondVB1.getChildren().add(emailTF);

                hintLB1.textProperty().addListener((ob, oldV, newV) -> {
                    if (newV == null || newV.isEmpty()) {
                        hintLB1.setManaged(false);
                        hintLB1.setVisible(false);
                        inputControlSecondVB1.setStyle("");
                    } else {
                        hintLB1.setManaged(true);
                        hintLB1.setVisible(true);
                        inputControlSecondVB1.setStyle("-fx-border-color: rgba(130,0,0);");
                    }
                });

                VBox inputControlVB2 = new VBox();
                inputControlVB2.setAlignment(Pos.BOTTOM_CENTER);
                inputControlVB2.prefWidthProperty().bind(bottomHB.widthProperty().divide(2).subtract(5));
                bottomHB.getChildren().add(inputControlVB2);

                VBox inputControlSecondVB2 = new VBox();
                inputControlSecondVB2.getStyleClass().add("input-guest-state-control");
                inputControlVB2.getChildren().add(inputControlSecondVB2);

                Label hintLB2 = new Label();
                hintLB2.getStyleClass().add("hint-label-registration");
                VBox.setMargin(hintLB2, new Insets(0, 0, 0, 5));
                hintLB2.prefWidthProperty().bind(inputControlVB2.widthProperty());
                inputControlVB2.getChildren().addFirst(hintLB2);
                hintLB2.setVisible(false);
                hintLB2.setManaged(false);
                hintLB2.setStyle("-fx-text-fill: rgba(130,0,0);");

                TextField phoneTF = new TextField();
                if (enteredPhone != null && !enteredPhone.isEmpty()) phoneTF.setText(enteredPhone);
                phoneTF.setUserData(hintLB2);
                phoneTF.setPromptText("Номер телефона");
                phoneTF.getStyleClass().add("style-input-control");
                phoneTF.prefWidthProperty().bind(inputControlVB2.widthProperty());
                inputControls.add(phoneTF);
                inputControlsForBtn.add(phoneTF);
                phoneTF.setOnMouseEntered(e -> {
                    phoneTF.setPromptText("+7(___)___-__-__");
                });
                phoneTF.setOnMouseExited(e -> {
                    phoneTF.setPromptText("Номер телефона");
                });

                Label lb2 = new Label(phoneTF.getPromptText());
                lb2.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
                VBox.setMargin(lb2, new Insets(5, 0, 0, 10));
                lb2.setVisible(false);
                lb2.setManaged(false);
                inputControlSecondVB2.getChildren().add(lb2);

                phoneTF.focusedProperty().addListener((obs, old, val) -> {
                    if (phoneTF.getText().isEmpty())
                        hintLB2.setText("Поле обязательно для заполнения");

                    if (val) {
                        lb2.setVisible(true);
                        lb2.setManaged(true);
                        if(!phoneTF.getText().matches("^\\+7([0-9]{3})[0-9]{3}-[0-9]{2}-[0-9]{2}$") && hintLB2.getText().isEmpty())
                            hintLB2.setText("Некорректный номер телефона");
                    } else {
                        if (phoneTF.getText() == null || phoneTF.getText().isEmpty()) {
                            lb2.setVisible(false);
                            lb2.setManaged(false);
                        }
                    }
                });

                phoneTF.textProperty().addListener((ob, oldV, newV) -> {
                    if (newV.isEmpty() && !phoneTF.isFocused()) {
                        lb2.setVisible(false);
                        lb2.setManaged(false);
                    }
                    buyerPhone = newV;
                    enteredPhone = newV;
                });

                InputControlMaskFormatter inputControlMaskFormatter = new InputControlMaskFormatter();
                inputControlMaskFormatter.apply(phoneTF, InputControlMaskFormatter.MaskContext.PHONE_MASK);
                inputControlSecondVB2.getChildren().add(phoneTF);

                hintLB2.textProperty().addListener((ob, oldV, newV) -> {
                    if (newV.isEmpty()) {
                        hintLB2.setManaged(false);
                        hintLB2.setVisible(false);
                        inputControlSecondVB2.setStyle("");
                    } else {
                        hintLB2.setManaged(true);
                        hintLB2.setVisible(true);
                        inputControlSecondVB2.setStyle("-fx-border-color: rgba(130,0,0);");
                    }
                });
            }
        }
    }

    private static boolean isAllowed(char c) {
        return Character.isLetter(c) && Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC || c == ' ' || c == '-';
    }

    private static TextFormatter.Change validateAndCapitalize(TextFormatter.Change change, String oldText
            , String newTextFull, TextInputControl textInputControl) {
        Label hintLB = ((Label) textInputControl.getUserData());

        if (newTextFull.isEmpty()) {
            if (change.getControl().isFocused())
                hintLB.setText("Поле обязательно для заполнения");
            return change;
        }

        char first = newTextFull.charAt(0);

        if (first == ' ' || first == '-') {
            hintLB.setText("Строка не должна начинаться со спец. символа");
            return null;
        }

        for (int i = 1; i < newTextFull.length(); i++) {
            char prev = newTextFull.charAt(i - 1);
            char curr = newTextFull.charAt(i);
            if (isSpecial(prev) && isSpecial(curr)) {
                hintLB.setText("Недопустимо два спец символа подряд");
                return null;
            }
        }

        String correctedText = capitalizeFirst(newTextFull);

        if (!correctedText.equals(newTextFull)) {
            change.setText(correctedText);
            change.setRange(0, oldText.length());
            change.setCaretPosition(correctedText.length());
            change.setAnchor(correctedText.length());
        }

        hintLB.setText("");
        textInputControl.setStyle("");
        return change;
    }

    private static boolean isSpecial(char c) {
        return c == ' ' || c == '-';
    }

    private static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        char first = text.charAt(0);
        if (Character.UnicodeBlock.of(first) == Character.UnicodeBlock.CYRILLIC
                && Character.isLowerCase(first)) {
            return Character.toUpperCase(first) + text.substring(1);
        }
        return text;
    }

    static String validateEmail(String email) {
        //Проверяем что значение не пустое
        if (email.isEmpty())
            return "Поле обязательно для заполнения";

        //Проверка на символ @
        if (!email.contains("@"))
            return "Адрес электронной почты должен содержать символ @";

        //Проверка длинны электронного адреса
        if (email.length() > 50)
            return "Адрес электронной почты не может быть длиннее 50-ти символов.";

        //Проверяем что мы имеем две части, как слева от @, так и справа
        String[] parts = email.split("@", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty())
            return "Некорректный формат email";

        //Создадим две переменные, для каждой из частей
        String localPart = parts[0];
        String domainPart = parts[1];

        //Проверка локальной части
        //Проверяем первый и последний символ в части имени email
        if (!EMAIL_NAME_PATTERN.matcher(localPart).matches()) {
            if (!Character.isLetterOrDigit(localPart.charAt(0)))
                return "Email не должен начинаться со специального символа";

            if (!Character.isLetterOrDigit(localPart.charAt(localPart.length() - 1)))
                return "Email не должен заканчиваться специальным символом";

            return "Email может содержать только англ. буквы, цифры и 1 спец. символ";
        }

        //Проверка доменной части
        //Доменная часть обязательно должна содержать символ .
        if (!domainPart.contains("."))
            return "Домен должен содержать точку";

        //Делим доменную часть левую(до символа .) и правую после неё, где указывается доменная зона(ru, com)
        String domain = domainPart.substring(0, domainPart.indexOf('.'));
        String zone = domainPart.substring(domainPart.indexOf('.') + 1);

        //Проверяем что указан допустимый домен
        if (!DOMAIN_NAME_PATTERN.matcher(domain).matches())
            return "Допустимые домены: yandex, mail, gmail";

        //Проверяем что доменная зона корректно указана
        if (!END_EMAIL_PATTERN.matcher("." + zone).matches())
            return "Доменная зона должна состоять из 2-3 маленьких латинских букв";

        return null;
    }

    private void createPaymentWin() {
        StackPane overSP = PopularDestinationsController.getOverlaySP();

        if (shadowPane == null) {
            shadowPane = new Pane();
            shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
            shadowPane.setVisible(false);
            overSP.getChildren().add(shadowPane);
        }

        closeBtnPayment = new Button();
        closeBtnPayment.setVisible(false);
        closeBtnPayment.setPrefHeight(30);
        closeBtnPayment.setPrefWidth(30);
        StackPane.setAlignment(closeBtnPayment, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtnPayment, new Insets(20, 20, 0, 0));
        closeBtnPayment.getStyleClass().add("close-button");
        closeBtnPayment.setOnAction(event -> {
            hidePaymentWin(false);
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeBtnPayment.setGraphic(closeImg);

        overSP.getChildren().add(closeBtnPayment);

        paymentWebView = new WebView();
        paymentWebView.setVisible(false);
        paymentWebView.setUserData("paymentWebView");

        double startWidthWebView = overSP.getWidth() * 0.7;
        double startHeightWebView = overSP.getHeight();

        paymentWebView.setPrefWidth(startWidthWebView);
        paymentWebView.setMaxWidth(startWidthWebView);
        paymentWebView.setMinWidth(startWidthWebView);
        paymentWebView.setPrefHeight(startHeightWebView);
        paymentWebView.setMaxHeight(startHeightWebView);
        paymentWebView.setMinHeight(startHeightWebView);

        // Привязка размеров (как у вас)
        overSP.widthProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue() * 0.7;
            paymentWebView.setPrefWidth(newVal);
            paymentWebView.setMaxWidth(newVal);
            paymentWebView.setMinWidth(newVal);
        });
        overSP.heightProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue();
            paymentWebView.setPrefHeight(newVal);
            paymentWebView.setMaxHeight(newVal);
            paymentWebView.setMinHeight(newVal);
        });

        overSP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!paymentWebView.isVisible()) return;

            Point2D pointInWindow = paymentWebView.screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && paymentWebView.contains(pointInWindow))
                return;
            else hidePaymentWin(false);
        });

        WebEngine engine = paymentWebView.getEngine();
        engine.setJavaScriptEnabled(true);

        engine.locationProperty().addListener((obs, oldUrl, newUrl) -> {
            if (newUrl != null && newUrl.startsWith("https://yoomoney.ru")) {
                engineIsEmpty = true;

                order.setPaid(true);
                new OrderService().updateRow(order);

                order = null;

                resetPaymentWebView();
                hidePaymentWin(true);
                // Здесь разблокируйте контент
                AnchorPane successfulPaymentWindow = createSuccessfulPaymentWindow();
                successfulPaymentWindow.setManaged(true);
                successfulPaymentWindow.setPadding(new Insets(20));

                FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.millis(600), successfulPaymentWindow);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                fadeIn.setOnFinished(eventIn -> {
                    PauseTransition hideTimer = new PauseTransition(javafx.util.Duration.seconds(3));
                    hideTimer.playFromStart();
                    hideTimer.setOnFinished(eventTimer -> {
                        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.millis(200), successfulPaymentWindow);
                        fadeOut.setToValue(0.0);
                        fadeOut.setOnFinished(event -> {
                            shadowPane.setVisible(false);
                            PopularDestinationsController.getOverlaySP().getChildren().remove(successfulPaymentWindow);
                        });
                        fadeOut.play();
                    });
                });
            }
        });

        overSP.getChildren().add(paymentWebView);
    }

    private void showPaymentWin(double amount, String description) {
        shadowPane.setVisible(true);
        closeBtnPayment.setVisible(true);
        paymentWebView.setVisible(true);

        if (engineIsEmpty) {
            if (resetPaymentTask != null && !resetPaymentTask.isDone()) {
                resetPaymentTask.cancel(false);
            }

            if (order == null) {
                createOrderAndGuests();
            }

            reloadPaymentContent(amount, description);
        }
    }

    private void reloadPaymentContent(double amount, String description) {
        new Thread(() -> {
            try {
                String token = getConfirmationToken(amount, description);
                String filledHtml = loadHtmlTemplate();

                Platform.runLater(() -> {
                    WebEngine engine = paymentWebView.getEngine();
                    if (loadListener != null) {
                        engine.getLoadWorker().stateProperty().removeListener(loadListener);
                    }
                    loadListener = (obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            String initScript = String.format(
                                    "function waitForWidget() {" +
                                            "  if (typeof YooMoneyCheckoutWidget !== 'undefined') {" +
                                            "    var checkout = new YooMoneyCheckoutWidget({" +
                                            "      confirmation_token: '%s'," +
                                            "      return_url: 'https://yoomoney.ru'," +
                                            "      error_callback: function(error) { console.error(error); }" +
                                            "    });" +
                                            "    checkout.render('payment-form');" +
                                            "    clearInterval(intervalId);" +
                                            "  }" +
                                            "}" +
                                            "var intervalId = setInterval(waitForWidget, 100);", token);
                            engineIsEmpty = false;

                            resetPaymentTask = sheduler.schedule(() -> {
                                Platform.runLater(() -> {
                                    if (paymentWebView != null && paymentWebView.isVisible()) {
                                        hidePaymentWin(false);
                                    }
                                    engineIsEmpty = true;
                                });
                            }, 10, TimeUnit.MINUTES);

                            engine.executeScript(initScript);
                        }
                    };
                    engine.getLoadWorker().stateProperty().addListener(loadListener);
                    engine.loadContent(filledHtml);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> hidePaymentWin(false));
            }
        }).start();
    }

    private void createOrderAndGuests() {
        order = new Order();
        order.setUser(new UserService().getRowById(CONFIG_MANAGER.getUserId()));
        order.setOrderDate(LocalDateTime.now());
        order.setRoom(room);
        order.setDateStart(startDate);
        order.setDateEnd(endDate);
        order.setOrderCost(orderCost);
        order.setPaid(false);
        new OrderService().saveRow(order);

        GuestService service = new GuestService();
        for (Guest guest : guests) {
            Guest saveGuest = new Guest();
            saveGuest.setOrder(order);
            saveGuest.setGuestFirstName(guest.getGuestFirstName());
            saveGuest.setGuestName(guest.getGuestName());
            saveGuest.setGuestGender(guest.getGuestGender());
            saveGuest.setGuestBirthday(guest.getGuestBirthday());
            saveGuest.setGuestCitizenship(guest.getGuestCitizenship());
            saveGuest.setGuestTypeDocument(guest.getGuestTypeDocument());
            saveGuest.setGuestPassport(guest.getGuestPassport());
            saveGuest.setBuyerBoolean(guest.isBuyerBoolean());
            if (saveGuest.isBuyerBoolean()) {
                saveGuest.setBuyerEmail(buyerEmail);
                saveGuest.setBuyerPhone(buyerPhone);
            }
            service.saveRow(saveGuest);
        }
    }

    private void hidePaymentWin(boolean showShadowPane) {
        shadowPane.setVisible(showShadowPane);
        closeBtnPayment.setVisible(false);
        paymentWebView.setVisible(false);
    }

    private String getConfirmationToken(double amount, String description) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", String.format("%.2f", amount).replace(",", "."));
        requestBody.put("description", description);
        String json = new Gson().toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/create-payment"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Сервер вернул ошибку: " + response.statusCode());
        }

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.get("confirmation_token").getAsString();
    }

    private String loadHtmlTemplate() throws IOException {
        InputStream is = getClass().getResourceAsStream("/com/example/travel/payment-widget.html");
        if (is == null) {
            System.err.println("ERROR: payment-widget.html not found in resources");
            throw new IOException("HTML шаблон не найден");
        }
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        return content;
    }

    private AnchorPane createSuccessfulPaymentWindow() {
        AnchorPane welcomeAP = new AnchorPane();
        welcomeAP.setMaxHeight(350);
        welcomeAP.setMaxWidth(500);
        welcomeAP.setOpacity(0.0);
        welcomeAP.setManaged(false);
        welcomeAP.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");

        VBox headerVB = new VBox(5);
        AnchorPane.setTopAnchor(headerVB, 20.0);
        AnchorPane.setLeftAnchor(headerVB, 0.0);
        AnchorPane.setRightAnchor(headerVB, 0.0);
        AnchorPane.setBottomAnchor(headerVB, 100.0);
        headerVB.setAlignment(Pos.CENTER);
        headerVB.getStyleClass().add("welcome-header-text-flow");

        Label firstText = new Label("Оплата");
        firstText.getStyleClass().add("welcome-header");

        Label secondText = new Label("проведена успешно!");
        secondText.getStyleClass().add("welcome-header");

        headerVB.getChildren().addAll(firstText, secondText);

        TextFlow footTextFlow = new TextFlow();
        AnchorPane.setLeftAnchor(footTextFlow, 0.0);
        AnchorPane.setRightAnchor(footTextFlow, 0.0);
        AnchorPane.setBottomAnchor(footTextFlow, 0.0);
        footTextFlow.setTextAlignment(TextAlignment.CENTER);

        Text firstTextFoot = new Text("Поздравляем с успешной оплатой!\n");
        firstTextFoot.getStyleClass().add("welcome-foot");
        firstTextFoot.setFill(Color.rgb(130, 130, 130));

        Text secondTextFoot = new Text("Пора собирать чемоданы в незабываемое приключение!");
        secondTextFoot.getStyleClass().add("welcome-foot");
        secondTextFoot.setFill(Color.rgb(130, 130, 130));

        footTextFlow.getChildren().addAll(firstTextFoot, secondTextFoot);

        welcomeAP.getChildren().addAll(headerVB, footTextFlow);

        PopularDestinationsController.getOverlaySP().getChildren().add(welcomeAP);

        return welcomeAP;
    }

    private void resetPaymentWebView() {
        Platform.runLater(() -> {
            WebEngine engine = paymentWebView.getEngine();
            engine.loadContent("");               // очищаем содержимое
            engineIsEmpty = true;
            // удаляем временный слушатель загрузки, если он есть
            if (loadListener != null) {
                engine.getLoadWorker().stateProperty().removeListener(loadListener);
                loadListener = null;
            }
            // отменяем запланированный сброс
            if (resetPaymentTask != null && !resetPaymentTask.isDone()) {
                resetPaymentTask.cancel(false);
            }
        });
    }
}
