package com.example.travel.controllers;

import com.example.travel.models.Hotel;
import com.example.travel.services.RoomService;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.util.function.Predicate;

public class NumberOfGuestsController extends Button {
    int adultsCount = 2;
    int childrenCount = 0;
    static int totalStatic = 2;

    private static final double GRID_PANE_WIDTH = 250;
    private static final double GRID_PANE_HEIGHT = 150;
    private static final double PADDING = 5;
    private static final double SIDE_PADDING = 10;

    private final Popup popup;
    private final GridPane contentGrid;
    private GuestCounter adultsCounter;
    private GuestCounter childrenCounter;

    public NumberOfGuestsController() {
        updateButtonText();
        getStyleClass().add("number-of-guests");

        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        contentGrid = createContent();
        popup.getContent().add(contentGrid);

        setOnAction(e -> togglePopup());
    }

    private GridPane createContent() {
        GridPane grid = new GridPane();
        grid.setMaxHeight(GRID_PANE_HEIGHT);
        grid.setMaxWidth(GRID_PANE_WIDTH);
        grid.setPadding(new Insets(PADDING, SIDE_PADDING, PADDING, SIDE_PADDING));

        double columnWidth = (GRID_PANE_WIDTH - 2 * SIDE_PADDING) / 2;
        double rowHeight = (GRID_PANE_HEIGHT - 2 * PADDING) / 2;

        grid.getColumnConstraints().addAll(
                new ColumnConstraints(columnWidth),
                new ColumnConstraints(columnWidth)
        );
        grid.getRowConstraints().addAll(
                new RowConstraints(rowHeight),
                new RowConstraints(rowHeight)
        );
        grid.getStyleClass().add("popup");

        // Взрослые
        VBox adultsVB = new VBox(3);
        adultsVB.setAlignment(Pos.CENTER_LEFT);
        Label adultsLB = new Label("Взрослые");
        adultsLB.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label adultsInfoLB = new Label("Старше 18 лет");
        adultsInfoLB.setStyle("-fx-font-size: 13; -fx-text-fill: gray;");
        adultsVB.getChildren().addAll(adultsLB, adultsInfoLB);
        GridPane.setRowIndex(adultsVB, 0);
        GridPane.setColumnIndex(adultsVB, 0);

        // Дети
        VBox childrenVB = new VBox(3);
        childrenVB.setAlignment(Pos.CENTER_LEFT);
        Label childrenLB = new Label("Дети");
        childrenLB.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label childrenInfoLB = new Label("До 18 лет");
        childrenInfoLB.setStyle("-fx-font-size: 13; -fx-text-fill: gray;");
        childrenVB.getChildren().addAll(childrenLB, childrenInfoLB);
        GridPane.setRowIndex(childrenVB, 1);
        GridPane.setColumnIndex(childrenVB, 0);

        // Счётчики
        adultsCounter = new GuestCounter(true, this);
        childrenCounter = new GuestCounter(false, this);
        adultsCounter.setCount(adultsCount);
        childrenCounter.setCount(childrenCount);

        GridPane.setColumnIndex(adultsCounter, 1);
        GridPane.setRowIndex(adultsCounter, 0);
        GridPane.setColumnIndex(childrenCounter, 1);
        GridPane.setRowIndex(childrenCounter, 1);

        grid.getChildren().addAll(adultsVB, childrenVB, adultsCounter, childrenCounter);
        return grid;
    }

    private void togglePopup() {
        if (popup.isShowing()) {
            popup.hide();
        } else {
            // Обновляем счётчики актуальными значениями
            adultsCounter.setCount(adultsCount);
            childrenCounter.setCount(childrenCount);

            Bounds bounds = localToScreen(getBoundsInLocal());
            double x = bounds.getMinX();
            double y = bounds.getMaxY() + 5;
            popup.show(this, x, y);
        }
    }

    // Метод, вызываемый счётчиками при изменении
    public void onCountChanged(int newAdults, int newChildren) {
        adultsCount = newAdults;
        childrenCount = newChildren;
        updateButtonText();
    }

    private void updateButtonText() {
        int total = adultsCount + childrenCount;
        totalStatic = total;
        Predicate<Hotel> countGuestsPredicate = hotel ->
                new RoomService().getMaxRoomSleepingPlaces(hotel.getIdHotel()) >= total;

        PopularDestinationsController.filteres.put("CountGuests", countGuestsPredicate);
        PopularDestinationsController.updatePredicateFilteredHotels();
        if (total == 1)
            setText("1 гость");
        else if (total >= 2 && total <= 4)
            setText(total + " гостя");
        else
            setText(total + " гостей");
    }
}