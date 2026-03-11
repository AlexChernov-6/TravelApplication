package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.services.DirectionService;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.util.Objects;

public class ViewingImages extends GridPane {

    private final StackPane overSP = PopularDestinationsController.getOverlaySP();
    private final Hotel hotel;
    private AnchorPane anchorPane;
    private Label countImageLB;
    private int maxIndImg = 1;
    private Pane shadowPane;

    public ViewingImages(Hotel hotel) {
        this.hotel = hotel;
        maxIndImg = hotel.getHotelPhotos().length;

        setAlignment(Pos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);

        getColumnConstraints().addAll(col1, col2, col3);

        getRowConstraints().addAll(
                new RowConstraints(),
                new RowConstraints()
        );

        ImageView firstIV = new ImageView(hotel.getImageByNumber(0));
        firstIV.setFitHeight(400);
        GridPane.setColumnIndex(firstIV, 0);
        GridPane.setRowIndex(firstIV, 0);
        GridPane.setRowSpan(firstIV, 2);
        addActionIV(firstIV);
        firstIV.getStyleClass().add("set-hand-cursor");
        firstIV.setPreserveRatio(true);

        ImageView secondIV = new ImageView(hotel.getImageByNumber(1));
        secondIV.setFitHeight(200);
        GridPane.setColumnIndex(secondIV, 1);
        GridPane.setRowIndex(secondIV, 0);
        addActionIV(secondIV);
        secondIV.getStyleClass().add("set-hand-cursor");
        secondIV.setPreserveRatio(true);

        ImageView thirdIV = new ImageView(hotel.getImageByNumber(2));
        thirdIV.setFitHeight(200);
        GridPane.setColumnIndex(thirdIV, 1);
        GridPane.setRowIndex(thirdIV, 1);
        addActionIV(thirdIV);
        thirdIV.getStyleClass().add("set-hand-cursor");
        thirdIV.setPreserveRatio(true);

        ImageView fourthIV = new ImageView(hotel.getImageByNumber(3));
        fourthIV.setFitHeight(200);
        GridPane.setColumnIndex(fourthIV, 2);
        GridPane.setRowIndex(fourthIV, 0);
        addActionIV(fourthIV);
        fourthIV.getStyleClass().add("set-hand-cursor");
        fourthIV.setPreserveRatio(true);

        StackPane fifthIStackPane = new StackPane();
        GridPane.setColumnIndex(fifthIStackPane, 2);
        GridPane.setRowIndex(fifthIStackPane, 1);

        ImageView fifthIV = new ImageView(hotel.getImageByNumber(4));
        fifthIV.setFitHeight(200);
        addActionIV(fifthIV);
        fifthIV.getStyleClass().add("set-hand-cursor");
        fifthIV.setPreserveRatio(true);

        Pane shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        Label countHiddenImage = new Label("+" + (maxIndImg - 5));
        countHiddenImage.setStyle("-fx-text-fill: white; -fx-font-size: 25;");

        fifthIStackPane.getChildren().addAll(fifthIV, shadowPane, countHiddenImage);

        getChildren().addAll(firstIV, secondIV, thirdIV, fourthIV, fifthIStackPane);
    }

    private void addActionIV(ImageView imageView) {
        imageView.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                if(anchorPane == null)
                    createViewingImages(imageView.getImage());

                show();
            }
        });
    }

    private void createViewingImages(Image image) {
        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        overSP.getChildren().add(shadowPane);

        anchorPane = new AnchorPane();

        overSP.heightProperty().addListener((ob, oldV, newV) -> {
            if(newV.doubleValue() >= 400) {
                anchorPane.setPrefHeight(newV.doubleValue());
                anchorPane.setMaxHeight(newV.doubleValue());
                anchorPane.setMinHeight(newV.doubleValue());
            } else {
                anchorPane.setPrefHeight(400);
                anchorPane.setMaxHeight(400);
                anchorPane.setMinHeight(400);
            }
        });

        overSP.widthProperty().addListener((ob, oldV, newV) -> {
            if(newV.doubleValue() >= 400) {
                anchorPane.setPrefWidth(newV.doubleValue());
                anchorPane.setMaxWidth(newV.doubleValue());
                anchorPane.setMinWidth(newV.doubleValue());
            } else {
                anchorPane.setPrefWidth(400);
                anchorPane.setMaxWidth(400);
                anchorPane.setMinWidth(400);
            }
        });

        Button closeBtn = new Button();
        closeBtn.setPrefHeight(30);
        closeBtn.setPrefWidth(30);
        AnchorPane.setTopAnchor(closeBtn, 10.0);
        AnchorPane.setRightAnchor(closeBtn, 15.0);
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

        anchorPane.getChildren().add(closeBtn);

        StackPane stackPane = new StackPane();
        AnchorPane.setTopAnchor(stackPane, 50.0);
        AnchorPane.setLeftAnchor(stackPane, 15.0);
        AnchorPane.setRightAnchor(stackPane, 15.0);
        AnchorPane.setBottomAnchor(stackPane, 150.0);

        ImageView selectedIV = new ImageView(image);
        selectedIV.fitHeightProperty().bind(stackPane.heightProperty());
        selectedIV.setPreserveRatio(true);

        Button prevImageBtn = new Button();
        prevImageBtn.getStyleClass().add("scroll-button");
        prevImageBtn.setPrefHeight(30);
        prevImageBtn.setPrefWidth(30);
        StackPane.setMargin(prevImageBtn, new Insets(0, 0, 0, 10));
        StackPane.setAlignment(prevImageBtn, Pos.CENTER_LEFT);
        prevImageBtn.setOnAction(e -> {
            if (hotel != null) {
                int newIndex = hotel.getCurrentImageIndex() - 1;
                hotel.setCurrentImageIndex(newIndex);
                countImageLB.setText(hotel.getCurrentImageIndex() + 1 + "/" + maxIndImg);
            }
        });

        prevImageBtn.setOnMouseEntered(event -> {
            prevImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.7);");
        });

        prevImageBtn.setOnMouseExited(event -> {
            prevImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
        });

        ImageView prevImageIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/left-arrow.png"))));
        prevImageIV.setFitHeight(25);
        prevImageIV.setFitWidth(25 / 1.5);
        prevImageIV.setPreserveRatio(true);

        prevImageBtn.setGraphic(prevImageIV);

        Button nextImageBtn = new Button();
        nextImageBtn.getStyleClass().add("scroll-button");
        nextImageBtn.setPrefHeight(30);
        nextImageBtn.setPrefWidth(30);
        StackPane.setMargin(nextImageBtn, new Insets(0, 10, 0, 0));
        StackPane.setAlignment(nextImageBtn, Pos.CENTER_RIGHT);
        nextImageBtn.setOnAction(e -> {
            if (hotel != null) {
                int newIndex = hotel.getCurrentImageIndex() + 1;
                hotel.setCurrentImageIndex(newIndex);
                countImageLB.setText(hotel.getCurrentImageIndex() + 1 + "/" + maxIndImg);
            }
        });

        nextImageBtn.setOnMouseEntered(event -> {
            nextImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.7);");
        });

        nextImageBtn.setOnMouseExited(event -> {
            nextImageBtn.setStyle("-fx-background-color: rgba(255,255,255,0.5);");
        });

        ImageView nextImageIV = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/right-arrow.png"))));
        nextImageIV.setFitHeight(25);
        nextImageIV.setFitWidth(25 / 1.5);
        nextImageIV.setPreserveRatio(true);

        nextImageBtn.setGraphic(nextImageIV);

        stackPane.getChildren().addAll(selectedIV, prevImageBtn, nextImageBtn);

        anchorPane.getChildren().add(stackPane);

        HBox countImageHB = new HBox();
        AnchorPane.setBottomAnchor(countImageHB, 120.0);
        AnchorPane.setLeftAnchor(countImageHB, 15.0);
        AnchorPane.setRightAnchor(countImageHB, 15.0);
        countImageHB.setAlignment(Pos.CENTER);

        countImageLB = new Label(hotel.getCurrentImageIndex() + 1 + "/" + maxIndImg);
        countImageLB.getStyleClass().add("count-image-label");
        countImageLB.setPadding(new Insets(3, 10, 3, 10));

        countImageHB.getChildren().add(countImageLB);

        anchorPane.getChildren().add(countImageHB);

        ListView<ImageView> allImages = new ListView<>();
        AnchorPane.setBottomAnchor(allImages, 10.0);
        AnchorPane.setLeftAnchor(allImages, 15.0);
        AnchorPane.setRightAnchor(allImages, 15.0);
        allImages.setFixedCellSize(100);
        allImages.setSelectionModel(null);
        allImages.getStyleClass().add("images-list-view");
        allImages.setOrientation(Orientation.HORIZONTAL);
        allImages.setPrefHeight(100);
        for(int i=0; i<maxIndImg; i=i+1) {
            ImageView imageView = new ImageView(hotel.getImageByNumber(i));
            imageView.setFitHeight(100);
            imageView.setFitHeight(150);
            allImages.getItems().add(imageView);
        }

        anchorPane.getChildren().add(allImages);

        overSP.getChildren().add(anchorPane);
    }

    public void show() {
        shadowPane.setVisible(true);
        anchorPane.setVisible(true);
    }

    public void hide() {
        shadowPane.setVisible(false);
        anchorPane.setVisible(false);
    }
}