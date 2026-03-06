package com.example.travel.controllers;

import com.example.travel.TravelApplication;
import com.example.travel.models.User;
import com.example.travel.services.UserService;
import com.example.travel.util.HelpFullClass;
import com.example.travel.util.SendingClass;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

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

    private VBox emailVB, confirmEmailVB;

    private List<TextField> enterNumbers;

    private TextField emailTF;

    private boolean updating = false;
    private int secondsRemaining;

    private Text boldEmail;

    public RegistrationWindow(StackPane overlaySP) {
        this.overlaySP = overlaySP;

        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        overlaySP.getChildren().add(shadowPane);

        setMaxHeight(350);
        setMaxWidth(500);

        setStyle("-fx-background-color: white;");
        getStyleClass().add("popup");

        createEnteredEmail();

        createConfirmEmail();

        overlaySP.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (!isVisible()) return;

            Point2D pointInWindow = screenToLocal(event.getScreenX(), event.getScreenY());
            if (pointInWindow != null && contains(pointInWindow))
                return;
            else
                hide();
        });

        overlaySP.getChildren().add(this);
    }

    public void show() {
        shadowPane.setVisible(true);
        setVisible(true);
    }

    public void hide() {
        shadowPane.setVisible(false);
        setVisible(false);
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
            hintEmail.setText(validateEmail(newV) != null ? validateEmail(newV) : "Адрес электронной почты");
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
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(600), confirmEmailVB);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();

                startCountdownIfNeeded(60);

                new Thread(() -> SendingClass.sendPostalDelivery(mail)).start();
            }
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
                FadeTransition fadeIn = new FadeTransition(Duration.millis(600), emailVB);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
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

                new Thread(() -> SendingClass.sendPostalDelivery(boldEmail.getText())).start();
            }
        });

        confirmEmailVB.getChildren().addAll(headerStackPane, hintTextFlow, enteredHB, getCodeBtnAgain);

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
                    for(TextField tf : enterNumbers)
                        tf.setText("");
                    enterNumbers.getFirst().requestFocus();

                    /*if(getVerificationCode(boldEmail.getText()) != null
                            && getVerificationCode(boldEmail.getText()).trim().equals(getEnteredCode(enterNumbers).trim())) {*/
                        UserService userService = new UserService();
                        User existingUser = userService.findByEmail(boldEmail.getText());

                        if (existingUser == null) {
                            User newUser = new User();
                            newUser.setUserEmail(boldEmail.getText());
                            userService.saveRow(newUser);
                            System.out.println("Новый пользователь создан с email: " + boldEmail.getText());
                        } else {
                            System.out.println("Пользователь уже существует: " + existingUser.getUserID());
                        }
                    /*} else {
                        System.out.println("Верификация не пройдена");
                    }*/
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

}
