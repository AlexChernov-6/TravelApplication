package com.example.travel.controllers;

import com.example.travel.TravelApplication;
import com.example.travel.models.Order;
import com.example.travel.models.User;
import com.example.travel.services.OrderService;
import com.example.travel.services.UserService;
import com.example.travel.util.HelpFullClass;
import com.example.travel.util.InputControlMaskFormatter;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

import static com.example.travel.controllers.CheckoutWindow.*;
import static com.example.travel.controllers.RegistrationWindow.CONFIG_MANAGER;

public class PersonalAccountWindow extends HBox {
    private AnchorPane centralAP;
    private GridPane gridPane;

    private User currUser;

    private String oldFirstName;
    private String oldName;
    private String oldMiddleName;
    private String oldBirthday;
    private String oldEmail;

    public enum ContextStartPersonalAccount {
        PROFILE,
        ORDERS
    }

    public PersonalAccountWindow(ContextStartPersonalAccount context) {
        setStyle("-fx-background-color: white");

        currUser = new UserService().getRowById(CONFIG_MANAGER.getUserId());

        oldFirstName = currUser.getUserFirstName();
        oldName = currUser.getUserSecondName();
        oldMiddleName = currUser.getUserSurname();
        oldBirthday = currUser.getUserBirthday();
        oldEmail = currUser.getUserEmail();

        VBox leftVB = new VBox(5);
        leftVB.setAlignment(Pos.TOP_CENTER);
        leftVB.setPrefWidth(80);
        leftVB.setPadding(new Insets(10, 0, 0, 0));
        leftVB.setStyle("-fx-background-color: #b40acf;");
        getChildren().add(leftVB);

        CustomButton profileBtn = new CustomButton(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/profile.png")))
                , "Профиль");
        profileBtn.setPrefWidth(60);
        profileBtn.setMaxWidth(60);
        profileBtn.setPrefHeight(35);
        leftVB.getChildren().add(profileBtn);
        profileBtn.setOnAction(e -> {
            createShapeProfile();
        });
        profileBtn.setOnMouseEntered(e -> {
            profileBtn.setStyle("-fx-background-color: #c334d9;");
        });
        profileBtn.setOnMouseExited(e -> {
            profileBtn.setStyle("");
        });

        leftVB.getChildren().add(createHorizontalLine());

        CustomButton ordersBtn = new CustomButton(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/order.png")))
                , "Заказы");
        ordersBtn.setPrefWidth(60);
        ordersBtn.setMaxWidth(60);
        ordersBtn.setPrefHeight(35);
        leftVB.getChildren().add(ordersBtn);
        ordersBtn.setOnAction(e -> {
            createShapeOrders();
        });
        ordersBtn.setOnMouseEntered(e -> {
            ordersBtn.setStyle("-fx-background-color: #c334d9;");
        });
        ordersBtn.setOnMouseExited(e -> {
            ordersBtn.setStyle("");
        });

        leftVB.getChildren().add(createHorizontalLine());

        centralAP = new AnchorPane();
        centralAP.setPadding(new Insets(20));
        widthProperty().addListener((ob, oldV, newV) -> {
            double centralStackPaneNewWidth = newV.doubleValue() - 80 - 45;
            centralAP.setMaxWidth(centralStackPaneNewWidth);
            centralAP.setPrefWidth(centralStackPaneNewWidth);
            centralAP.setMinWidth(centralStackPaneNewWidth);
        });
        getChildren().add(centralAP);

        VBox rightVB = new VBox();
        rightVB.setAlignment(Pos.TOP_LEFT);
        rightVB.setPrefWidth(45);
        rightVB.setPadding(new Insets(10, 0, 0, 0));
        getChildren().add(rightVB);

        Button closeBtn = new Button();
        closeBtn.setPrefHeight(30);
        closeBtn.setPrefWidth(30);
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(event -> {
            PopularDestinationsController.getOverlaySP().getChildren().remove(this);
        });

        ImageView closeImg = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/close.png"))));
        closeImg.setFitHeight(15);
        closeImg.setFitWidth(15);
        closeImg.setPreserveRatio(true);

        closeBtn.setGraphic(closeImg);

        rightVB.getChildren().add(closeBtn);

        PopularDestinationsController.getOverlaySP().getChildren().add(this);

        if(context == ContextStartPersonalAccount.PROFILE)
            profileBtn.fire();

        if(context == ContextStartPersonalAccount.ORDERS)
            ordersBtn.fire();
    }

    private Node createHorizontalLine() {
        Region line = new Region();
        line.setStyle("-fx-background-color: white; -fx-min-height: 0.5px; -fx-pref-height: 0.5px; -fx-max-height: 0.5px;");
        return line;
    }

    private void createShapeProfile() {
        centralAP.getChildren().clear();

        Label updateProfileLB = new Label("Редактировать профиль");
        updateProfileLB.getStyleClass().add("popular-destinations-label");
        AnchorPane.setTopAnchor(updateProfileLB, 0.0);
        AnchorPane.setLeftAnchor(updateProfileLB, 15.0);
        AnchorPane.setRightAnchor(updateProfileLB, 15.0);
        updateProfileLB.setPrefHeight(35);
        updateProfileLB.setAlignment(Pos.BOTTOM_LEFT);
        centralAP.getChildren().add(updateProfileLB);

        gridPane = new GridPane();
        AnchorPane.setTopAnchor(gridPane, 45.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 80.0);
        centralAP.getChildren().add(gridPane);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);

        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints());

        TextField firstNameTF = new TextField();
        setStateInputControl(0, 0, firstNameTF, "Фамилия", CheckoutWindow.InputControlContext.FIRST_NAME_OR_NAME);
        firstNameTF.setText(oldFirstName);

        TextField nameTF = new TextField();
        setStateInputControl(1, 0, nameTF, "Имя", CheckoutWindow.InputControlContext.FIRST_NAME_OR_NAME);
        nameTF.setText(oldName);

        TextField sureNameTF = new TextField();
        setStateInputControl(0, 1, sureNameTF, "Отчество", CheckoutWindow.InputControlContext.FIRST_NAME_OR_NAME);
        sureNameTF.setText(oldMiddleName);

        TextField birthdayTF = new TextField();
        birthdayTF.setText(oldBirthday);
        setStateInputControl(1, 1, birthdayTF, "Дата рождения", CheckoutWindow.InputControlContext.BIRTHDAY);
        birthdayTF.setOnMouseEntered(e -> {
            birthdayTF.setPromptText("__.__.____");
        });
        birthdayTF.setOnMouseExited(e -> {
            birthdayTF.setPromptText("Дата рождения");
        });

        VBox inputControlVB1 = new VBox();
        inputControlVB1.setAlignment(Pos.CENTER);
        GridPane.setRowIndex(inputControlVB1, 2);
        GridPane.setColumnSpan(inputControlVB1, 2);
        GridPane.setMargin(inputControlVB1, new Insets(10));
        gridPane.getChildren().add(inputControlVB1);

        VBox inputControlSecondVB1 = new VBox();
        inputControlSecondVB1.getStyleClass().add("input-guest-state-control");
        inputControlVB1.getChildren().add(inputControlSecondVB1);

        Label hintLB1 = new Label();
        hintLB1.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintLB1, new Insets(0, 0, 0, 5));
        hintLB1.prefWidthProperty().bind(inputControlVB1.widthProperty());
        inputControlVB1.getChildren().addFirst(hintLB1);
        hintLB1.setVisible(false);
        hintLB1.setManaged(false);
        hintLB1.setStyle("-fx-text-fill: rgba(130,0,0);");

        TextField emailTF = new TextField();
        emailTF.setPromptText("Email *");
        emailTF.getStyleClass().add("style-input-control");
        emailTF.setUserData(hintLB1);
        emailTF.prefWidthProperty().bind(inputControlVB1.widthProperty());

        Label lb1 = new Label(emailTF.getPromptText());
        lb1.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
        VBox.setMargin(lb1, new Insets(5, 0, 0, 10));
        lb1.setVisible(false);
        lb1.setManaged(false);
        inputControlSecondVB1.getChildren().add(lb1);

        emailTF.focusedProperty().addListener((obs, old, val) -> {
            if (emailTF.getText().isEmpty())
                hintLB1.setText("Поле обязательно для заполнения");

            if (val) {
                lb1.setVisible(true);
                lb1.setManaged(true);
                if (hintLB1.getText() != null && hintLB1.getText().isEmpty())
                    hintLB1.setText(validateEmail(emailTF.getText()));
            } else {
                if (emailTF.getText() == null || emailTF.getText().isEmpty()) {
                    lb1.setVisible(false);
                    lb1.setManaged(false);
                }
            }
        });

        emailTF.textProperty().addListener((ob, oldV, newV) -> {
            if (validateEmail(newV) != null) {
                hintLB1.setText(validateEmail(newV));
            } else {
                hintLB1.setText("");
            }

            if (newV.isEmpty() && !emailTF.isFocused()) {
                lb1.setVisible(false);
                lb1.setManaged(false);
            }
        });
        inputControlSecondVB1.getChildren().add(emailTF);

        hintLB1.textProperty().addListener((ob, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                hintLB1.setManaged(false);
                hintLB1.setVisible(false);
                inputControlSecondVB1.setStyle("");
            } else {
                hintLB1.setManaged(true);
                hintLB1.setVisible(true);
                inputControlSecondVB1.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });
        emailTF.setText(oldEmail);

        HBox buttonsHB = new HBox(20);
        buttonsHB.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(buttonsHB, 0.0);
        AnchorPane.setLeftAnchor(buttonsHB, 0.0);
        AnchorPane.setRightAnchor(buttonsHB, 0.0);
        buttonsHB.setPrefHeight(80);
        centralAP.getChildren().add(buttonsHB);

        Button cancelBtn = new Button("Отмена");
        cancelBtn.setPrefHeight(50);
        cancelBtn.setPrefWidth(150);
        cancelBtn.getStyleClass().add("reset-button");
        buttonsHB.getChildren().add(cancelBtn);
        cancelBtn.setOnAction(e -> {
            firstNameTF.setText(oldFirstName);
            nameTF.setText(oldName);
            sureNameTF.setText(oldMiddleName);
            birthdayTF.requestFocus();
            birthdayTF.setText(oldBirthday);
            updateProfileLB.requestFocus();
            emailTF.setText(oldEmail);
        });

        Button updateUserStateBtn = new Button("Применить");
        updateUserStateBtn.setPrefHeight(50);
        updateUserStateBtn.setPrefWidth(150);
        updateUserStateBtn.getStyleClass().add("show-result-button");
        buttonsHB.getChildren().add(updateUserStateBtn);
        updateUserStateBtn.setOnAction(e -> {
            //Сделать подтверждение нового Email
            oldFirstName = firstNameTF.getText();
            oldName = nameTF.getText();
            oldMiddleName = sureNameTF.getText();
            oldBirthday = birthdayTF.getText();
            oldEmail = emailTF.getText();

            currUser.setUserFirstName(oldFirstName);
            currUser.setUserSecondName(oldName);
            currUser.setUserSurname(oldMiddleName);
            currUser.setUserBirthday(oldBirthday);
            currUser.setUserEmail(oldEmail);

            new UserService().updateRow(currUser);
        });

        updateProfileLB.requestFocus();
    }

    private void setStateInputControl(int col, int row, TextInputControl node, String promptText, CheckoutWindow.InputControlContext controlContext) {
        VBox inputControlVB = new VBox();
        inputControlVB.setAlignment(Pos.BOTTOM_LEFT);
        GridPane.setRowIndex(inputControlVB, row);
        GridPane.setColumnIndex(inputControlVB, col);
        GridPane.setMargin(inputControlVB, new Insets(10));

        VBox inputControlSecondVB = new VBox();
        inputControlSecondVB.getStyleClass().add("input-guest-state-control");
        inputControlVB.getChildren().add(inputControlSecondVB);

        Label hintLB = new Label();
        hintLB.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintLB, new Insets(0, 0, 0, 5));
        hintLB.prefWidthProperty().bind(node.widthProperty());
        hintLB.setStyle("-fx-text-fill: rgba(130,0,0);");
        hintLB.setVisible(false);
        hintLB.setManaged(false);
        hintLB.textProperty().addListener((ob, oldV, newV) -> {
            if (newV.isEmpty()) {
                hintLB.setManaged(false);
                hintLB.setVisible(false);
                inputControlSecondVB.setStyle("");
            } else {
                hintLB.setManaged(true);
                hintLB.setVisible(true);
                inputControlSecondVB.setStyle("-fx-border-color: rgba(130,0,0);");
            }
        });
        inputControlVB.getChildren().addFirst(hintLB);

        node.setPromptText(promptText);
        node.getStyleClass().add("style-input-control");

        Label lb = new Label(node.getPromptText());
        lb.setStyle("-fx-text-fill: rgba(180, 180, 210); -fx-font-weight: bold; -fx-font-size: 12px;");
        VBox.setMargin(lb, new Insets(5, 0, 0, 10));
        if (node.getUserData() != null && node.getUserData().equals("note Editable")) {
            lb.setVisible(true);
            lb.setManaged(true);
        } else {
            lb.setVisible(false);
            lb.setManaged(false);
        }
        inputControlSecondVB.getChildren().add(lb);

        node.setUserData(hintLB);

        node.focusedProperty().addListener((obs, old, val) -> {
            if (val) {
                lb.setVisible(true);
                lb.setManaged(true);
            } else {
                if (node.getText() == null || node.getText().isEmpty()) {
                    lb.setVisible(false);
                    lb.setManaged(false);
                }
            }
        });

        node.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.isEmpty() && !node.isFocused()) {
                lb.setVisible(false);
                lb.setManaged(false);
            }
        });


        if (controlContext == CheckoutWindow.InputControlContext.FIRST_NAME_OR_NAME) {
            node.setTextFormatter(new TextFormatter<>(change -> {
                String oldText = change.getControlText();
                String inputText = change.getText();

                if (!inputText.isEmpty()) {
                    String filtered = inputText.chars()
                            .filter(c -> isAllowed((char) c))
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();

                    if (filtered.isEmpty()) {
                        if (inputText.length() == 1) {
                            hintLB.setText("Доступны только буквы русского алфавита");
                        }
                        return null;
                    } else hintLB.setText("");
                    change.setText(filtered);
                }

                if (change.isDeleted()) {
                    return validateAndCapitalize(change, oldText, change.getControlNewText(), node);
                }

                String newTextFull = change.getControlNewText();
                return validateAndCapitalize(change, oldText, newTextFull, node);
            }));
        } else if (controlContext == CheckoutWindow.InputControlContext.BIRTHDAY) {
            InputControlMaskFormatter maskBirthday = new InputControlMaskFormatter();
            maskBirthday.apply(node, InputControlMaskFormatter.MaskContext.DATE_MASK);
        }

        inputControlSecondVB.getChildren().add(node);

        gridPane.getChildren().add(inputControlVB);
    }

    private void createShapeOrders() {
        centralAP.getChildren().clear();

        ListView<Order> orderListView = new ListView<>();
        AnchorPane.setTopAnchor(orderListView, 0.0);
        AnchorPane.setLeftAnchor(orderListView, 20.0);
        AnchorPane.setRightAnchor(orderListView, 20.0);
        AnchorPane.setBottomAnchor(orderListView, 0.0);
        orderListView.setCellFactory(cell -> new OrderCell());
        orderListView.getStyleClass().addAll("list-view", "scroll-pane");
        orderListView.setSelectionModel(null);
        Platform.runLater(() -> {
            new HelpFullClass().scrollPaneAnimation(orderListView);
        });
        centralAP.getChildren().add(orderListView);

        orderListView.getItems().setAll(new OrderService().getAllOrderByUserId(CONFIG_MANAGER.getUserId()));
    }
}
