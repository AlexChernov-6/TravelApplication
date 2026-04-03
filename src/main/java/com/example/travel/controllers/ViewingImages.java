package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Objects;

import static com.example.travel.util.ImageUtils.round;

public class ViewingImages extends GridPane {

    private final StackPane overSP = PopularDestinationsController.getOverlaySP();
    private Hotel hotel;
    private AnchorPane anchorPane;
    private Label countImageLB;
    private int maxIndImg;
    private Pane shadowPane;
    private ListView<ImageView> allImages;
    private ImageView selectedIV, firstIV, secondIV, thirdIV, fourthIV, fifthIV;
    private Label countHiddenImage;

    public ViewingImages(GridPane parentGP) {
        setAlignment(Pos.TOP_CENTER);

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

        firstIV = new ImageView();
        GridPane.setColumnIndex(firstIV, 0);
        GridPane.setRowIndex(firstIV, 0);
        GridPane.setRowSpan(firstIV, 2);
        firstIV.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                hotel.setCurrentImageIndex(0);
                if(anchorPane == null)
                    createViewingImages();
                else
                    updateSelectedImage();
                show();
            }
        });
        firstIV.getStyleClass().add("set-hand-cursor");
        firstIV.setPreserveRatio(true);

        secondIV = new ImageView();
        GridPane.setColumnIndex(secondIV, 1);
        GridPane.setRowIndex(secondIV, 0);
        secondIV.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                hotel.setCurrentImageIndex(1);
                if(anchorPane == null)
                    createViewingImages();
                else
                    updateSelectedImage();
                show();
            }
        });
        secondIV.getStyleClass().add("set-hand-cursor");
        secondIV.setPreserveRatio(true);

        thirdIV = new ImageView();
        GridPane.setColumnIndex(thirdIV, 1);
        GridPane.setRowIndex(thirdIV, 1);
        thirdIV.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                hotel.setCurrentImageIndex(2);
                if(anchorPane == null)
                    createViewingImages();
                else
                    updateSelectedImage();
                show();
            }
        });
        thirdIV.getStyleClass().add("set-hand-cursor");
        thirdIV.setPreserveRatio(true);

        fourthIV = new ImageView();
        GridPane.setColumnIndex(fourthIV, 2);
        GridPane.setRowIndex(fourthIV, 0);
        fourthIV.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                hotel.setCurrentImageIndex(3);
                if(anchorPane == null)
                    createViewingImages();
                else
                    updateSelectedImage();
                show();
            }
        });
        fourthIV.getStyleClass().add("set-hand-cursor");
        fourthIV.setPreserveRatio(true);

        StackPane fifthIStackPane = new StackPane();
        GridPane.setColumnIndex(fifthIStackPane, 2);
        GridPane.setRowIndex(fifthIStackPane, 1);
        fifthIStackPane.setStyle("-fx-background-radius: 0 0 10 0;");

        fifthIV = new ImageView();
        fifthIV.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                hotel.setCurrentImageIndex(4);
                if(anchorPane == null)
                    createViewingImages();
                else
                    updateSelectedImage();
                show();
            }
        });
        fifthIV.getStyleClass().add("set-hand-cursor");
        fifthIV.setPreserveRatio(true);

        fifthIV.fitWidthProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue();
            fifthIStackPane.setPrefWidth(newVal);
            fifthIStackPane.setMinWidth(newVal);
            fifthIStackPane.setMaxWidth(newVal);
        });

        Pane shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        shadowPane.setMouseTransparent(true);

        countHiddenImage = new Label();
        countHiddenImage.setStyle("-fx-text-fill: white; -fx-font-size: 25; -fx-font-weight: bold;");
        countHiddenImage.setMouseTransparent(true);

        fifthIStackPane.getChildren().addAll(fifthIV, shadowPane, countHiddenImage);

        getChildren().addAll(firstIV, secondIV, thirdIV, fourthIV, fifthIStackPane);

        widthProperty().addListener((ob, oldV, newV) -> {
            double newVal = newV.doubleValue();
            if(newVal >= 600) {
                firstIV.setFitWidth(newVal / 2);
                secondIV.setFitWidth(newVal / 4);
                thirdIV.setFitWidth(newVal / 4);
                fourthIV.setFitWidth(newVal / 4);
                fifthIV.setFitWidth(newVal / 4);
                round(firstIV, 30, 0, 0, 30);
                round(fourthIV, 0, 30, 0, 0);
                round(fifthIV, 0, 0, 30, 0);
                Platform.runLater(() -> {
                    round(shadowPane, 0, 0, 30, 0);
                    double newHeight = 80 + firstIV.getLayoutBounds().getHeight();
                    parentGP.setPrefHeight(newHeight);
                });
            }
        });
    }

    private void createViewingImages() {
        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
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

        selectedIV = new ImageView(hotel.getImageByNumber(hotel.getCurrentImageIndex()));
        selectedIV.fitHeightProperty().bind(stackPane.heightProperty());
        selectedIV.setPreserveRatio(true);

        selectedIV.fitHeightProperty().addListener((ob, oldV, newV) -> {
            round(selectedIV, 30, 30, 30, 30);
        });

        Button prevImageBtn = new Button();
        prevImageBtn.getStyleClass().add("scroll-button");
        prevImageBtn.setPrefHeight(30);
        prevImageBtn.setPrefWidth(30);
        StackPane.setMargin(prevImageBtn, new Insets(0, 0, 0, 10));
        StackPane.setAlignment(prevImageBtn, Pos.CENTER_LEFT);
        prevImageBtn.setOnAction(e -> {
            if(hotel.getCurrentImageIndex() - 1 >= 0) {
                int newV = hotel.getCurrentImageIndex() - 1;
                hotel.setCurrentImageIndex(newV);
            } else
                hotel.setCurrentImageIndex(maxIndImg - 1);
            updateSelectedImage();

            allImages.requestFocus();
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
            if(hotel.getCurrentImageIndex() + 1 < maxIndImg)
                hotel.setCurrentImageIndex(hotel.getCurrentImageIndex() + 1);
            else
                hotel.setCurrentImageIndex(0);
            updateSelectedImage();

            allImages.requestFocus();
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

        allImages = new ListView<>();
        AnchorPane.setBottomAnchor(allImages, 10.0);
        AnchorPane.setLeftAnchor(allImages, 15.0);
        AnchorPane.setRightAnchor(allImages, 15.0);
        allImages.getStyleClass().add("images-list-view");
        allImages.setOrientation(Orientation.HORIZONTAL);
        allImages.setPrefHeight(100);
        allImages.getSelectionModel().selectedIndexProperty().addListener((ob, oldV, newV) -> {
            hotel.setCurrentImageIndex(newV.intValue());
            updateSelectedImage();
        });

        for(int i=0; i<maxIndImg; i=i+1) {
            ImageView imageView = new ImageView(hotel.getImageByNumber(i));
            imageView.setFitHeight(94);
            imageView.setPreserveRatio(true);
            round(imageView, 30, 30, 30, 30);
            allImages.getItems().add(imageView);
        }

        allImages.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.UP)
                nextImageBtn.fire();
            if(e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.DOWN)
                prevImageBtn.fire();
            if(e.getCode() == KeyCode.ESCAPE)
                closeBtn.fire();
        });

        Platform.runLater(() -> {
            allImages.requestFocus();
        });

        anchorPane.getChildren().add(allImages);

        overSP.getChildren().add(anchorPane);

        overSP.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(!isVisible()) return;

            Point2D pointInPrevBtn = prevImageBtn.screenToLocal(event.getScreenX(), event.getScreenY());
            Point2D pointInNextBtn = nextImageBtn.screenToLocal(event.getScreenX(), event.getScreenY());
            Point2D pointInListView = allImages.screenToLocal(event.getScreenX(), event.getScreenY());
            Point2D pointInSelectedImageView = selectedIV.screenToLocal(event.getScreenX(), event.getScreenY());

            if ((pointInPrevBtn != null && prevImageBtn.contains(pointInPrevBtn))
                    || (pointInNextBtn != null && nextImageBtn.contains(pointInNextBtn))
                    || (pointInListView != null && allImages.contains(pointInListView))
                    || (pointInSelectedImageView != null && selectedIV.contains(pointInSelectedImageView)))
                return;
            else
                hide();
        });
    }

    public void show() {
        shadowPane.setVisible(true);
        anchorPane.setVisible(true);
        Platform.runLater(() -> {
            allImages.requestFocus();
        });
    }

    public void hide() {
        shadowPane.setVisible(false);
        anchorPane.setVisible(false);
    }

    private void updateSelectedImage() {
        selectedIV.setImage(hotel.getImageByNumber(hotel.getCurrentImageIndex()));
        allImages.scrollTo(hotel.getCurrentImageIndex());
        allImages.getSelectionModel().select(hotel.getCurrentImageIndex());
        countImageLB.setText(hotel.getCurrentImageIndex() + 1 + "/" + maxIndImg);
    }

    protected void updateSelectedHotel(Hotel hotel) {
        this.hotel = hotel;
        maxIndImg = hotel.getHotelPhotos().length;

        firstIV.setImage(hotel.getImageByNumber(0));
        secondIV.setImage(hotel.getImageByNumber(1));
        thirdIV.setImage(hotel.getImageByNumber(2));
        fourthIV.setImage(hotel.getImageByNumber(3));
        fifthIV.setImage(hotel.getImageByNumber(4));

        countHiddenImage.setText("+" + (maxIndImg - 5));
    }
}