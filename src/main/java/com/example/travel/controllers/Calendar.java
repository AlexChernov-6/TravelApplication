package com.example.travel.controllers;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Calendar extends TilePane {
    public Calendar() {
        setPadding(new Insets(10));
        setPrefColumns(7);
        // Фиксированные размеры ячеек, чтобы они точно были видны
        setPrefTileWidth(23);
        setPrefTileHeight(30);
    }

    public void createCalendar(YearMonth currentMonth) {
        getChildren().clear();

        Locale russian = Locale.forLanguageTag("ru-RU");

        // Заголовки дней недели
        for (DayOfWeek day : DayOfWeek.values()) {
            String shortName = day.getDisplayName(TextStyle.SHORT, russian);
            Label dayLabel = new Label(shortName);
            dayLabel.setStyle("-fx-font-weight: bold;");
            getChildren().add(dayLabel);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstDayOfWeekValue = firstOfMonth.getDayOfWeek().getValue(); // 1 = пн, 7 = вс

        // Пустые ячейки перед первым числом
        for (int i = 1; i < firstDayOfWeekValue; i++) {
            Button emptyBtn = new Button();
            emptyBtn.setVisible(false);
            emptyBtn.setDisable(true);
            emptyBtn.getStyleClass().add("custom-button-double");
            getChildren().add(emptyBtn);
        }

        // Дни текущего месяца
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMaxWidth(Double.MAX_VALUE);
            dayButton.getStyleClass().add("custom-button-double");
            getChildren().add(dayButton);
        }

        // Дни следующего месяца (для заполнения сетки 6×7)
        int totalCells = getChildren().size();
        int remainingCells = 42 - totalCells; // 42 = 6 строк * 7 колонок
        for (int i = 1; i <= remainingCells; i++) {
            Button nextMonthBtn = new Button(String.valueOf(i));
            nextMonthBtn.setStyle("-fx-text-fill: lightgray;");
            nextMonthBtn.setDisable(true);
            nextMonthBtn.getStyleClass().add("custom-button-double");
            getChildren().add(nextMonthBtn);
        }
    }
}