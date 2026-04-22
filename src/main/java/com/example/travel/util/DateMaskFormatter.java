package com.example.travel.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

public class DateMaskFormatter {

    private static final String MASK = "__.__.____";
    private static final char PLACEHOLDER = '_';

    /**
     * Применяет маску ввода даты к текстовому полю.
     * @param textField поле для форматирования
     */
    public static void apply(TextInputControl textField) {
        // Фильтр для обработки изменений текста
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getText().isEmpty() && !change.isDeleted())
                return change;

            String oldText = change.getControlText();
            System.out.println("Change text = " + change.getText() + "; Change oldText = " + change.getControlText());

            if (change.getText().equals(MASK)) {
                return change;
            }

            // Обработка удаления (Backspace / Delete)
            if (change.isDeleted()) {
                return handleDeletion(change, oldText);
            }

            // Обработка вставки одного символа (цифры)
            if (change.getText().length() == 1 && Character.isDigit(change.getText().charAt(0))) {
                return handleDigitInsert(change, oldText);
            }

            // Вставка нескольких символов или нецифровых символов запрещена
            return null;
        };

        TextFormatter<String> formatter = new TextFormatter<>(new StringConverter<String>() {
            @Override
            public String fromString(String string) {
                return string;
            }

            @Override
            public String toString(String object) {
                return object;
            }
        }, "", filter);

        textField.setTextFormatter(formatter);

        // Устанавливаем маску при получении фокуса, если поле пустое
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                String currentText = textField.getText();
                if (currentText == null || currentText.isEmpty() || currentText.equals(MASK)) {
                    textField.setText(MASK);
                    textField.positionCaret(0);
                }
            } else {
                if (MASK.equals(textField.getText())) {
                    textField.setText("");
                }
            }
        });
    }

    // Обработка ввода цифры
    private static TextFormatter.Change handleDigitInsert(TextFormatter.Change change, String oldText) {
        if (oldText.length() != MASK.length()) return null;

        int caretPos = change.getCaretPosition();
        char digit = change.getText().charAt(0);

        if(caretPos > 10 || caretPos == 0)
            return null;

        int targetPos = (caretPos == 3 || caretPos == 6) ? caretPos + 1 : caretPos;

        StringBuilder sb = new StringBuilder(oldText);
        sb.setCharAt(targetPos - 1, digit);
        String newText = sb.toString();

        change.setText(newText);
        change.setRange(0, oldText.length());
        change.setCaretPosition(targetPos);
        change.setAnchor(targetPos);
        return change;
    }

    // Обработка удаления (Backspace)
    private static TextFormatter.Change handleDeletion(TextFormatter.Change change, String oldText) {
        if (oldText.isEmpty()) return null;

        int caretPos = change.getCaretPosition();

        // При Backspace удаляем символ слева от курсора, но не разделители (точки)
        int deletePos = (caretPos == 2 || caretPos == 5) ? caretPos - 1 : caretPos;

        // Заменяем символ на плейсхолдер
        StringBuilder sb = new StringBuilder(oldText);
        sb.setCharAt(deletePos, PLACEHOLDER);           //0 0 . 0 0 . 0 0 0 0
                                                        //0 1 2 3 4 5 6 7 8 9
        String newText = sb.toString();

        change.setText(newText);
        change.setRange(0, oldText.length());
        change.setCaretPosition(deletePos);
        change.setAnchor(deletePos);
        return change;
    }
}