package com.example.travel.util;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class HelpFullClass {
    public static String getRussianMonthName(int month) {
        switch (month) {
            case 1: return "Январь";
            case 2: return "Февраль";
            case 3: return "Март";
            case 4: return "Апрель";
            case 5: return "Май";
            case 6: return "Июнь";
            case 7: return "Июль";
            case 8: return "Август";
            case 9: return "Сентябрь";
            case 10: return "Октябрь";
            case 11: return "Ноябрь";
            case 12: return "Декабрь";
            default: return "Не корректный номер месяца";
        }
    }

    public static HBox createLoadHB(String text) {
        HBox loadHB = new HBox();
        loadHB.setPrefHeight(60);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 18px;");
        label.setPrefHeight(60);

        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(HelpFullClass.class.getResourceAsStream("/images/loading.gif"))));
        imageView.setFitWidth(80);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);
        HBox.setMargin(imageView, new Insets(2, 0, 0, 5));

        loadHB.getChildren().addAll(label, imageView);

        return loadHB;
    }
}
