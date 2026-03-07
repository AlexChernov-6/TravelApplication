package com.example.travel.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

public class HelpFullClass {
    private ScrollBar vBar;

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

    public static int getNumberMonthWithRussianName(String month) {
        switch (month.toLowerCase()) {
            case "январь": return 1;
            case "февраль": return 2;
            case "март": return 3;
            case "апрель": return 4;
            case "май": return 5;
            case "июнь": return 6;
            case "июль": return 7;
            case "август": return 8;
            case "сентябрь": return 9;
            case "октябрь": return 10;
            case "ноябрь": return 11;
            case "декабрь": return 12;
            default: return 0;
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

    public void scrollPaneAnimation(ScrollPane scrollPane) {
        Platform.runLater(() -> {
            Node verticalBar = scrollPane.lookup(".scroll-bar:vertical");
            if (verticalBar instanceof ScrollBar) {
                vBar = (ScrollBar) verticalBar;
                vBar.setOpacity(0.0);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(70), vBar);
            fadeIn.setToValue(1.0);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(600), vBar);
            fadeOut.setToValue(0.0);

            PauseTransition hideTimer = new PauseTransition(Duration.seconds(2));
            hideTimer.setOnFinished(event -> {
                // По окончании таймера запускаем плавное исчезновение
                // Останавливаем возможную анимацию появления
                fadeIn.stop();
                fadeOut.playFromStart();
            });

            Runnable showBar = () -> {
                // Останавливаем текущие анимации
                fadeOut.stop();
                // Запускаем появление, если оно ещё не выполняется или opacity не полная
                if (vBar.getOpacity() < 1.0) {
                    fadeIn.playFromStart();
                }
                // Сбрасываем таймер скрытия
                hideTimer.stop();
                hideTimer.playFromStart();
            };

            vBar.setOnMouseEntered(e -> showBar.run());

            vBar.setOnMouseExited(e -> {
                hideTimer.stop();
                hideTimer.playFromStart();
            });

            scrollPane.vvalueProperty().addListener((ob, oldV, newV) -> {
                if (oldV.doubleValue() != newV.doubleValue()) {
                    showBar.run();
                }
            });
        });
    }

    public static void openWebPage(String urlAddress) {
        try {
            //Создаётся экземпляр рабочего стола
            Desktop desktop = Desktop.getDesktop();
            //Проверяем, поддерживает ли ОС пользователя открытие браузера
            if (desktop.isSupported(Desktop.Action.BROWSE))
                //Открывает URI в браузере по переданному адресу
                desktop.browse(new URI(urlAddress));
        } catch (Exception e) {
            e.printStackTrace();
            //showError("Не удалось открыть браузер");
        }
    }
}
