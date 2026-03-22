package com.example.travel.controllers;

import com.example.travel.TravelApplication;
import com.example.travel.models.Direction;
import com.example.travel.models.Hotel;
import com.example.travel.services.DirectionService;
import com.example.travel.services.HotelService;
import com.example.travel.services.RoomService;
import com.example.travel.util.ConfigManager;
import com.example.travel.util.HelpFullClass;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static com.example.travel.util.HelpFullClass.createLoadHB;

public class PopularDestinationsController {

    // Сохраняем ссылку на StackPane, чтобы календарь был виден поверх всех элементов
    private static StackPane overlaySP;
    private static HotelService hotelService = new HotelService();
    private static ListView<Hotel> hotelsLV;
    private static AnchorPane rootAP;
    private static Label popularDestinationsLb;
    private static HBox buttonsBox;
    private static Button backBtn, stickyBackBtn;
    private static TilePane popularDestinationsTP;
    private static boolean isStickyMode;
    private static ScrollPane rootScrollPane;
    private static ObservableList<Hotel> observableHotels;
    private static FilteredList<Hotel> filteredHotels;
    private static SortedContext sortedContext = SortedContext.BY_DEFAULT;
    private static SorterWindow sorterWindow;
    private static FilterWindow filterWindow;
    protected static Direction oldPressedDirection;
    private static final RoomService roomService = new RoomService();
    protected static Map<String, Predicate<Hotel>> filteres = new HashMap<>();

    protected static ObjectProperty<Predicate<Hotel>> nameHotel = new SimpleObjectProperty<>();

    private static int countPopularDestinations = 0;
    private static int oldCountChildInRow = 0;

    private static RegistrationWindow registrationWindow;

    protected static CustomButton profileBtn;

    private static ConfigManager configManager = new ConfigManager();

    public enum SortedContext {
        BY_DEFAULT,
        MORE_EXPENSIVE,
        CHEAPER
    }

    public static StackPane createShapePopularDestinations() {
        overlaySP = new StackPane();

        rootAP = new AnchorPane();

        rootScrollPane = new ScrollPane();
        rootScrollPane.setContent(rootAP);
        rootScrollPane.setFitToWidth(true);
        rootScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rootScrollPane.getStyleClass().add("scroll-pane");
        new HelpFullClass().scrollPaneAnimation(rootScrollPane);

        overlaySP.getChildren().add(rootScrollPane);

        TextField searchTF = new TextField();
        searchTF.setPrefHeight(60.0);
        searchTF.setPromptText("Направления или отели");
        searchTF.getStyleClass().add("search-text-field");
        HBox.setMargin(searchTF, new Insets(0, 0, 0, 10));

        nameHotel.bind(Bindings.createObjectBinding(() -> hotel -> true));

        searchTF.textProperty().addListener((ob, oldV, newV) -> {
            String searchText = newV == null ? "" : newV.trim();
            nameHotel.bind(Bindings.createObjectBinding(() -> hotel -> {
                if(hotelsLV.isVisible()) {
                    if (newV == null || newV.isEmpty()) return true;
                    return hotel.getHotelName().toUpperCase().contains(newV.toUpperCase());
                }
                return false;
            }));

            filteres.put("searchTF", nameHotel.get());
            updatePredicateFilteredHotels();

            if (popularDestinationsTP.isVisible()) {
                // Фильтрация направлений (TilePane)
                popularDestinationsTP.getChildren().forEach(node -> {
                    if (node instanceof CustomDirection) {
                        CustomDirection cd = (CustomDirection) node;
                        boolean matches = searchText.isEmpty() ||
                                cd.getCityName().toUpperCase().contains(searchText.toUpperCase());
                        node.setVisible(matches);
                        node.setManaged(matches);
                    }
                });
            }
        });

        // Создаём календарь (он теперь будет создан один раз)
        CustomCalendar calendar = new CustomCalendar();
        calendar.setPrefWidth(150);
        calendar.setPrefHeight(60);

        NumberOfGuestsController number = new NumberOfGuestsController();
        number.setPrefWidth(110);
        number.setPrefHeight(60);

        HBox searchHB = new HBox();
        searchTF.setPrefHeight(60.0);
        searchHB.getStyleClass().add("search-hbox");
        searchHB.getChildren().addAll(searchTF, calendar, number);
        AnchorPane.setTopAnchor(searchHB, 15.0);
        AnchorPane.setLeftAnchor(searchHB, 10.0);
        AnchorPane.setRightAnchor(searchHB, 130.0);

        searchHB.widthProperty().addListener((ob, oldV, newV) -> {
            searchTF.setPrefWidth(newV.doubleValue() - 270);
        });

        CustomButton ordersBtn = new CustomButton(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/order.png")))
                , "Заказы");
        ordersBtn.setPrefWidth(50);
        ordersBtn.setMaxWidth(50);
        ordersBtn.setPrefHeight(35);
        AnchorPane.setTopAnchor(ordersBtn, 25.0);
        AnchorPane.setRightAnchor(ordersBtn, 70.0);

        profileBtn = new CustomButton(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/profile.png")))
                , configManager.getUserId() == 0 ? "Войти" : "Профиль");
        profileBtn.setPrefWidth(60);
        profileBtn.setMaxWidth(60);
        profileBtn.setPrefHeight(35);
        AnchorPane.setTopAnchor(profileBtn, 25.0);
        AnchorPane.setRightAnchor(profileBtn, 10.0);
        profileBtn.setOnAction(e -> {
            if(registrationWindow == null)
                registrationWindow = new RegistrationWindow(overlaySP);
            else
                registrationWindow.show();
        });

        AnchorPane headerAP = new AnchorPane();
        AnchorPane.setTopAnchor(headerAP, 0.0);
        AnchorPane.setRightAnchor(headerAP, 0.0);
        AnchorPane.setLeftAnchor(headerAP, 0.0);
        headerAP.getStyleClass().add("header-anchor-pane");
        headerAP.setPrefHeight(100);

        headerAP.getChildren().addAll(searchHB, ordersBtn, profileBtn);

        popularDestinationsLb = new Label("Популярные направления");
        popularDestinationsLb.getStyleClass().add("popular-destinations-label");
        AnchorPane.setTopAnchor(popularDestinationsLb, 100.0);
        AnchorPane.setLeftAnchor(popularDestinationsLb, 15.0);
        AnchorPane.setRightAnchor(popularDestinationsLb, 15.0);
        popularDestinationsLb.setPrefHeight(35);
        popularDestinationsLb.setAlignment(Pos.BOTTOM_LEFT);

        popularDestinationsTP = new TilePane();
        AnchorPane.setTopAnchor(popularDestinationsTP, 150.0);
        AnchorPane.setLeftAnchor(popularDestinationsTP, 15.0);
        AnchorPane.setRightAnchor(popularDestinationsTP, 15.0);
        popularDestinationsTP.setHgap(30.0);
        popularDestinationsTP.setVgap(30.0);
        popularDestinationsTP.setPrefColumns(5);
        popularDestinationsTP.setPrefTileHeight(290);
        popularDestinationsTP.setPrefTileWidth(270);
        popularDestinationsTP.widthProperty().addListener((ob, oldV, newV) -> {
            int countChildInRow = Math.max((newV.intValue() + 30) / 300, 1);
            if(countChildInRow != oldCountChildInRow) {
                oldCountChildInRow = countChildInRow;
                int countRow = (countPopularDestinations % countChildInRow) == 0 ? countPopularDestinations / countChildInRow
                        : (countPopularDestinations / countChildInRow) + 1;
                popularDestinationsTP.setPrefHeight(countRow * 320.0);
            }
        });

        List<Direction> directions = new DirectionService().getAllRow();
        for(Direction direction : directions) {
            CustomDirection direction1 = new CustomDirection(direction);
            direction1.setOnAction(e -> {
                showHotelsList(direction);
                oldPressedDirection = direction;
            });
            popularDestinationsTP.getChildren().add(direction1);
        }

        countPopularDestinations = popularDestinationsTP.getChildren().size();

        rootAP.getChildren().addAll(headerAP, popularDestinationsLb, popularDestinationsTP);

        rootScrollPane.vvalueProperty().addListener((obs, old, val) ->
                updateStickyButton(rootScrollPane));
        rootScrollPane.viewportBoundsProperty().addListener((obs, old, val) ->
                updateStickyButton(rootScrollPane));

        overlaySP.widthProperty().addListener((ob, oldV, newV) -> {
            if(filterWindow != null && filterWindow.isVisible()) {
                filterWindow.setSizeWindow();
            }
        });

        overlaySP.heightProperty().addListener((ob, oldV, newV) -> {
            if(filterWindow != null && filterWindow.isVisible()) {
                filterWindow.setSizeWindow();
            }
        });

        return overlaySP;
    }

    private static void showHotelsList(Direction direction) {
        popularDestinationsTP.setVisible(false);
        popularDestinationsTP.setManaged(false);
        popularDestinationsLb.setVisible(false);
        popularDestinationsLb.setManaged(false);

        HBox loadHB = createLoadHB("Ищем отели");
        AnchorPane.setTopAnchor(loadHB, 100.0);
        AnchorPane.setLeftAnchor(loadHB, 15.0);
        AnchorPane.setRightAnchor(loadHB, 15.0);
        rootAP.getChildren().add(loadHB);

        new Thread(() -> {
            List<Hotel> hotels = hotelService.getAllHotelsByDirectionID(direction.getIdDirection());

            Platform.runLater(() -> {
                if(hotelsLV == null)
                    createListView();
                else {
                    hotelsLV.setVisible(true);
                    hotelsLV.setManaged(true);
                    }

                observableHotels = FXCollections.observableArrayList(hotels);
                filteredHotels = new FilteredList<>(observableHotels, p -> true);

                popularDestinationsLb.setOpacity(0.0);

                updatePredicateFilteredHotels();

                hotelsLV.setItems(filteredHotels);

                if (sortedContext == SortedContext.BY_DEFAULT) {
                    PopularDestinationsController.sortHotels(Comparator.comparingDouble(Hotel::getHotelRating).reversed());
                }
                if (sortedContext == SortedContext.CHEAPER) {
                    PopularDestinationsController.sortHotels(Comparator.comparingDouble(
                            h -> roomService.getMinRoomPriceByHotelId(h.getIdHotel())));
                }
                if (sortedContext == SortedContext.MORE_EXPENSIVE) {
                    PopularDestinationsController.sortHotels(Comparator.comparingDouble(
                            (Hotel h) -> roomService.getMinRoomPriceByHotelId(h.getIdHotel())).reversed());
                }

                rootAP.getChildren().remove(loadHB);

                AnchorPane.setLeftAnchor(popularDestinationsLb, 80.0);
                popularDestinationsLb.setVisible(true);
                popularDestinationsLb.setManaged(true);
                popularDestinationsLb.setText(String.format("Найдено отелей: %d ", hotels.size()));

                if(backBtn == null)
                    createBackBtn();
                else {
                    backBtn.setVisible(true);
                    backBtn.setManaged(true);
                }

                if(buttonsBox == null)
                    createFilterAndSort();
                else {
                    buttonsBox.setVisible(true);
                    buttonsBox.setManaged(true);
                }

                if(stickyBackBtn == null)
                    createStickyBackBtn();
            });
        }).start();
    }

    private static void createListView() {
        hotelsLV = new ListView<>();
        hotelsLV.setSelectionModel(null);
        hotelsLV.setCellFactory(param -> new HotelCell());
        hotelsLV.getStyleClass().add("list-view");
        hotelsLV.addEventFilter(ScrollEvent.SCROLL, event -> {
            Event redirectedEvent = event.copyFor(hotelsLV, rootAP);
            rootAP.fireEvent(redirectedEvent);
            event.consume();
        });

        AnchorPane.setTopAnchor(hotelsLV, 200.0);
        AnchorPane.setLeftAnchor(hotelsLV, 0.0);
        AnchorPane.setRightAnchor(hotelsLV, 0.0);

        rootAP.getChildren().add(hotelsLV);
    }

    private static void createFilterAndSort() {
        // Контейнер для кнопок
        buttonsBox = new HBox(10); // отступ между кнопками 10px
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        // Кнопка сортировки (только иконка)
        Button sortBtn = new Button();
        sortBtn.setPrefHeight(35);
        sortBtn.getStyleClass().add("custom-button");
        sortBtn.setPadding(new Insets(0));
        sortBtn.setOnAction(e -> {
            if (sorterWindow == null) {
                sorterWindow = new SorterWindow();
                sorterWindow.show(sortBtn);
            } else {
                if (!sorterWindow.getPopup().isShowing())
                    sorterWindow.show(sortBtn);
                else
                    sorterWindow.hide();
            }
        });

        HBox backgroundHBSort = new HBox();
        backgroundHBSort.getStyleClass().add("sort-button-hbox");
        backgroundHBSort.setAlignment(Pos.CENTER);
        backgroundHBSort.setPadding(new Insets(10));

        ImageView imageSort = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/sort.png")))
        );
        imageSort.setFitHeight(20);
        imageSort.setFitWidth(20);
        imageSort.setPreserveRatio(true);

        backgroundHBSort.getChildren().add(imageSort);
        sortBtn.setGraphic(backgroundHBSort);

        // Кнопка фильтра (иконка + текст)
        Button filterBtn = new Button();
        filterBtn.setPrefHeight(40);
        filterBtn.setPrefWidth(160); // фиксированная ширина
        filterBtn.getStyleClass().add("custom-button");
        filterBtn.setPadding(new Insets(0));
        filterBtn.setOnAction(e -> {
            if (filterWindow == null) {
                filterWindow = new FilterWindow(overlaySP);
            } else {
                if (!filterWindow.isVisible())
                    filterWindow.show();
                else
                    filterWindow.hide();
            }
        });

        HBox backgroundHBFilter = new HBox(5);
        backgroundHBFilter.getStyleClass().add("sort-button-hbox");
        backgroundHBFilter.setAlignment(Pos.CENTER_LEFT);
        backgroundHBFilter.setPadding(new Insets(2, 10, 2, 10));

        ImageView imageFilter = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/filter.png")))
        );
        imageFilter.setFitHeight(25);
        imageFilter.setFitWidth(25);
        imageFilter.setPreserveRatio(true);

        Label filterLabel = new Label("Все фильтры");
        filterLabel.getStyleClass().add("filter-label");

        backgroundHBFilter.getChildren().addAll(imageFilter, filterLabel);
        filterBtn.setGraphic(backgroundHBFilter);

        // Добавляем кнопки в HBox
        buttonsBox.getChildren().addAll(sortBtn, filterBtn);

        // Позиционируем HBox в AnchorPane
        AnchorPane.setTopAnchor(buttonsBox, 140.0);
        AnchorPane.setLeftAnchor(buttonsBox, 80.0);

        rootAP.getChildren().add(buttonsBox);
    }

    private static void createBackBtn() {
        backBtn = new Button();
        backBtn.getStyleClass().add("back-button");
        AnchorPane.setLeftAnchor(backBtn, 15.0);
        AnchorPane.setTopAnchor(backBtn, 130.0);
        backBtn.setPrefWidth(40);
        backBtn.setPrefHeight(40);
        backBtn.setOnAction(e -> handleBackAction());

        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/back-button.png"))));
        imageView.setFitWidth(35 / 1.5);
        imageView.setFitHeight(35);
        imageView.setPreserveRatio(true);

        backBtn.setGraphic(imageView);

        rootAP.getChildren().add(backBtn);
    }

    private static void createStickyBackBtn() {
        stickyBackBtn = new Button();
        stickyBackBtn.getStyleClass().add("back-button");
        StackPane.setAlignment(stickyBackBtn, Pos.TOP_LEFT);
        StackPane.setMargin(stickyBackBtn, new Insets(15, 0, 0, 15));
        stickyBackBtn.setPrefWidth(40);
        stickyBackBtn.setPrefHeight(40);
        stickyBackBtn.setOnAction(e -> handleBackAction());
        stickyBackBtn.setVisible(false);
        stickyBackBtn.setManaged(false);

        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/back-button.png"))));
        imageView.setFitWidth(35 / 1.5);
        imageView.setFitHeight(35);
        imageView.setPreserveRatio(true);

        stickyBackBtn.setGraphic(imageView);

        overlaySP.getChildren().add(stickyBackBtn);
    }

    private static void handleBackAction() {
        hotelsLV.setVisible(false);
        hotelsLV.setManaged(false);

        AnchorPane.setLeftAnchor(popularDestinationsLb, 15.0);
        popularDestinationsLb.setText("Популярные направления");

        // Скрываем обе кнопки
        backBtn.setVisible(false);
        backBtn.setManaged(false);
        stickyBackBtn.setVisible(false);
        stickyBackBtn.setManaged(false);

        buttonsBox.setVisible(false);
        buttonsBox.setManaged(false);

        popularDestinationsTP.setVisible(true);
        popularDestinationsTP.setManaged(true);

        popularDestinationsTP.getChildren().forEach(node -> {
            if (node instanceof CustomDirection) {
                node.setVisible(true);
                node.setManaged(true);
            }
        });
    }

    private static void updateStickyButton(ScrollPane scrollPane) {
        if (backBtn == null || !hotelsLV.isVisible() || stickyBackBtn == null)
            return;
        if (backBtn.getWidth() <= 0 || backBtn.getHeight() <= 0)
            return;

        // Позиция верха кнопки в координатах globalStackPane (контент)
        Bounds btnBounds = backBtn.getBoundsInParent(); // относительно rootAP
        Point2D btnInContent = rootAP.localToParent(btnBounds.getMinX(), btnBounds.getMinY());
        double btnTop = btnInContent.getY();

        // Вычисляем верхнюю границу видимой области в координатах контента
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double visibleTop = 0;
        if (contentHeight > viewportHeight)
            visibleTop = scrollPane.getVvalue() * (contentHeight - viewportHeight);

        if (btnTop < visibleTop) {
            // Кнопка подходит к верху – включаем sticky
            if (!isStickyMode) {
                backBtn.setVisible(false);
                backBtn.setManaged(false);
                stickyBackBtn.setVisible(true);
                stickyBackBtn.setManaged(true);
                isStickyMode = true;
            }
        } else {
            // Кнопка далеко от верха – отключаем sticky
            if (isStickyMode) {
                backBtn.setVisible(true);
                backBtn.setManaged(true);
                stickyBackBtn.setVisible(false);
                stickyBackBtn.setManaged(false);
                isStickyMode = false;
            }
        }
    }

    public static SortedContext getSortedContext() {
        return sortedContext;
    }

    public static void setSortedContext(SortedContext sortedContext) {
        PopularDestinationsController.sortedContext = sortedContext;
    }

    public static void sortHotels(Comparator<Hotel> comparator) {
        if (observableHotels != null) {
            FXCollections.sort(observableHotels, comparator);
        }
    }

    public static FilteredList<Hotel> getFilteredHotels() {
        return filteredHotels;
    }

    public static ListView<Hotel> getHotelsLV() {
        return hotelsLV;
    }

    public static Label getPopularDestinationsLb() {
        return popularDestinationsLb;
    }

    public static void updatePredicateFilteredHotels() {
        if(filteredHotels == null)
            return;

        Predicate<Hotel> combined = filteres.values().stream().reduce(Predicate::and).orElse(h -> true);
        filteredHotels.setPredicate(combined);

        int cellHeight = 230;
        hotelsLV.setPrefHeight(filteredHotels.size() * cellHeight);
        Platform.runLater(() -> {
            popularDestinationsLb.setOpacity(1.0);
            popularDestinationsLb.setText("Найдено отелей: " + filteredHotels.size());
        });
    }

    public static StackPane getOverlaySP() {
        return overlaySP;
    }
}