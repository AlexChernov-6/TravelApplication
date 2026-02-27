package com.example.travel.controllers;

import java.util.ArrayList;
import java.util.List;

public class CustomRadioParent {
    private final List<CustomRadioButton> customRadioButtonList;

    public CustomRadioParent() {
        customRadioButtonList = new ArrayList<>();
    }

    public List<CustomRadioButton> getCustomRadioButtonList() {
        return customRadioButtonList;
    }
}
