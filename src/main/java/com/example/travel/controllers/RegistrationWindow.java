package com.example.travel.controllers;

import com.example.travel.TravelApplication;
import com.example.travel.models.User;
import com.example.travel.services.UserService;
import com.example.travel.util.ConfigManager;
import com.example.travel.util.HelpFullClass;
import com.example.travel.util.SendingClass;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static com.example.travel.util.HelpFullClass.getNumberMonthWithRussianName;
import static com.example.travel.util.HelpFullClass.getRussianMonthName;
import static com.example.travel.util.SendingClass.getVerificationCode;

public class RegistrationWindow extends AnchorPane {
    private final StackPane overlaySP;
    private Pane shadowPane;

    private static final String EMAIL_NAME = "[A-Za-z0-9]+(?:[\\.-_]?[A-Za-z0-9]+)*";
    private static final String DOMAIN_NAME = "yandex|mail|gmail";
    private static final String END_EMAIL = "\\.[a-z]{2,3}";

    private static final Pattern EMAIL_NAME_PATTERN = Pattern.compile("^" + EMAIL_NAME + "$");
    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^(" + DOMAIN_NAME + ")$");
    private static final Pattern END_EMAIL_PATTERN = Pattern.compile("^" + END_EMAIL + "$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^" + EMAIL_NAME + "@(" + DOMAIN_NAME + ")" + END_EMAIL + "$");

    private Timer countdownTimer;

    private Button getCodeBtn, getCodeBtnAgain;

    private VBox emailVB, confirmEmailVB, addPersonalInformation, patronymicTF;

    private List<TextField> enterNumbers;

    private TextField emailTF;

    private boolean updating = false;
    private int secondsRemaining;

    private Text boldEmail;

    private Label errorVerification;

    private AnchorPane welcomeAP;

    private ConfigManager configManager = new ConfigManager();

    private ObjectProperty<Boolean> isEmpty = new SimpleObjectProperty<>();

    private HBox firstAndSecondNameHB, birthdayHB;

    public RegistrationWindow() {
        this.overlaySP = PopularDestinationsController.getOverlaySP();

        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        overlaySP.getChildren().add(shadowPane);

        setMaxHeight(350);
        setMaxWidth(500);
        setOpacity(0.0);

        getStyleClass().add("popup");

        createEnteredEmail();

        createConfirmEmail();

        overlaySP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!isVisible()) return;

            Node target = event.getPickResult().getIntersectedNode();

            if (target != null && target.getScene() != null &&
                    target.getScene().getWindow() instanceof javafx.stage.PopupWindow) {
                return;
            }

            Point2D pointInWindow = screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && (pointInWindow.getX() > 0 && pointInWindow.getY() > 0)
                    && (pointInWindow.getX() < 500 && pointInWindow.getY() < 350))
                return;
            else
                hide();
        });

        overlaySP.getChildren().add(this);

        FadeTransition fadeId = new FadeTransition(Duration.millis(400), this);
        fadeId.setToValue(1.0);
        fadeId.play();

        emailTF.requestFocus();
    }

    public void show() {
        shadowPane.setVisible(true);
        setVisible(true);
        FadeTransition fadeId = new FadeTransition(Duration.millis(400), this);
        fadeId.setToValue(1.0);
        fadeId.play();
    }

    public void hide() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            shadowPane.setVisible(false);
            setVisible(false);
        });
        fadeOut.play();
    }

    public void createEnteredEmail() {
        emailVB = new VBox(2);
        AnchorPane.setTopAnchor(emailVB, 25.0);
        AnchorPane.setLeftAnchor(emailVB, 50.0);
        AnchorPane.setRightAnchor(emailVB, 50.0);
        emailVB.setAlignment(Pos.TOP_CENTER);
        emailVB.setOpacity(1.0);

        Label headerLB = new Label("Войти или создать профиль");
        headerLB.getStyleClass().add("filters-label");

        Label hintEmail = new Label("Адрес электронной почты");
        hintEmail.getStyleClass().add("hint-label-registration");
        VBox.setMargin(hintEmail, new Insets(40, 0, 0, 0));
        hintEmail.setAlignment(Pos.BOTTOM_LEFT);
        hintEmail.prefWidthProperty().bind(emailVB.widthProperty().subtract(10));
        hintEmail.textProperty().addListener((ob, oldV, newV) -> {
            if(newV.equals("Адрес электронной почты"))
                hintEmail.setStyle("");
            else
                hintEmail.setStyle("-fx-text-fill: rgba(130,0,0);");
        });

        emailTF = new TextField();
        emailTF.setPromptText("Введите email");
        emailTF.getStyleClass().add("text-field-email");
        emailTF.textProperty().addListener((ob, oldV, newV) -> {
            if(validateEmail(newV) != null) {
                hintEmail.setText(validateEmail(newV));
                getCodeBtn.setDisable(true);
            } else {
                hintEmail.setText("Адрес электронной почты");
                getCodeBtn.setDisable(false);
            }
        });

        getCodeBtn = new Button("Получить код");
        getCodeBtn.getStyleClass().add("show-result-button");
        VBox.setMargin(getCodeBtn, new Insets(18, 0, 0, 0));
        getCodeBtn.prefWidthProperty().bind(emailTF.widthProperty());
        getCodeBtn.prefHeightProperty().bind(emailTF.heightProperty());
        getCodeBtn.setOnAction(e -> {
            if (emailTF.getText().isEmpty()) {
                hintEmail.setText("Укажите адрес электронной почты");
                return;
            }

            if (getCodeBtn.getText().equals("Получить код") && hintEmail.getText().equals("Адрес электронной почты")) {
                String mail = emailTF.getText();
                boldEmail.setText(mail);

                if (!SendingClass.canSendEmail(mail)) {
                    int remaining = SendingClass.getRemainingTime(mail);
                    startCountdownIfNeeded(remaining);
                    return;
                }

                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), emailVB);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> {
                    emailVB.setManaged(false);
                    emailVB.toBack();
                    confirmEmailVB.setManaged(true);
                    enterNumbers.getFirst().requestFocus();
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(600), confirmEmailVB);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();

                startCountdownIfNeeded(60);

                //Поменять на реальную отправку
                new Thread(() -> SendingClass.testSendPostalDelivery(mail)).start();
            }
        });

        emailVB.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER)
                getCodeBtn.fire();
        });

        Label userAgreementLB = new Label("Нажимая на кнопку, я соглашаюсь");
        userAgreementLB.getStyleClass().add("hint-label-registration");
        userAgreementLB.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setMargin(userAgreementLB, new Insets(18, 0, 0, 0));

        Button withTheRulesForUsingTheTradingPlatform = new Button("с правилами пользования торговой площадкой");
        withTheRulesForUsingTheTradingPlatform.getStyleClass().add("documents-button");
        withTheRulesForUsingTheTradingPlatform.setOnAction(e ->
                HelpFullClass.openWebPage("https://zelmex.ru/"));

        Button privacyPolicy = new Button("Политика конфиденциальности");
        privacyPolicy.getStyleClass().add("documents-button");
        privacyPolicy.setOnAction(e ->
                HelpFullClass.openWebPage("https://zelmex.ru/"));


        emailVB.getChildren().addAll(headerLB, hintEmail, emailTF, getCodeBtn, userAgreementLB, withTheRulesForUsingTheTradingPlatform, privacyPolicy);

        getChildren().add(emailVB);
    }

    private void createConfirmEmail() {
        enterNumbers = new ArrayList<>();

        confirmEmailVB = new VBox(20);
        AnchorPane.setTopAnchor(confirmEmailVB, 25.0);
        AnchorPane.setLeftAnchor(confirmEmailVB, 50.0);
        AnchorPane.setRightAnchor(confirmEmailVB, 50.0);
        confirmEmailVB.setOpacity(0.0);
        confirmEmailVB.setAlignment(Pos.TOP_CENTER);
        confirmEmailVB.setManaged(false);

        StackPane headerStackPane = new StackPane();
        headerStackPane.prefHeight(30);

        Label headerLB = new Label("Подтверждение email");
        headerLB.getStyleClass().add("filters-label");

        Button backBtn = new Button();
        backBtn.getStyleClass().add("back-button-double");
        backBtn.setPrefWidth(30);
        backBtn.setPrefHeight(30);
        backBtn.setOnAction(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), confirmEmailVB);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                confirmEmailVB.setManaged(false);
                emailVB.setManaged(true);
                emailVB.toFront();
                emailTF.requestFocus();
                FadeTransition fadeIn = new FadeTransition(Duration.millis(600), emailVB);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();

            errorVerification.setVisible(false);
        });

        ImageView imageView = new ImageView(
                new Image(Objects.requireNonNull(TravelApplication.class.getResourceAsStream("/images/left-arrow.png"))));
        imageView.setFitWidth(25 / 1.5);
        imageView.setFitHeight(25);
        imageView.setPreserveRatio(true);

        backBtn.setGraphic(imageView);
        StackPane.setAlignment(backBtn, Pos.CENTER_LEFT);

        headerStackPane.getChildren().addAll(headerLB, backBtn);

        TextFlow hintTextFlow = new TextFlow();

        Text before = new Text("Мы отправили код на адрес: ");
        before.setStyle("-fx-font-size: 18px;");
        boldEmail = new Text();
        boldEmail.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        Text after = new Text(", пожалуйста, проверьте папки «Входящие» и «Спам»");
        after.setStyle("-fx-font-size: 18px;");

        hintTextFlow.getChildren().addAll(before, boldEmail, after);

        HBox enteredHB = new HBox(10);
        enteredHB.setAlignment(Pos.CENTER);

        for(int i = 0; i < 6; i = i + 1) {
            enteredHB.getChildren().add(createNumberCodeTF());
        }

        getCodeBtnAgain = new Button("Получить код");
        getCodeBtnAgain.getStyleClass().add("show-result-button");
        VBox.setMargin(getCodeBtnAgain, new Insets(10, 0, 0, 0));
        getCodeBtnAgain.prefWidthProperty().bind(emailTF.widthProperty());
        getCodeBtnAgain.prefHeightProperty().bind(emailTF.heightProperty());
        getCodeBtnAgain.setOnAction(e -> {
            if (getCodeBtnAgain.getText().equals("Получить код")) {
                if (!SendingClass.canSendEmail(boldEmail.getText())) {
                    int remaining = SendingClass.getRemainingTime(boldEmail.getText());
                    startCountdownIfNeeded(remaining);
                    return;
                }
                startCountdownIfNeeded(60);

                //Поменять на реальную отправку
                new Thread(() -> SendingClass.testSendPostalDelivery(boldEmail.getText())).start();
            }
        });

        confirmEmailVB.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER)
                getCodeBtnAgain.fire();
        });

        errorVerification = new Label();
        errorVerification.getStyleClass().add("error-verification-label");

        confirmEmailVB.getChildren().addAll(headerStackPane, hintTextFlow, enteredHB, getCodeBtnAgain, errorVerification);

        getChildren().add(confirmEmailVB);
    }

    private static String validateEmail(String email) {
        //Проверяем что значение не пустое
        if (email.isEmpty())
            return "Email не может быть пустым";

        //Проверка на символ @
        if (!email.contains("@"))
            return "Адрес электронной почты должен содержать символ @";

        //Проверка длинны электронного адреса
        if (email.length() > 50)
            return "Адрес электронной почты не может быть длиннее 50-ти символов.";

        //Проверяем что мы имеем две части, как слева от @, так и справа
        String[] parts = email.split("@", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty())
            return "Некорректный формат email";

        //Создадим две переменные, для каждой из частей
        String localPart = parts[0];
        String domainPart = parts[1];

        //Проверка локальной части
        //Проверяем первый и последний символ в части имени email
        if (!EMAIL_NAME_PATTERN.matcher(localPart).matches()) {
            if (!Character.isLetterOrDigit(localPart.charAt(0)))
                return "Email не должен начинаться со специального символа";

            if (!Character.isLetterOrDigit(localPart.charAt(localPart.length() - 1)))
                return "Email не должен заканчиваться специальным символом";

            return "Email может содержать только англ. буквы, цифры и 1 спец. символ";
        }

        //Проверка доменной части
        //Доменная часть обязательно должна содержать символ .
        if (!domainPart.contains("."))
            return "Домен должен содержать точку";

        //Делим доменную часть левую(до символа .) и правую после неё, где указывается доменная зона(ru, com)
        String domain = domainPart.substring(0, domainPart.indexOf('.'));
        String zone = domainPart.substring(domainPart.indexOf('.') + 1);

        //Проверяем что указан допустимый домен
        if (!DOMAIN_NAME_PATTERN.matcher(domain).matches())
            return "Допустимые домены: yandex, mail, gmail";

        //Проверяем что доменная зона корректно указана
        if (!END_EMAIL_PATTERN.matcher("." + zone).matches())
            return "Доменная зона должна состоять из 2-3 маленьких латинских букв";

        return null;
    }

    private void updateButtonsText(int seconds) {
        if (seconds > 0) {
            String text = "Повторно через " + seconds;
            getCodeBtn.setText(text);
            getCodeBtnAgain.setText(text);
            getCodeBtn.setDisable(true);
            getCodeBtnAgain.setDisable(true);
        } else {
            getCodeBtn.setText("Получить код");
            getCodeBtnAgain.setText("Получить код");
            getCodeBtn.setDisable(false);
            getCodeBtnAgain.setDisable(false);
        }
    }

    private void startCountdownIfNeeded(int startSeconds) {
        if (countdownTimer != null) {
            updateButtonsText(secondsRemaining);
            return;
        }

        secondsRemaining = startSeconds;
        updateButtonsText(secondsRemaining);

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    secondsRemaining--;
                    if (secondsRemaining > 0) {
                        updateButtonsText(secondsRemaining);
                    } else {
                        updateButtonsText(0);
                        countdownTimer.cancel();
                        countdownTimer = null;
                    }
                });
            }
        }, 1000, 1000);
    }

    private TextField createNumberCodeTF() {
        TextField number = new TextField();
        number.setPrefWidth(40);
        number.setPrefHeight(40);
        number.setAlignment(Pos.CENTER);
        number.getStyleClass().add("text-field-email");
        number.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d")) {
                return change;
            } else {
                String lastChar = newText.substring(newText.length() - 1);
                if (lastChar.matches("\\d")) {
                    change.setText(lastChar);
                    change.setRange(0, change.getControlText().length());
                    change.setCaretPosition(lastChar.length());
                    change.setAnchor(lastChar.length());
                    return change;
                }
            }
            return null;
        }));

        enterNumbers.add(number);

        number.textProperty().addListener((ob, oldV, newV) -> {
            if (updating) return;
            updating = true;
            try {
                if(getEnteredCode(enterNumbers).length() == 6) {

                    if(getVerificationCode(boldEmail.getText()) != null
                            && getVerificationCode(boldEmail.getText()).trim().equals(getEnteredCode(enterNumbers).trim())) {
                        UserService userService = new UserService();
                        User existingUser = userService.findByEmail(boldEmail.getText());

                        if (existingUser == null) {
                            User newUser = new User();
                            newUser.setUserEmail(boldEmail.getText());
                            userService.saveRow(newUser);
                            configManager.setUserId(newUser.getUserID());
                            configManager.save();

                            if(addPersonalInformation == null)
                                fillInPersonalInformation();

                            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), confirmEmailVB);
                            fadeOut.setToValue(0.0);
                            fadeOut.setOnFinished(event -> {
                                confirmEmailVB.setManaged(false);
                                confirmEmailVB.toBack();
                                addPersonalInformation.setManaged(true);
                                addPersonalInformation.toFront();
                                FadeTransition fadeIn = new FadeTransition(Duration.millis(600), addPersonalInformation);
                                fadeIn.setToValue(1.0);
                                fadeIn.play();
                            });
                            fadeOut.play();

                            System.out.println("Новый пользователь создан с email: " + boldEmail.getText());
                        } else {
                            configManager.setUserId(existingUser.getUserID());
                            configManager.save();

                            creatingAWelcomeWindow();

                            PopularDestinationsController.profileBtn.setTextBtn("Профиль");

                            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), confirmEmailVB);
                            fadeOut.setToValue(0.0);
                            fadeOut.setOnFinished(event -> {
                                confirmEmailVB.setManaged(false);
                                confirmEmailVB.toBack();
                                welcomeAP.setManaged(true);
                                welcomeAP.toFront();
                                FadeTransition fadeIn = new FadeTransition(Duration.millis(600), welcomeAP);
                                fadeIn.setToValue(1.0);
                                fadeIn.play();
                                fadeIn.setOnFinished(eventIn -> {
                                    PauseTransition hideTimer = new PauseTransition(Duration.seconds(3));
                                    hideTimer.playFromStart();
                                    hideTimer.setOnFinished(eventTimer -> {
                                        hide();
                                    });
                                });
                            });
                            fadeOut.play();
                        }
                    } else {
                        errorVerification.setText("Верификация не пройдена: неверный код");
                        errorVerification.setVisible(true);
                    }

                    for(TextField tf : enterNumbers)
                        tf.setText("");
                    enterNumbers.getFirst().requestFocus();
                    return;
                }

                if (!enterNumbers.getLast().equals(number) && !enterNumbers.getFirst().equals(number)) {
                    TextField nextTF = null, prevTF = null;
                    for (int i = 0; i < enterNumbers.size(); i++) {
                        TextField currentTF = enterNumbers.get(i);
                        if (currentTF.equals(number)) {
                            nextTF = enterNumbers.get(i + 1);
                            prevTF = enterNumbers.get(i - 1);
                            break;
                        }
                    }

                    if (newV.isEmpty()) {
                        prevTF.requestFocus();
                        prevTF.positionCaret(1);
                        prevTF.cancelEdit();
                    } else {
                        nextTF.requestFocus();
                        nextTF.positionCaret(1);
                        nextTF.cancelEdit();
                    }
                } else if (enterNumbers.getLast().equals(number)) {
                    TextField prevTF = enterNumbers.get(enterNumbers.size() - 2);

                    if (newV.isEmpty()) {
                        prevTF.requestFocus();
                        prevTF.positionCaret(1);
                        prevTF.cancelEdit();
                    }


                } else if (enterNumbers.getFirst().equals(number)) {
                    TextField nextTF = enterNumbers.get(1);

                    if (!newV.isEmpty()) {
                        nextTF.requestFocus();
                        nextTF.positionCaret(1);
                        nextTF.cancelEdit();
                    }
                }
            } finally {
                updating = false;
            }
        });

        number.setOnKeyPressed(e -> {
            TextField nextTF = null, prevTF = null;
            if (enterNumbers.getFirst().equals(number))
                nextTF = enterNumbers.get(1);
            else if (enterNumbers.getLast().equals(number))
                prevTF = enterNumbers.get(enterNumbers.size() - 2);
            else {
                for (int i = 0; i < enterNumbers.size(); i++) {
                    TextField currentTF = enterNumbers.get(i);
                    if (currentTF.equals(number)) {
                        nextTF = enterNumbers.get(i + 1);
                        prevTF = enterNumbers.get(i - 1);
                        break;
                    }
                }
            }

            if(e.getCode() == KeyCode.UP || e.getCode() == KeyCode.RIGHT) {
                if(nextTF != null) {
                    nextTF.requestFocus();
                    nextTF.positionCaret(1);
                    nextTF.cancelEdit();
                }
            } else if(e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.LEFT) {
                if(prevTF != null) {
                    prevTF.requestFocus();
                    prevTF.positionCaret(1);
                    prevTF.cancelEdit();
                }
            } else if(e.getCode() == KeyCode.BACK_SPACE && number.getText().isEmpty()) {
                if(prevTF != null) {
                    prevTF.requestFocus();
                    prevTF.positionCaret(1);
                    prevTF.cancelEdit();
                }
            } else if(e.getCode() == KeyCode.SPACE) {
                if(nextTF != null) {
                    nextTF.requestFocus();
                    nextTF.positionCaret(1);
                    nextTF.cancelEdit();
                }
            }
        });

        return number;
    }

    private static String getEnteredCode(List<TextField> textFields) {
        StringBuilder sB = new StringBuilder();
        for (TextField tF : textFields) {
            sB.append(tF.getText());
        }
        return sB.toString();
    }

    private void creatingAWelcomeWindow() {
        welcomeAP = new AnchorPane();
        AnchorPane.setTopAnchor(welcomeAP, 25.0);
        AnchorPane.setBottomAnchor(welcomeAP, 25.0);
        AnchorPane.setLeftAnchor(welcomeAP, 50.0);
        AnchorPane.setRightAnchor(welcomeAP, 50.0);
        welcomeAP.setOpacity(0.0);
        welcomeAP.setManaged(false);

        VBox headerVB = new VBox(5);
        AnchorPane.setTopAnchor(headerVB, 20.0);
        AnchorPane.setLeftAnchor(headerVB, 0.0);
        AnchorPane.setRightAnchor(headerVB, 0.0);
        AnchorPane.setBottomAnchor(headerVB, 100.0);
        headerVB.setAlignment(Pos.CENTER);
        headerVB.getStyleClass().add("welcome-header-text-flow");

        Label firstText = new Label("Вы успешно");
        firstText.getStyleClass().add("welcome-header");

        Label secondText = new Label("вошли!");
        secondText.getStyleClass().add("welcome-header");

        headerVB.getChildren().addAll(firstText, secondText);

        TextFlow footTextFlow = new TextFlow();
        AnchorPane.setLeftAnchor(footTextFlow, 0.0);
        AnchorPane.setRightAnchor(footTextFlow, 0.0);
        AnchorPane.setBottomAnchor(footTextFlow, 0.0);
        footTextFlow.setTextAlignment(TextAlignment.CENTER);

        Text firstTextFoot = new Text("Поздравляем с успешной регистрацией!\n");
        firstTextFoot.getStyleClass().add("welcome-foot");
        firstTextFoot.setFill(Color.rgb(130,130,130));

        Text secondTextFoot = new Text("Пора искать идеальный отель для вашего следующего приключения.");
        secondTextFoot.getStyleClass().add("welcome-foot");
        secondTextFoot.setFill(Color.rgb(130,130,130));

        footTextFlow.getChildren().addAll(firstTextFoot, secondTextFoot);

        welcomeAP.getChildren().addAll(headerVB, footTextFlow);

        getChildren().add(welcomeAP);
    }

    private void fillInPersonalInformation() {
        addPersonalInformation = new VBox(15);
        AnchorPane.setTopAnchor(addPersonalInformation, 25.0);
        AnchorPane.setBottomAnchor(addPersonalInformation, 25.0);
        AnchorPane.setLeftAnchor(addPersonalInformation, 50.0);
        AnchorPane.setRightAnchor(addPersonalInformation, 50.0);
        addPersonalInformation.setOpacity(0.0);
        addPersonalInformation.setManaged(false);
        addPersonalInformation.setAlignment(Pos.TOP_CENTER);

        Label headerLB = new Label("Добавить личные данные");
        headerLB.getStyleClass().add("filters-label");

        firstAndSecondNameHB = new HBox(10);

        VBox nameTF = createHintTextField("Имя", "Укажите имя");

        VBox firstNameTF = createHintTextField("Фамилия", "Укажите фамилию");

        firstAndSecondNameHB.getChildren().addAll(nameTF, firstNameTF);

        patronymicTF = createHintTextField("Отчество", "Укажите отчество");

        birthdayHB = new HBox(10);
        birthdayHB.setAlignment(Pos.CENTER);

        ComboBox<Integer> dayBirthdayCB = new ComboBox<>();
        dayBirthdayCB.setPromptText("День");
        dayBirthdayCB.getStyleClass().add("combo-box");
        for(int i = 1; i <= 31; i = i + 1)
            dayBirthdayCB.getItems().add(i);

        ComboBox<String> monthBirthdayCB = new ComboBox<>();
        monthBirthdayCB.setPromptText("Месяц");
        monthBirthdayCB.getStyleClass().add("combo-box");
        for(int i = 1; i <= 12; i = i + 1)
            monthBirthdayCB.getItems().add(getRussianMonthName(i));

        ComboBox<Integer> yearBirthdayCB = new ComboBox<>();
        yearBirthdayCB.setPromptText("Год");
        yearBirthdayCB.getStyleClass().add("combo-box");
        for(int i = LocalDate.now().getYear() - 18; i >= LocalDate.now().getYear() - 118; i = i - 1)
            yearBirthdayCB.getItems().add(i);
        dayBirthdayCB.valueProperty().addListener((ob, oldV, newV) -> {
            if(newV == null)
                isEmpty.setValue(checkAllControl());
            else if(checkDate(dayBirthdayCB, monthBirthdayCB, yearBirthdayCB) != null)
                isEmpty.setValue(false);
        });
        monthBirthdayCB.valueProperty().addListener((ob, oldV, newV) -> {
            if(newV == null)
                isEmpty.setValue(checkAllControl());
            else if(checkDate(dayBirthdayCB, monthBirthdayCB, yearBirthdayCB) != null)
                isEmpty.setValue(false);
        });
        yearBirthdayCB.valueProperty().addListener((ob, oldV, newV) -> {
            if(newV == null)
                isEmpty.setValue(checkAllControl());
            else if(checkDate(dayBirthdayCB, monthBirthdayCB, yearBirthdayCB) != null)
                isEmpty.setValue(false);
        });

        Button fillItOutLater = new Button("Заполнить позже");
        fillItOutLater.prefWidthProperty().bind(addPersonalInformation.widthProperty());
        fillItOutLater.getStyleClass().add("fill-later");
        fillItOutLater.setOnAction(e -> {
            hide();
        });

        Button saveNewState = new Button("Сохранить данные");
        saveNewState.prefWidthProperty().bind(addPersonalInformation.widthProperty());
        saveNewState.getStyleClass().add("show-result-button");
        saveNewState.setOnAction(e -> {
            UserService userService = new UserService();
            User updatebleUser = userService.getRowById(configManager.getUserId());

            String newName = ((TextField) nameTF.getChildren().stream().filter(child -> child instanceof TextField)
                    .toList().getFirst()).getText();

            String newFirstName = ((TextField) firstNameTF.getChildren().stream().filter(child -> child instanceof TextField)
                    .toList().getFirst()).getText();

            String newPatronymicName = ((TextField) patronymicTF.getChildren().stream().filter(child -> child instanceof TextField)
                    .toList().getFirst()).getText();

            LocalDate newDate = checkDate(dayBirthdayCB, monthBirthdayCB, yearBirthdayCB);

            if(newName != null && !newName.isEmpty())
                updatebleUser.setUserSecondName(newName);

            if(newFirstName != null && !newFirstName.isEmpty())
                updatebleUser.setUserFirstName(newFirstName);

            if(newPatronymicName != null && !newPatronymicName.isEmpty())
                updatebleUser.setUserSurname(newPatronymicName);

            if(newDate != null)
                updatebleUser.setUserBirthday(newDate.toString());

            userService.updateRow(updatebleUser);

            hide();
        });

        isEmpty.addListener((ob, oldV, newV) -> {
            if(oldV != newV) {
                if (newV == true) {
                    addPersonalInformation.getChildren().add(fillItOutLater);
                    addPersonalInformation.getChildren().remove(saveNewState);
                } else {
                    addPersonalInformation.getChildren().add(saveNewState);
                    addPersonalInformation.getChildren().remove(fillItOutLater);
                }
            }
        });

        birthdayHB.getChildren().addAll(dayBirthdayCB, monthBirthdayCB, yearBirthdayCB);

        addPersonalInformation.getChildren().addAll(headerLB, firstAndSecondNameHB
                , patronymicTF, birthdayHB, fillItOutLater);

        getChildren().add(addPersonalInformation);
    }

    private VBox createHintTextField(String hintText, String promptText) {
        VBox rootVB = new VBox(2);
        rootVB.setAlignment(Pos.CENTER);

        Label hintEmail = new Label(hintText);
        hintEmail.getStyleClass().add("hint-label-registration");
        hintEmail.setAlignment(Pos.BOTTOM_LEFT);
        hintEmail.prefWidthProperty().bind(widthProperty().subtract(10));

        TextField emailTF = new TextField();
        emailTF.setPromptText(promptText);
        emailTF.getStyleClass().add("text-field-email");

        emailTF.textProperty().addListener((ob, oldV, newV) -> {
            if(newV.isEmpty())
                isEmpty.setValue(checkAllControl());
            else
                isEmpty.setValue(false);
        });

        rootVB.getChildren().addAll(hintEmail, emailTF);

        return rootVB;
    }

    private boolean checkAllControl() {
        for(Node node : firstAndSecondNameHB.getChildren()) {
            if(node instanceof VBox)
                for(Node node1 : ((VBox) node).getChildren())
                    if(node1 instanceof TextField && !((TextField) node1).getText().isEmpty())
                        return false;
        }

        for(Node node : patronymicTF.getChildren()) {
            if(node instanceof TextField && !((TextField) node).getText().isEmpty())
                return false;
        }

        for(Node node : birthdayHB.getChildren()) {
            if(node instanceof ComboBox<?>)
                if(((ComboBox<?>) node).getValue() != null)
                    return false;
        }

        return true;
    }

    private LocalDate checkDate(ComboBox<Integer> days, ComboBox<String> month, ComboBox<Integer> year) {
        try {
          return LocalDate.of(year.getValue(), getNumberMonthWithRussianName(month.getValue()), days.getValue());
        } catch (DateTimeException | NullPointerException e) {
            return null;
        }
    }
}
