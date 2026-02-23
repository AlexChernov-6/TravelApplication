package com.example.travel.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.travel.util.HelpFullClass.getRussianMonthName;

public class CustomCalendarBtn extends Button {
    private int month = LocalDate.now().getMonthValue();
    private int year = LocalDate.now().getYear();
    private final Label monthLb;

    public CustomCalendarBtn(boolean isStartMonth, double prefHeight) {
        getStyleClass().add("custom-button");
        if(isStartMonth) {
            if (month + 1 <= 12)
                month = month + 1;
            else {
                month = 1;
                year = year + 1;
            }
        }
        ImageView imageView = new ImageView(new Image(isStartMonth ? Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/left-arrow.png")) : Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/right-arrow.png"))));
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);

        monthLb = new Label();
        HBox.setMargin(monthLb, new Insets(0, 10, 0, 10));
        updateLabel();

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefHeight(prefHeight);
        if(isStartMonth)
            hBox.getChildren().addAll(imageView, monthLb);
        else
            hBox.getChildren().addAll(monthLb, imageView);
        setGraphic(hBox);
    }

    public void updateLabel() {
        monthLb.setText(String.format("%s %d", getRussianMonthName(month), year));
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
