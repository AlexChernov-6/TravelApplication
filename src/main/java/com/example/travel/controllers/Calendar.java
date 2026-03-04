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
import java.util.*;

public class Calendar extends TilePane {
    private static Map<Button, Boolean> buttonsCalendar = CustomCalendar.getButtonsCalendar();;
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
            LocalDate date = currentMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMaxWidth(Double.MAX_VALUE);
            dayButton.setAlignment(Pos.CENTER);
            dayButton.setUserData(date);
            dayButton.setOnMouseEntered(e -> {
                setHoverStyle(dayButton);
            });
            buttonsCalendar.put(dayButton, false);
            dayButton.setOnAction(e -> handleDateSelection(dayButton));
            getChildren().add(dayButton);
        }
    }

    protected static void updateStyleRange() {
        if (buttonsCalendar == null) return;

        // Сброс стилей у всех кнопок
        for (Button btn : buttonsCalendar.keySet()) {
            btn.getStyleClass().clear();
            btn.getStyleClass().add("custom-button-double");
            btn.setDisable(false);

            LocalDate date = (LocalDate) btn.getUserData();
            if(date.isBefore(LocalDate.now().plusDays(1))) {
                btn.getStyleClass().clear();
                btn.getStyleClass().add("custom-button-double-disable");
                btn.setDisable(true);
            } else {
                if(CustomCalendar.getStartSelectedBtn() == null && date.isAfter(LocalDate.now().plusYears(1))) {
                    btn.getStyleClass().clear();
                    btn.getStyleClass().add("custom-button-double-disable");
                    btn.setDisable(true);
                }
                else if(CustomCalendar.getStartSelectedBtn() != null
                        && (date.isAfter(((LocalDate) CustomCalendar.getStartSelectedBtn().getUserData())
                        .plusMonths(1)) || date.isBefore(((LocalDate) CustomCalendar.getStartSelectedBtn().getUserData())
                        .minusMonths(1)))) {
                    btn.getStyleClass().clear();
                    btn.getStyleClass().add("custom-button-double-disable");
                    btn.setDisable(true);
                }
            }
        }

        // Выделение начальной и конечной дат
        if (CustomCalendar.getStartSelectedBtn() != null) {
            CustomCalendar.getStartSelectedBtn().getStyleClass().clear();
            CustomCalendar.getStartSelectedBtn().getStyleClass().add("custom-button-double-select");
        }
        if (CustomCalendar.getEndSelectedBtn() != null) {
            CustomCalendar.getEndSelectedBtn().getStyleClass().clear();
            CustomCalendar.getEndSelectedBtn().getStyleClass().add("custom-button-double-select");
        }

        // Если выбраны обе даты, выделяем промежуток
        if (CustomCalendar.getStartSelectedBtn() != null && CustomCalendar.getEndSelectedBtn() != null) {
            // Удаляем старый класс диапазона у всех
            for (Button btn : buttonsCalendar.keySet()) {
                btn.getStyleClass().remove("custom-button-double-stack");
            }

            // Собираем все видимые кнопки с датами
            List<Button> visibleButtons = new ArrayList<>();
            for (Button btn : buttonsCalendar.keySet()) {
                if (btn.isVisible() && btn.getUserData() instanceof LocalDate) {
                    visibleButtons.add(btn);
                }
            }
            // Сортируем по дате
            visibleButtons.sort(Comparator.comparing(b -> (LocalDate) b.getUserData()));

            LocalDate startDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            LocalDate endDate = (LocalDate) CustomCalendar.getEndSelectedBtn().getUserData();

            boolean inRange = false;
            for (Button btn : visibleButtons) {
                LocalDate date = (LocalDate) btn.getUserData();
                if (date.equals(startDate)) {
                    inRange = true;
                    continue; // сама start уже имеет select
                }
                if (date.equals(endDate)) {
                    inRange = false;
                    continue; // end тоже select
                }
                if (inRange) {
                    btn.getStyleClass().add("custom-button-double-stack");
                }
            }
        }
    }

    private void setHoverStyle(Button button) {
        if(CustomCalendar.getStartSelectedBtn() != null && CustomCalendar.getEndSelectedBtn() == null) {
            for (Button btn : buttonsCalendar.keySet()) {
                btn.getStyleClass().remove("custom-button-double-stack");
            }

            List<Button> visibleButtons = new ArrayList<>();
            for (Button btn : buttonsCalendar.keySet()) {
                if (btn.isVisible() && btn.getUserData() instanceof LocalDate) {
                    visibleButtons.add(btn);
                }
            }

            visibleButtons.sort(Comparator.comparing(b -> (LocalDate) b.getUserData()));

            LocalDate startDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            LocalDate endDate = (LocalDate) button.getUserData();

            if(endDate.isBefore(startDate)) {
                startDate = endDate;
                endDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            }


            boolean inRange = false;
            for (Button btn : visibleButtons) {
                LocalDate date = (LocalDate) btn.getUserData();
                if (date.equals(startDate)) {
                    inRange = true;
                    continue; // сама start уже имеет select
                }
                if (date.equals(endDate)) {
                    inRange = false;
                    continue; // end тоже select
                }
                if (inRange) {
                    btn.getStyleClass().add("custom-button-double-stack");
                }
            }
        }
    }

    private void handleDateSelection(Button clicked) {
        LocalDate clickedDate = (LocalDate) clicked.getUserData();

        // Если нажата уже выбранная начальная дата
        if (clicked.equals(CustomCalendar.getStartSelectedBtn())) {
            CustomCalendar.setEndSelectedBtn(null);
            updateStyleRange();
            return;
        }
        // Если нажата уже выбранная конечная дата
        if (clicked.equals(CustomCalendar.getEndSelectedBtn())) {
            CustomCalendar.setStartSelectedBtn(CustomCalendar.getEndSelectedBtn());
            CustomCalendar.setEndSelectedBtn(null);
            updateStyleRange();
            return;
        }

        // Если выбраны обе даты — сбрасываем и начинаем новый выбор
        if (CustomCalendar.getStartSelectedBtn() != null && CustomCalendar.getEndSelectedBtn() != null) {
            CustomCalendar.setStartSelectedBtn(clicked);
            CustomCalendar.setEndSelectedBtn(null);
        }
        // Если начальная ещё не выбрана
        else if (CustomCalendar.getStartSelectedBtn() == null) {
            CustomCalendar.setStartSelectedBtn(clicked);
        }
        // Если выбрана только начальная — выбираем конечную и упорядочиваем
        else {
            LocalDate startDate = (LocalDate) CustomCalendar.getStartSelectedBtn().getUserData();
            if (startDate.isAfter(clickedDate)) {
                // Меняем местами, чтобы start был раньше
                CustomCalendar.setEndSelectedBtn(CustomCalendar.getStartSelectedBtn());
                CustomCalendar.setStartSelectedBtn(clicked);
            } else {
                CustomCalendar.setEndSelectedBtn(clicked);
            }
        }
        updateStyleRange();
    }
}