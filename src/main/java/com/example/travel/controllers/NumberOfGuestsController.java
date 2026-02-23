    package com.example.travel.controllers;

    import javafx.event.EventHandler;
    import javafx.geometry.Bounds;
    import javafx.geometry.Insets;
    import javafx.geometry.Point2D;
    import javafx.geometry.Pos;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.layout.*;

    public class NumberOfGuestsController extends Button {
        protected static int adultsCount = 2;
        protected static int childrenCount = 0;

        private static final double GRID_PANE_WIDTH = 250;
        private static final double GRID_PANE_HEIGHT = 150; // чуть увеличил для удобства
        private static final double GRID_PANE_TOP_AND_BOTTOM_PADDING = 5;
        private static final double GRID_PANE_LEFT_AND_RIGHT_PADDING = 10;
        private static final double GRID_PANE_COUNT_COLUMNS_OR_ROW = 2;

        protected static GridPane rootGridPane;
        private static StackPane parentStackPane;
        protected static boolean isNumberOfGuestsVisible = false;

        // Ссылки на счётчики, чтобы обновлять их лейблы при показе
        private GuestCounter adultsCounter;
        private GuestCounter childrenCounter;

        private final EventHandler<MouseEvent> clickOutsideHandler;

        public NumberOfGuestsController(StackPane stackPane) {
            updateButtonText(); // переименовал для ясности
            getStyleClass().add("number-of-guests");
            parentStackPane = stackPane;

            clickOutsideHandler = event -> {
                if (rootGridPane != null && isNumberOfGuestsVisible) {
                    if (!rootGridPane.getBoundsInParent().contains(event.getX(), event.getY())
                            && !getBoundsInParent().contains(event.getX(), event.getY())) {
                        hideNumberOfGuests();
                    }
                }
            };

            setOnAction(e -> toggleNumberOfGuests());
        }

        // Обновляет текст на главной кнопке
        private void updateButtonText() {
            int total = adultsCount + childrenCount;
            if (total == 1)
                setText("1 гость");
            else if (total >= 2 && total <= 4)
                setText(total + " гостя");
            else
                setText(total + " гостей");
        }

        private void toggleNumberOfGuests() {
            if (!isNumberOfGuestsVisible)
                showNumberOfGuests();
            else
                hideNumberOfGuests();
        }

        private void createRootMarkup() {
            rootGridPane = new GridPane();
            rootGridPane.setManaged(false);
            rootGridPane.setMaxHeight(GRID_PANE_HEIGHT);
            rootGridPane.setMaxWidth(GRID_PANE_WIDTH);
            rootGridPane.setPadding(new Insets(GRID_PANE_TOP_AND_BOTTOM_PADDING, GRID_PANE_LEFT_AND_RIGHT_PADDING,
                    GRID_PANE_TOP_AND_BOTTOM_PADDING, GRID_PANE_LEFT_AND_RIGHT_PADDING));

            double columnWidth = (GRID_PANE_WIDTH - 2 * GRID_PANE_LEFT_AND_RIGHT_PADDING) / GRID_PANE_COUNT_COLUMNS_OR_ROW;
            double rowHeight = (GRID_PANE_HEIGHT - 2 * GRID_PANE_TOP_AND_BOTTOM_PADDING) / GRID_PANE_COUNT_COLUMNS_OR_ROW;

            rootGridPane.getColumnConstraints().addAll(
                    new ColumnConstraints(columnWidth),
                    new ColumnConstraints(columnWidth));
            rootGridPane.getRowConstraints().addAll(
                    new RowConstraints(rowHeight),
                    new RowConstraints(rowHeight));
            rootGridPane.getStyleClass().add("root-grid-pane");

            // Взрослые (левая колонка, строка 0)
            VBox adultsVB = new VBox(5);
            adultsVB.setAlignment(Pos.CENTER_LEFT);
            Label adultsLB = new Label("Взрослые");
            adultsLB.setStyle("-fx-font-weight: bold;");
            Label adultsInfoLB = new Label("Старше 18 лет");
            adultsInfoLB.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
            adultsVB.getChildren().addAll(adultsLB, adultsInfoLB);
            GridPane.setRowIndex(adultsVB, 0);
            GridPane.setColumnIndex(adultsVB, 0);

            // Дети (левая колонка, строка 1)
            VBox childrenVB = new VBox(5);
            childrenVB.setAlignment(Pos.CENTER_LEFT);
            Label childrenLB = new Label("Дети");
            childrenLB.setStyle("-fx-font-weight: bold;");
            Label childrenInfoLB = new Label("До 18 лет");
            childrenInfoLB.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
            childrenVB.getChildren().addAll(childrenLB, childrenInfoLB);
            GridPane.setRowIndex(childrenVB, 1);
            GridPane.setColumnIndex(childrenVB, 0);

            // Создаём счётчики и сохраняем ссылки
            adultsCounter = new GuestCounter(true, this);
            childrenCounter = new GuestCounter(false, this);

            GridPane.setColumnIndex(adultsCounter, 1);
            GridPane.setRowIndex(adultsCounter, 0);
            GridPane.setColumnIndex(childrenCounter, 1);
            GridPane.setRowIndex(childrenCounter, 1);

            rootGridPane.getChildren().addAll(adultsVB, childrenVB, adultsCounter, childrenCounter);
            parentStackPane.getChildren().add(rootGridPane);
        }

        private void showNumberOfGuests() {
            if (rootGridPane == null) {
                createRootMarkup();
            } else {
                // Если панель уже создана, обновляем лейблы счётчиков актуальными значениями
                adultsCounter.setCount(adultsCount);
                childrenCounter.setCount(childrenCount);
            }

            // Позиционируем под кнопкой
            setLayout();

            rootGridPane.setVisible(true);
            rootGridPane.toFront();

            parentStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler);
            isNumberOfGuestsVisible = true;
        }

        private void hideNumberOfGuests() {
            if (rootGridPane != null) {
                rootGridPane.setVisible(false);
                parentStackPane.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler);
                isNumberOfGuestsVisible = false;
            }
        }

        // Метод, который будут вызывать счётчики для обновления главной кнопки
        public void onCountChanged() {
            updateButtonText();
        }

        public void setLayout() {
            // Получаем координаты нижнего края кнопки в системе координат сцены
            Bounds boundsInScene = localToScene(getBoundsInLocal());
            double xInScene = boundsInScene.getMinX();
            double yInScene = boundsInScene.getMaxY() + 5; // небольшой отступ

            // Преобразуем координаты из сцены в координаты StackPane
            Point2D pointInStackPane = parentStackPane.sceneToLocal(xInScene, yInScene);

            // Устанавливаем позицию И размеры календаря
            rootGridPane.resizeRelocate(pointInStackPane.getX(), pointInStackPane.getY(), GRID_PANE_WIDTH, GRID_PANE_HEIGHT);

            // Принудительно применяем CSS и выполняем компоновку
            rootGridPane.applyCss();
            rootGridPane.layout();
        }
    }