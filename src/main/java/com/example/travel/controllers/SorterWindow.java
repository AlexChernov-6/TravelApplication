package com.example.travel.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class SorterWindow extends VBox {

    private final Popup popup;
    private boolean alreadyHasHandler = false;

    public SorterWindow() {
        getStyleClass().add("popup");
        setPadding(new Insets(10, 15, 20, 15));
        setSpacing(20);
        setPrefWidth(300); // фиксированная ширина, высота подстроится

        Label sortLabel = new Label("Сортировка");
        sortLabel.getStyleClass().add("sort-label");

        Button defaultSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.BY_DEFAULT, this);
        Button cheaperSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.CHEAPER, this);
        Button moreExpensiveSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.MORE_EXPENSIVE, this);

        getChildren().addAll(sortLabel, defaultSort, cheaperSort, moreExpensiveSort);

        popup = new Popup();
        popup.getContent().add(this);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
    }

    public void show(Button owner) {
        if (!popup.isShowing()) {
            // Позиционируем под кнопкой
            Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
            double x = bounds.getMinX() - 20;
            double y = bounds.getMaxY() - 15;
            popup.show(owner, x, y);
        }
    }

    public void hide() {
        popup.hide();
    }

    public Popup getPopup() {
        return popup;
    }
}