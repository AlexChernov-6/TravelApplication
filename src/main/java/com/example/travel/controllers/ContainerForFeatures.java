package com.example.travel.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ContainerForFeatures extends VBox {
    private final HBox firstRow;
    private final HBox secondRow;
    private final Label overflowCounter;
    private final ObservableList<Node> allFeatures = FXCollections.observableArrayList();

    public ContainerForFeatures() {
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(2);
        setFillWidth(true);
        setMaxWidth(Double.MAX_VALUE);

        firstRow = new HBox(2);
        firstRow.setAlignment(Pos.CENTER_LEFT);
        firstRow.setFillHeight(false);

        secondRow = new HBox(2);
        secondRow.setAlignment(Pos.CENTER_LEFT);
        secondRow.setFillHeight(false);
        secondRow.setManaged(false);

        overflowCounter = new Label();
        overflowCounter.getStyleClass().add("feature-label");
        overflowCounter.setMinWidth(Region.USE_PREF_SIZE);

        getChildren().addAll(firstRow, secondRow);
    }

    public void clear() {
        allFeatures.clear();
        requestLayout();
    }

    public void addNode(Label node) {
        allFeatures.add(node);
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        double availableWidth = getWidth() - getInsets().getLeft() - getInsets().getRight();
        if (availableWidth <= 0) {
            super.layoutChildren();
            return;
        }

        firstRow.getChildren().clear();
        secondRow.getChildren().clear();
        secondRow.setManaged(false);

        double spacing = firstRow.getSpacing(); // = 2
        double currentWidth = 0;
        int index = 0;

        // Заполняем первую строку
        for (; index < allFeatures.size(); index++) {
            Node node = allFeatures.get(index);
            double nodeWidth = node.prefWidth(-1);
            if (currentWidth > 0) {
                nodeWidth += spacing;
            }
            if (currentWidth + nodeWidth <= availableWidth) {
                firstRow.getChildren().add(node);
                currentWidth += nodeWidth;
            } else {
                break;
            }
        }

        // Заполняем вторую строку
        currentWidth = 0;
        int overflowCount = 0;
        for (; index < allFeatures.size(); index++) {
            Node node = allFeatures.get(index);
            double nodeWidth = node.prefWidth(-1);
            if (currentWidth > 0) {
                nodeWidth += spacing;
            }
            if (currentWidth + nodeWidth <= availableWidth) {
                secondRow.getChildren().add(node);
                currentWidth += nodeWidth;
            } else {
                overflowCount = allFeatures.size() - index;
                break;
            }
        }

        // Добавляем счётчик, если есть скрытые элементы
        if (overflowCount > 0) {
            overflowCounter.setText("+" + overflowCount);
            secondRow.getChildren().add(overflowCounter);
            secondRow.setManaged(true);
        } else {
            // Если вторая строка не пуста, показываем её
            secondRow.setManaged(!secondRow.getChildren().isEmpty());
        }

        super.layoutChildren();
    }
}