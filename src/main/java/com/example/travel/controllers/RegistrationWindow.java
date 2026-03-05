package com.example.travel.controllers;

import com.example.travel.services.DirectionService;
import com.example.travel.util.HelpFullClass;
import com.example.travel.util.SendingClass;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

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

    private Button getCodeBtn;

    public RegistrationWindow(StackPane overlaySP) {
        this.overlaySP = overlaySP;

        shadowPane = new Pane();
        shadowPane.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        overlaySP.getChildren().add(shadowPane);

        setMaxHeight(350);
        setMaxWidth(500);

        setStyle("-fx-background-color: white;");
        getStyleClass().add("popup");

        createHeader();

        createBody();

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

    private void createHeader() {
        Label headerLB = new Label("Войти или создать профиль");
        headerLB.getStyleClass().add("filters-label");
        headerLB.setAlignment(Pos.BOTTOM_CENTER);
        AnchorPane.setTopAnchor(headerLB, 25.0);
        AnchorPane.setLeftAnchor(headerLB, 15.0);
        AnchorPane.setRightAnchor(headerLB, 15.0);

        getChildren().add(headerLB);
    }

    public void show() {
        shadowPane.setVisible(true);
        setVisible(true);
    }

    public void hide() {
        shadowPane.setVisible(false);
        setVisible(false);
    }

    public void createBody() {
        VBox emailVB = new VBox(2);
        AnchorPane.setTopAnchor(emailVB, 90.0);
        AnchorPane.setLeftAnchor(emailVB, 50.0);
        AnchorPane.setRightAnchor(emailVB, 50.0);
        emailVB.setAlignment(Pos.TOP_CENTER);

        Label hintEmail = new Label("Адрес электронной почты");
        hintEmail.getStyleClass().add("hint-label-registration");
        hintEmail.setAlignment(Pos.BOTTOM_LEFT);
        hintEmail.prefWidthProperty().bind(emailVB.widthProperty().subtract(10));
        hintEmail.textProperty().addListener((ob, oldV, newV) -> {
            if(newV.equals("Адрес электронной почты"))
                setStyle("");
            else
                setStyle("-fx-text-fill: rgba(130,0,0);");
        });

        TextField emailTF = new TextField();
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
            if (getCodeBtn.getText().equals("Получить код") && hintEmail.getText().equals("Адрес электронной почты")) {
                String mail = emailTF.getText();
                if (!SendingClass.canSendEmail(emailTF.getText())) {
                    int remaining = SendingClass.getRemainingTime(mail);
                    startCountdown(remaining);
                    return;
                }

                // ОТПРАВЛЯЕМ email
                new Thread(() -> {
                    boolean sent = SendingClass.sendPostalDelivery(mail);
                    if(sent)
                        System.out.println("Сообщение отправлено");
                    else
                        System.out.println("Сообщение не отправлено");
                }).start();

                startCountdown(60);
            }
        });

        Label userAgreementLB = new Label("Нажимая на кнопку, я соглашаюсь");
        userAgreementLB.getStyleClass().add("hint-label-registration");
        userAgreementLB.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setMargin(userAgreementLB, new Insets(18, 0, 0, 0));

        Button withTheRulesForUsingTheTradingPlatform = new Button("с правилами пользования торговой площадкой");
        withTheRulesForUsingTheTradingPlatform.getStyleClass().add("documents-button");
        withTheRulesForUsingTheTradingPlatform.setOnAction(e ->
                HelpFullClass.openWebPage("https://metanit.com/java/javafx/3.2.php"));

        Button privacyPolicy = new Button("Политика конфиденциальности");
        privacyPolicy.getStyleClass().add("documents-button");
        privacyPolicy.setOnAction(e ->
                HelpFullClass.openWebPage("https://metanit.com/java/javafx/3.2.php"));


        emailVB.getChildren().addAll(hintEmail, emailTF, getCodeBtn, userAgreementLB, withTheRulesForUsingTheTradingPlatform, privacyPolicy);

        getChildren().add(emailVB);
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

    private void startCountdown(int startSeconds) {
        // Отменяем предыдущий таймер
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        getCodeBtn.setDisable(true);
        final int[] secondsRemaining = {startSeconds};

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    secondsRemaining[0]--;
                    if (secondsRemaining[0] > 0) {
                        getCodeBtn.setText("Повторно через " + secondsRemaining[0]);
                    } else {
                        getCodeBtn.setText("Получить код");
                        getCodeBtn.setDisable(false);
                        countdownTimer.cancel();
                        countdownTimer = null;
                    }
                });
            }
        }, 1000, 1000);
    }
}
