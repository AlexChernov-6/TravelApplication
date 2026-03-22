package com.example.travel.controllers;

import com.example.travel.models.Review;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.travel.util.HelpFullClass.getRussianMonthName;

public class ReviewCell extends ListCell<Review> {
    private final GridPane rootGP;
    private final Label nameUser, reviewDate, reviewComment;
    private final HBox estimationsHB;
    private final ImageView[] starViews = new ImageView[5];

    private static final Image FILLED_STAR;
    private static final Image EMPTY_STAR;

    static {
        FILLED_STAR = new Image(Objects.requireNonNull(
                ReviewCell.class.getResourceAsStream("/images/star.png")));
        EMPTY_STAR = new Image(Objects.requireNonNull(
                ReviewCell.class.getResourceAsStream("/images/starNoneSelected.png")));
    }

    public ReviewCell() {
        rootGP = new GridPane();
        rootGP.setStyle("-fx-background-color: rgba(255,255,255); -fx-background-radius: 12px;");
        rootGP.setPadding(new Insets(20));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMaxWidth(USE_PREF_SIZE);

        ColumnConstraints col2 = new ColumnConstraints();

        rootGP.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPrefHeight(20);
        row1.setVgrow(Priority.NEVER);

        RowConstraints row2 = new RowConstraints();
        row2.setPrefHeight(15);
        row2.setVgrow(Priority.NEVER);

        RowConstraints row3 = new RowConstraints();

        rootGP.getRowConstraints().addAll(row1, row2, row3);

        nameUser = new Label("Имя пользователя неизвестно");
        nameUser.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        reviewDate = new Label("00 месяц 0000");
        reviewDate.setStyle("-fx-text-fill: rgba(170, 170, 170);");
        GridPane.setRowIndex(reviewDate, 1);

        estimationsHB = new HBox(5);
        GridPane.setColumnIndex(estimationsHB, 1);
        GridPane.setRowSpan(estimationsHB, 2);
        estimationsHB.setAlignment(Pos.CENTER_LEFT);
        estimationsHB.setPadding(new Insets(0, 0, 0, 10));

        for (int i = 0; i < 5; i++) {
            starViews[i] = new ImageView();
            starViews[i].setFitWidth(30);
            starViews[i].setFitHeight(30);
            starViews[i].setPreserveRatio(true);
            estimationsHB.getChildren().add(starViews[i]);
        }

        reviewComment = new Label();
        reviewComment.setStyle("-fx-font-size: 14px");
        reviewComment.setWrapText(true);
        GridPane.setRowIndex(reviewComment, 2);
        GridPane.setColumnSpan(reviewComment, 2);
        GridPane.setMargin(reviewComment, new Insets(5, 0, 0, 0));

        rootGP.getChildren().addAll(nameUser, reviewDate, estimationsHB, reviewComment);
    }

    @Override
    protected void updateItem(Review review, boolean empty) {
        super.updateItem(review, empty);

        if (empty || review == null) {
            setGraphic(null);
            setText(null);
        } else {
            updateStateCell(review);
            setGraphic(rootGP);
        }
    }

    private void updateStateCell(Review review) {
        nameUser.setText(review.getUser().getUserSecondName());
        LocalDate lDT = review.getCratedAt().toLocalDate();
        reviewDate.setText(lDT.getDayOfMonth() + " " + getRussianMonthName(lDT.getMonth().getValue()).toLowerCase()
                + " " + lDT.getYear());
        reviewComment.setText(review.getComment());

        createEstimations(review.getRating());
    }

    private void createEstimations(int value) {
        for (int i = 0; i < 5; i++) {
            starViews[i].setImage(i < value ? FILLED_STAR : EMPTY_STAR);
        }
    }
}