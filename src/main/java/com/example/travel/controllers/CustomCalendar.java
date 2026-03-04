package com.example.travel.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static com.example.travel.util.HelpFullClass.getRussianMonthName;

public class CustomCalendar extends Button {
    private static final double GRID_PANE_WIDTH = 400;
    private static final double GRID_PANE_HEIGHT = 300;
    private static final double GRID_PANE_FIRST_ROW_HEIGHT = 60;

    private int currentMonth = LocalDate.now().getMonthValue();
    private int currentYear = LocalDate.now().getYear();

    private final Popup popup;
    private final GridPane calendarGrid;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private CustomCalendarBtn startMonthBtn;
    private CustomCalendarBtn endMonthBtn;

    private static Map<Button, Boolean> buttonsCalendar = new LinkedHashMap<>();

    private static ObjectProperty<Button> startSelectedBtn = new SimpleObjectProperty<>();
    private static ObjectProperty<Button> endSelectedBtn = new SimpleObjectProperty<>();

    private static Map<YearMonth, Calendar> calendarMap = new HashMap<>();

    public CustomCalendar() {
        setText("Даты");
        getStyleClass().add("custom-calendar-btn");

        // Создаём Popup
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        // Создаём содержимое календаря
        calendarGrid = new GridPane();
        calendarGrid.setPrefWidth(GRID_PANE_WIDTH);
        calendarGrid.setMaxWidth(GRID_PANE_WIDTH);
        calendarGrid.setPrefHeight(GRID_PANE_HEIGHT);
        calendarGrid.setMaxHeight(GRID_PANE_HEIGHT);
        calendarGrid.getColumnConstraints().addAll(
                new ColumnConstraints(GRID_PANE_WIDTH / 2),
                new ColumnConstraints(GRID_PANE_WIDTH / 2)
        );
        calendarGrid.getRowConstraints().addAll(
                new RowConstraints(GRID_PANE_FIRST_ROW_HEIGHT),
                new RowConstraints(GRID_PANE_HEIGHT - GRID_PANE_FIRST_ROW_HEIGHT)
        );
        calendarGrid.getStyleClass().add("popup");

        updateCalendars();

        startMonthBtn = new CustomCalendarBtn(true, GRID_PANE_FIRST_ROW_HEIGHT);
        endMonthBtn = new CustomCalendarBtn(false, GRID_PANE_FIRST_ROW_HEIGHT);
        GridPane.setColumnIndex(startMonthBtn, 0);
        GridPane.setColumnIndex(endMonthBtn, 1);
        GridPane.setHalignment(endMonthBtn, HPos.RIGHT);

        // Настраиваем кнопки переключения месяцев
        startMonthBtn.setOnAction(e -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthButtons();
            updateCalendars();
        });

        endMonthBtn.setOnAction(e -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthButtons();
            updateCalendars();
        });

        calendarGrid.getChildren().addAll(startMonthBtn, endMonthBtn);

        popup.getContent().add(calendarGrid);
        setOnAction(e -> toggleCalendar());

        startSelectedBtn.addListener((ob, oldV, newV) -> {
            Platform.runLater(() -> {
                if(oldV != null && oldV.equals(getEndSelectedBtn()))
                    setText(getTextByUserData(newV) + " - " + getTextByUserData(getEndSelectedBtn()));
                else if(newV != null && oldV != newV && getEndSelectedBtn() == null) {
                    setText(getTextByUserData(newV));
                }
            });
        });

        endSelectedBtn.addListener((ob, oldV, newV) -> {
            Platform.runLater(() -> {
                if(newV != null && oldV != newV) {
                    setText(getTextByUserData(getStartSelectedBtn()) + " - " + getTextByUserData(newV));
                }
            });
        });
    }

    private void updateMonthButtons() {
        startMonthBtn.setMonth(currentMonth);
        startMonthBtn.setYear(currentYear);
        startMonthBtn.updateLabel();

        int nextMonth = currentMonth + 1;
        int nextMonthYear = currentYear;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextMonthYear++;
        }
        endMonthBtn.setMonth(nextMonth);
        endMonthBtn.setYear(nextMonthYear);
        endMonthBtn.updateLabel();
    }

    private void updateCalendars() {
        Calendar newStart = Calendar.getInstance(YearMonth.of(currentYear, currentMonth));
        int nextMonth = currentMonth + 1;
        int nextMonthYear = currentYear;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextMonthYear++;
        }
        Calendar newEnd = Calendar.getInstance(YearMonth.of(nextMonthYear, nextMonth));

        if (calendarStart != null) {
            calendarGrid.getChildren().remove(calendarStart);
        }
        if (calendarEnd != null) {
            calendarGrid.getChildren().remove(calendarEnd);
        }

        // Присваиваем новые
        calendarStart = newStart;
        calendarEnd = newEnd;

        // Добавляем в grid с правильными координатами
        GridPane.setConstraints(calendarStart, 0, 1);
        GridPane.setConstraints(calendarEnd, 1, 1);
        calendarGrid.getChildren().addAll(calendarStart, calendarEnd);

        // Обновляем стили выделения для нового диапазона
        Calendar.updateStyleRange();
    }

    private void toggleCalendar() {
        if (popup.isShowing()) {
            popup.hide();
        } else {
            // Обновляем данные перед показом
            updateMonthButtons();
            updateCalendars();

            // Позиционируем под кнопкой
            Bounds bounds = localToScreen(getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMaxY() + 5;
            popup.show(this, x, y);
        }
    }

    public static Map<Button, Boolean> getButtonsCalendar() {
        return buttonsCalendar;
    }

    public static Map<YearMonth, Calendar> getCalendarMap() {
        return calendarMap;
    }

    public static Button getStartSelectedBtn() {
        return startSelectedBtn.get();
    }

    public static void setStartSelectedBtn(Button btn) {
        startSelectedBtn.set(btn);
    }

    public static Button getEndSelectedBtn() {
        return endSelectedBtn.get();
    }

    public static void setEndSelectedBtn(Button btn) {
        endSelectedBtn.set(btn);
    }

    private String getTextByUserData(Button button) {
        return ((LocalDate) button.getUserData()).getDayOfMonth() + " "
                + getRussianMonthName(((LocalDate) button.getUserData()).getMonthValue()).substring(0, 3).toLowerCase();
    }
}