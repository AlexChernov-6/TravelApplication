package com.example.travel.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.time.LocalDate;
import java.time.YearMonth;

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

    public CustomCalendar() {
        setText("Даты");
        getStyleClass().add("custom-calendar-btn");

        // Создаём Popup
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        // Создаём содержимое календаря
        calendarGrid = createCalendarGrid();
        popup.getContent().add(calendarGrid);

        setOnAction(e -> toggleCalendar());
    }

    private GridPane createCalendarGrid() {
        GridPane grid = new GridPane();
        grid.setPrefWidth(GRID_PANE_WIDTH);
        grid.setMaxWidth(GRID_PANE_WIDTH);
        grid.setPrefHeight(GRID_PANE_HEIGHT);
        grid.setMaxHeight(GRID_PANE_HEIGHT);
        grid.getColumnConstraints().addAll(
                new ColumnConstraints(GRID_PANE_WIDTH / 2),
                new ColumnConstraints(GRID_PANE_WIDTH / 2)
        );
        grid.getRowConstraints().addAll(
                new RowConstraints(GRID_PANE_FIRST_ROW_HEIGHT),
                new RowConstraints(GRID_PANE_HEIGHT - GRID_PANE_FIRST_ROW_HEIGHT)
        );
        grid.getStyleClass().add("popup");

        calendarStart = new Calendar();
        calendarEnd = new Calendar();
        GridPane.setColumnIndex(calendarStart, 0);
        GridPane.setRowIndex(calendarStart, 1);
        GridPane.setColumnIndex(calendarEnd, 1);
        GridPane.setRowIndex(calendarEnd, 1);

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

        grid.getChildren().addAll(startMonthBtn, endMonthBtn, calendarStart, calendarEnd);
        return grid;
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
        calendarStart.createCalendar(YearMonth.of(currentYear, currentMonth));

        int nextMonth = currentMonth + 1;
        int nextMonthYear = currentYear;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextMonthYear++;
        }
        calendarEnd.createCalendar(YearMonth.of(nextMonthYear, nextMonth));
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
}