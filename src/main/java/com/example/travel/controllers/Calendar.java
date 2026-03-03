package com.example.travel.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public class Calendar extends TilePane {
    private Map<Button, Boolean> buttonsCalendar = CustomCalendar.getButtonsCalendar();;
    private Calendar() {
        setPadding(new Insets(10));
        setPrefColumns(7);
        // Фиксированные размеры ячеек, чтобы они точно были видны
        setPrefTileWidth(23);
        setPrefTileHeight(30);
    }

    public static Calendar getInstance(YearMonth month) {
        // Если уже есть в кэше — возвращаем
        if (CustomCalendar.getCalendarMap().containsKey(month)) {
            return CustomCalendar.getCalendarMap().get(month);
        }
        // Иначе создаём новый, инициализируем и кладём в кэш
        Calendar calendar = new Calendar();
        calendar.createCalendar(month);
        CustomCalendar.getCalendarMap().put(month, calendar);
        return calendar;
    }

    private void createCalendar(YearMonth currentMonth) {
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
            dayButton.setAlignment(Pos.CENTER);
            dayButton.getStyleClass().add("custom-button-double");
            buttonsCalendar.put(dayButton, false);

            dayButton.setOnAction(e -> {
                if(CustomCalendar.startSelectedBtn != null && CustomCalendar.startSelectedBtn.equals(dayButton)) {
                    CustomCalendar.endSelectedBtn = null;
                    updateStyleRange();
                    return;
                }

                if(CustomCalendar.endSelectedBtn != null && CustomCalendar.endSelectedBtn.equals(dayButton)) {
                    CustomCalendar.startSelectedBtn = CustomCalendar.endSelectedBtn;
                    CustomCalendar.endSelectedBtn = null;
                    updateStyleRange();
                    return;
                }

                if(CustomCalendar.startSelectedBtn != null && CustomCalendar.endSelectedBtn != null) {
                    CustomCalendar.startSelectedBtn = null;
                    CustomCalendar.endSelectedBtn = null;
                }

                if(CustomCalendar.startSelectedBtn == null) {
                    CustomCalendar.startSelectedBtn = dayButton;
                } else {
                    CustomCalendar.endSelectedBtn = dayButton;
                }

                updateStyleRange();
            });
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
            nextMonthBtn.setOnAction(e -> {
                if(CustomCalendar.startSelectedBtn != null && CustomCalendar.startSelectedBtn.equals(nextMonthBtn)) {
                    CustomCalendar.endSelectedBtn = null;
                    updateStyleRange();
                    return;
                }

                if(CustomCalendar.endSelectedBtn != null && CustomCalendar.endSelectedBtn.equals(nextMonthBtn)) {
                    CustomCalendar.startSelectedBtn = CustomCalendar.endSelectedBtn;
                    CustomCalendar.endSelectedBtn = null;
                    updateStyleRange();
                    return;
                }

                if(CustomCalendar.startSelectedBtn != null && CustomCalendar.endSelectedBtn != null) {
                    CustomCalendar.startSelectedBtn = null;
                    CustomCalendar.endSelectedBtn = null;
                }

                if(CustomCalendar.startSelectedBtn == null) {
                    CustomCalendar.startSelectedBtn = nextMonthBtn;
                } else {
                    CustomCalendar.endSelectedBtn = nextMonthBtn;
                }

                updateStyleRange();
            });
            getChildren().add(nextMonthBtn);
        }
    }

    private Button getFirstBtn(Button btn1, Button btn12) {
        for(Button button : buttonsCalendar.keySet()) {
            if (button.equals(btn1) || button.equals(btn12))
                return button;
        }
        return btn1;
    }

    private void toggleButtons() {
        Button btn = CustomCalendar.startSelectedBtn;
        CustomCalendar.startSelectedBtn = CustomCalendar.endSelectedBtn;
        CustomCalendar.endSelectedBtn = btn;
    }

    protected static void updateStyleRange() {
        for (Button btn : CustomCalendar.getButtonsCalendar().keySet()) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("custom-button-double");
            CustomCalendar.getButtonsCalendar().put(btn, false);
        }

        if (CustomCalendar.startSelectedBtn != null) {
            CustomCalendar.startSelectedBtn.getStyleClass().clear();
            CustomCalendar.startSelectedBtn.getStyleClass().add("custom-button-double-select");
            CustomCalendar.getButtonsCalendar().put(CustomCalendar.startSelectedBtn, true);
        }
        if (CustomCalendar.endSelectedBtn != null) {
            CustomCalendar.endSelectedBtn.getStyleClass().clear();
            CustomCalendar.endSelectedBtn.getStyleClass().add("custom-button-double-select");
            CustomCalendar.getButtonsCalendar().put(CustomCalendar.endSelectedBtn, true);
        }

        if (CustomCalendar.startSelectedBtn != null && CustomCalendar.endSelectedBtn != null) {
            for (Button btn : CustomCalendar.getButtonsCalendar().keySet()) {
                btn.getStyleClass().remove("custom-button-double-stack");
            }

            boolean inRange = false;
            for (Button btn : CustomCalendar.getButtonsCalendar().keySet()) {
                if (btn.equals(CustomCalendar.startSelectedBtn)) {
                    inRange = true;
                    continue;
                }
                if (btn.equals(CustomCalendar.endSelectedBtn)) {
                    inRange = false;
                    continue; // конечная тоже select
                }
                if (inRange) {
                    btn.getStyleClass().add("custom-button-double-stack");
                }
            }
        }
    }
}