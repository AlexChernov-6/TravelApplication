package com.example.travel.controllers;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class SorterWindow extends VBox {

    private final Popup popup;

    private boolean visible;

    public SorterWindow() {
        getStyleClass().add("popup");
        setPadding(new Insets(10, 15, 20, 15));
        setSpacing(20);
        setPrefWidth(300); // фиксированная ширина, высота подстроится

        Label sortLabel = new Label("Сортировка");
        sortLabel.getStyleClass().add("sort-label");

        CustomSelectedBtn defaultSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.BY_DEFAULT, this);
        CustomSelectedBtn cheaperSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.CHEAPER, this);
        CustomSelectedBtn moreExpensiveSort = new CustomSelectedBtn(PopularDestinationsController.SortedContext.MORE_EXPENSIVE, this);

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
            visible = true;
        }
    }

    public void hide() {
        visible = false;
        popup.hide();
    }

    public Popup getPopup() {
        return popup;
    }

    public void setShape(Node... nodes) {
        getChildren().removeIf(node -> node instanceof CustomSelectedBtn);

        getChildren().addAll(nodes);
    }

    public boolean isVisible1() {
        return visible;
    }

    public void setVisible1(boolean visible) {
        this.visible = visible;
    }
}