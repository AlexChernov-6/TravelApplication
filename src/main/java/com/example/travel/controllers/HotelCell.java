package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.models.HotelFeature;
import com.example.travel.services.HotelFeatureRelationService;
import com.example.travel.services.RoomService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Objects;

public class HotelCell extends ListCell<Hotel> {
    private static final double COLUMN_IMAGE_WIDTH = 200;
    private static final double COLUMN_PRICE_WIDTH = 300;
    private static final double GRID_HEIGHT = 200;
    private static final double ROOT_PADDING = 10;
    private static final Image STAR_IMAGE =
            new Image(Objects.requireNonNull(HotelCell.class.getResourceAsStream("/images/star.png")));
    private static final Image DISCOUND_IMAGE =
            new Image(Objects.requireNonNull(HotelCell.class.getResourceAsStream("/images/discount.png")));
    private static final Image RUBLE_IMAGE =
            new Image(Objects.requireNonNull(HotelCell.class.getResourceAsStream("/images/ruble.png")));
    private static final Image RUBLE_SMALL_IMAGE =
            new Image(Objects.requireNonNull(HotelCell.class.getResourceAsStream("/images/ruble-small.png")));

    private GridPane rootGridPane;
    private VBox rootVB;
    private ImageView imageViewHotel;
    private Label hotelNameLB;
    private Label hotelCountStar;
    private Label ratingLB;
    private Label countRatingsLB;
    private Label actualMinPriceLB;
    private Text actualPriceLB;
    private ContainerForFeatures hotelFeaturesContainer;
    private StackPane imagesHotelSP;

    private Hotel currentHotel;

    private int currentVisibleImage = 0;

    private final ChangeListener<Number> imageIndexListener;

    private Button prevImageBtn, nextImageBtn;

    public HotelCell() {
        createCell();

        imageIndexListener = (obs, oldVal, newVal) -> {
            if (currentHotel != null) {
                imageViewHotel.setImage(currentHotel.getImageByNumber(newVal.intValue()));
            }
        };
    }

    @Override
    protected void updateItem(Hotel hotel, boolean empty) {
        super.updateItem(hotel, empty);

        setText(null);
        setGraphic(null);

        if (empty || hotel == null) {
            if (currentHotel != null) {
                currentHotel.currentImageIndexProperty().removeListener(imageIndexListener);
                currentHotel = null;
            }
            setGraphic(null);
            setText(null);
        } else {
            if (currentHotel != hotel) {
                if (currentHotel != null) {
                    currentHotel.currentImageIndexProperty().removeListener(imageIndexListener);
                }
                currentHotel = hotel;
                currentHotel.currentImageIndexProperty().addListener(imageIndexListener);
            }
            imageViewHotel.setImage(hotel.getImageByNumber(hotel.getCurrentImageIndex()));
            updateStateCell(hotel);
            setGraphic(rootVB);
        }
    }

    private void createCell() {
        rootVB = new VBox();
        rootVB.setAlignment(Pos.CENTER);
        rootVB.setPadding(new Insets(ROOT_PADDING));

        rootGridPane = new GridPane();
        rootGridPane.getStyleClass().add("root-grid-pane");
        rootGridPane.setPrefHeight(GRID_HEIGHT);
        rootGridPane.setMaxHeight(GRID_HEIGHT);

        ColumnConstraints col1 = new ColumnConstraints(COLUMN_IMAGE_WIDTH);
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(COLUMN_IMAGE_WIDTH);

        // Колонка 2 (фичи) – занимает всё доступное место, может расти
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        // Колонка 3 (цена и кнопка) – фиксированная ширина, не растягивается, минимальная ширина = 300
        ColumnConstraints col3 = new ColumnConstraints(COLUMN_PRICE_WIDTH);
        col3.setHgrow(Priority.NEVER);
        col3.setMinWidth(COLUMN_PRICE_WIDTH);

        rootGridPane.getColumnConstraints().addAll(col1, col2, col3);

        rootVB.prefWidthProperty().bind(widthProperty().subtract(ROOT_PADDING * 2));
        rootGridPane.prefWidthProperty().bind(rootVB.widthProperty().subtract(ROOT_PADDING * 2));

        rootGridPane.getRowConstraints().addAll(
                new RowConstraints(GRID_HEIGHT / 2),
                new RowConstraints(GRID_HEIGHT / 2)
        );

        imagesHotelSP = new StackPane();
        imagesHotelSP.setMaxWidth(COLUMN_IMAGE_WIDTH);
        imagesHotelSP.setMaxHeight(GRID_HEIGHT);
        GridPane.setColumnIndex(imagesHotelSP, 0);
        GridPane.setRowIndex(imagesHotelSP, 0);
        GridPane.setRowSpan(imagesHotelSP, 2);

        imageViewHotel = new ImageView();
        imageViewHotel.setFitWidth(COLUMN_IMAGE_WIDTH);
        imageViewHotel.setFitHeight(GRID_HEIGHT);
        //imageViewHotel.setPreserveRatio(true);

        Rectangle clip = new Rectangle(imageViewHotel.getFitWidth(), imageViewHotel.getFitHeight());
        clip.setArcWidth(25);
        clip.setArcHeight(25);
        imageViewHotel.setClip(clip);

        prevImageBtn = new Button();
        prevImageBtn.getStyleClass().add("scroll-button");
        prevImageBtn.setPrefHeight(20);
        prevImageBtn.setPrefWidth(20);
        StackPane.setMargin(prevImageBtn, new Insets(0, 0, 0, 10));
        StackPane.setAlignment(prevImageBtn, Pos.CENTER_LEFT);
        prevImageBtn.setVisible(false);
        prevImageBtn.setOnAction(e -> {
            if (currentHotel != null) {
                int newIndex = currentHotel.getCurrentImageIndex() - 1;
                currentHotel.setCurrentImageIndex(newIndex);
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
            if (currentHotel != null) {
                int newIndex = currentHotel.getCurrentImageIndex() + 1;
                currentHotel.setCurrentImageIndex(newIndex);
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

        rootGridPane.setOnMouseEntered(event -> {
            updateVisibleButton();
        });

        rootGridPane.setOnMouseExited(event -> {
            prevImageBtn.setVisible(false);
            nextImageBtn.setVisible(false);
        });

        imagesHotelSP.getChildren().addAll(imageViewHotel, prevImageBtn, nextImageBtn);

        rootGridPane.getChildren().add(imagesHotelSP);

        VBox stateHotelVB = new VBox(5);
        stateHotelVB.setPadding(new Insets(10));
        GridPane.setColumnIndex(stateHotelVB, 1);
        GridPane.setRowIndex(stateHotelVB, 0);
        stateHotelVB.setAlignment(Pos.TOP_LEFT);

        HBox hotelNameHB = new HBox();
        hotelNameHB.setAlignment(Pos.TOP_LEFT);

        hotelNameLB = new Label("hotelName");
        hotelNameLB.getStyleClass().add("hotel-name-label");

        hotelCountStar = new Label(" ?");
        hotelCountStar.getStyleClass().add("hotel-name-label");

        ImageView starImageView = new ImageView(STAR_IMAGE);
        starImageView.setFitHeight(15);
        starImageView.setFitWidth(15);
        starImageView.setPreserveRatio(true);

        hotelNameHB.getChildren().addAll(hotelNameLB, hotelCountStar, starImageView);

        HBox ratingHotelHB = new HBox(2);
        ratingHotelHB.getStyleClass().add("hotel-rating-hbox");
        ratingHotelHB.setMaxWidth(Region.USE_PREF_SIZE);

        ratingLB = new Label("5");
        ratingLB.getStyleClass().add("hotel-rating-label");
        ratingLB.setAlignment(Pos.CENTER);
        ratingLB.setPadding(new Insets(2, 10, 2, 10));

        countRatingsLB = new Label("Число оценок: ");
        countRatingsLB.setAlignment(Pos.CENTER_LEFT);
        countRatingsLB.getStyleClass().add("hotel-count-rating-label");
        countRatingsLB.setPadding(new Insets(2, 5, 2, 5));

        ratingHotelHB.getChildren().addAll(ratingLB, countRatingsLB);

        stateHotelVB.getChildren().addAll(hotelNameHB, ratingHotelHB);

        rootGridPane.getChildren().add(stateHotelVB);

        hotelFeaturesContainer = new ContainerForFeatures();
        GridPane.setValignment(hotelFeaturesContainer, VPos.BOTTOM);
        hotelFeaturesContainer.setMaxHeight(GRID_HEIGHT / 4 + 10);
        hotelFeaturesContainer.setPadding(new Insets(10));
        GridPane.setColumnIndex(hotelFeaturesContainer, 1);
        GridPane.setRowIndex(hotelFeaturesContainer, 1);

        hotelFeaturesContainer.maxWidthProperty()
                .bind(widthProperty().subtract(COLUMN_IMAGE_WIDTH).subtract(COLUMN_PRICE_WIDTH).subtract(ROOT_PADDING * 2));

        rootGridPane.getChildren().add(hotelFeaturesContainer);

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

        rootGridPane.getChildren().add(minPriceRoomVB);

        Button selectBtn = new Button("Выбрать");
        GridPane.setColumnIndex(selectBtn, 2);
        GridPane.setRowIndex(selectBtn, 1);
        GridPane.setHalignment(selectBtn, HPos.RIGHT);
        GridPane.setValignment(selectBtn, VPos.BOTTOM);
        selectBtn.getStyleClass().add("select-button");
        selectBtn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(selectBtn, true);
        GridPane.setMargin(selectBtn, new Insets(0, 30, 30, 10));

        rootGridPane.getChildren().add(selectBtn);

        rootVB.getChildren().add(rootGridPane);
    }

    private void updateStateCell(Hotel hotel) {
        imageViewHotel.setImage(hotel.getImageByNumber(currentVisibleImage));
        hotelNameLB.setText(hotel.getHotelName());
        hotelCountStar.setText(" " + hotel.getCountStars());
        double hotelRating = hotel.getHotelRating();
        ratingLB.setText(String.format("%.1f", hotelRating));
        if (hotelRating >= 4)
            ratingLB.setStyle("-fx-background-color: #0bb527;");
        else if (hotelRating < 4 && hotelRating >= 3)
            ratingLB.setStyle("-fx-background-color: #7fb50b;");
        else if (hotelRating < 3 && hotelRating >= 2)
            ratingLB.setStyle("-fx-background-color: #cbdb16;");
        else
            ratingLB.setStyle("-fx-background-color: #b00000;");

        countRatingsLB.setText("Число оценок: " + hotel.getCountRatings());


        hotelFeaturesContainer.clear();
        for (HotelFeature hotelFeature : new HotelFeatureRelationService().getAllHotelFeatureByHotelId(hotel.getIdHotel())) {
            Label label = new Label(hotelFeature.getFeatureName());
            label.getStyleClass().add("feature-label");
            hotelFeaturesContainer.addNode(label);
        }

        double minPrice = new RoomService().getMinRoomPriceByHotelId(hotel.getIdHotel());
        actualMinPriceLB.setText(String.format("%.2f", minPrice));
        actualPriceLB.setText(String.format("%.2f", minPrice + minPrice * 0.05));
        //Обработать цену
    }

    private void updateVisibleButton() {
        if (currentHotel == null) return;
        int index = currentHotel.getCurrentImageIndex();
        int max = currentHotel.getHotelPhotos().length - 1;
        prevImageBtn.setVisible(index > 0);
        nextImageBtn.setVisible(index < max);
    }
}
