package com.example.travel.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.YearMonth;

public class CustomCalendar extends Button {
    private static final double gridPaneWidth = 400;
    private static final double gridPaneHeight = 300;
    private static final double gridPaneFirstRowHeight = 60;

    // Текущие месяц и год
    private int currentMonth = LocalDate.now().getMonthValue();
    private int currentYear = LocalDate.now().getYear();

    // Единственный экземпляр календаря (singleton)
    protected static GridPane calendarInstance;
    protected static boolean isCalendarVisible = false;

    // Обработчик для закрытия по клику вне календаря
    private final EventHandler<MouseEvent> clickOutsideHandler;
    private final StackPane parentStackPane;

    public CustomCalendar(StackPane stackPane) {
        this.parentStackPane = stackPane;
        Platform.runLater(() -> setText("Даты"));
        getStyleClass().add("custom-calendar-btn");

        // Создаём обработчик для закрытия календаря
        clickOutsideHandler = event -> {
            if (calendarInstance != null && isCalendarVisible) {
                // Если клик НЕ по календарю и НЕ по кнопке
                if (!calendarInstance.getBoundsInParent().contains(event.getX(), event.getY())
                        && !getBoundsInParent().contains(event.getX(), event.getY())) {
                    hideCalendar();
                }
            }
        };

        setOnAction(e -> toggleCalendar());
    }

    private void toggleCalendar() {
        if (!isCalendarVisible) {
            showCalendar();
        } else {
            hideCalendar();
        }
    }

    private void showCalendar() {
        // Если календарь ещё не создан - создаём
        if (calendarInstance == null) {
            createCalendarInstance();
        }

        // Обновляем данные календаря
        updateCalendarData();

        // Позиционируем календарь под кнопкой
        setLayout();

        // Добавляем календарь и обработчик клика
        if (!parentStackPane.getChildren().contains(calendarInstance)) {
            parentStackPane.getChildren().add(calendarInstance);
        }
        parentStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler);

        calendarInstance.setVisible(true);
        calendarInstance.toFront(); // Поднимаем на передний план
        isCalendarVisible = true;
    }

    private void hideCalendar() {
        if (calendarInstance != null) {
            calendarInstance.setVisible(false);
            parentStackPane.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler);
            isCalendarVisible = false;
        }
    }

    private void createCalendarInstance() {
        calendarInstance = new GridPane();
        calendarInstance.setPrefWidth(gridPaneWidth);
        calendarInstance.setMaxWidth(gridPaneWidth);
        calendarInstance.setPrefHeight(gridPaneHeight);
        calendarInstance.setMaxHeight(gridPaneHeight);
        calendarInstance.getColumnConstraints().addAll(
                new ColumnConstraints(gridPaneWidth / 2),
                new ColumnConstraints(gridPaneWidth / 2)
        );
        calendarInstance.getRowConstraints().addAll(
                new RowConstraints(gridPaneFirstRowHeight),
                new RowConstraints(gridPaneHeight - gridPaneFirstRowHeight)
        );
        calendarInstance.getStyleClass().add("root-grid-pane");

        // Создаём начальный и конечный календари
        Calendar calendarStart = new Calendar();
        Calendar calendarEnd = new Calendar();

        GridPane.setColumnIndex(calendarStart, 0);
        GridPane.setRowIndex(calendarStart, 1);
        GridPane.setColumnIndex(calendarEnd, 1);
        GridPane.setRowIndex(calendarEnd, 1);

        // Кнопки переключения месяцев
        CustomCalendarBtn customCalendarBtnStartMonth = new CustomCalendarBtn(true, gridPaneFirstRowHeight);
        GridPane.setColumnIndex(customCalendarBtnStartMonth, 0);

        CustomCalendarBtn customCalendarBtnEndMonth = new CustomCalendarBtn(false, gridPaneFirstRowHeight);
        GridPane.setColumnIndex(customCalendarBtnEndMonth, 1);
        GridPane.setHalignment(customCalendarBtnEndMonth, HPos.RIGHT);

        // Сохраняем ссылки на компоненты для обновления
        calendarInstance.getProperties().put("calendarStart", calendarStart);
        calendarInstance.getProperties().put("calendarEnd", calendarEnd);
        calendarInstance.getProperties().put("startMonthBtn", customCalendarBtnStartMonth);
        calendarInstance.getProperties().put("endMonthBtn", customCalendarBtnEndMonth);

        // Настраиваем обработчики кнопок месяцев
        setupMonthButtons(customCalendarBtnStartMonth, customCalendarBtnEndMonth,
                calendarStart, calendarEnd);

        calendarInstance.getChildren().addAll(
                customCalendarBtnStartMonth,
                customCalendarBtnEndMonth,
                calendarStart,
                calendarEnd
        );

        calendarInstance.setManaged(false);
    }

    private void setupMonthButtons(CustomCalendarBtn startBtn, CustomCalendarBtn endBtn,
                                   Calendar startCal, Calendar endCal) {
        startBtn.setOnAction(e -> {
            currentMonth--;
            if (currentMonth < 1) {
                currentMonth = 12;
                currentYear--;
            }
            updateMonthButtons(startBtn, endBtn);
            updateCalendars(startCal, endCal);
        });

        endBtn.setOnAction(e -> {
            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
            updateMonthButtons(startBtn, endBtn);
            updateCalendars(startCal, endCal);
        });
    }

    private void updateMonthButtons(CustomCalendarBtn startBtn, CustomCalendarBtn endBtn) {
        startBtn.setMonth(currentMonth);
        startBtn.setYear(currentYear);
        startBtn.updateLabel();

        int nextMonth = currentMonth + 1;
        int nextMonthYear = currentYear;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextMonthYear++;
        }
        endBtn.setMonth(nextMonth);
        endBtn.setYear(nextMonthYear);
        endBtn.updateLabel();
    }

    private void updateCalendars(Calendar startCal, Calendar endCal) {
        startCal.createCalendar(YearMonth.of(currentYear, currentMonth));

        int nextMonth = currentMonth + 1;
        int nextMonthYear = currentYear;
        if (nextMonth > 12) {
            nextMonth = 1;
            nextMonthYear++;
        }
        endCal.createCalendar(YearMonth.of(nextMonthYear, nextMonth));
    }

    private void updateCalendarData() {
        if (calendarInstance != null) {
            Calendar startCal = (Calendar) calendarInstance.getProperties().get("calendarStart");
            Calendar endCal = (Calendar) calendarInstance.getProperties().get("calendarEnd");
            CustomCalendarBtn startBtn = (CustomCalendarBtn) calendarInstance.getProperties().get("startMonthBtn");
            CustomCalendarBtn endBtn = (CustomCalendarBtn) calendarInstance.getProperties().get("endMonthBtn");

            if (startCal != null && endCal != null && startBtn != null && endBtn != null) {
                updateMonthButtons(startBtn, endBtn);
                updateCalendars(startCal, endCal);
            }
        }
    }

    public void setLayout() {
        // Получаем координаты нижнего края кнопки в системе координат сцены
        Bounds boundsInScene = localToScene(getBoundsInLocal());
        double xInScene = boundsInScene.getMinX();
        double yInScene = boundsInScene.getMaxY() + 5; // небольшой отступ

        // Преобразуем координаты из сцены в координаты StackPane
        Point2D pointInStackPane = parentStackPane.sceneToLocal(xInScene, yInScene);

        // Устанавливаем позицию И размеры календаря
        calendarInstance.resizeRelocate(pointInStackPane.getX(), pointInStackPane.getY(), gridPaneWidth, gridPaneHeight);

        // Принудительно применяем CSS и выполняем компоновку
        calendarInstance.applyCss();
        calendarInstance.layout();
    }
}