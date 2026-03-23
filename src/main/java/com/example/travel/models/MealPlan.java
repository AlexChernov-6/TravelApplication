package com.example.travel.models;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plans", schema = "public")
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meal_plan")
    private int idMealPlan;
    @Column(name = "meal_name")
    private String mealName;

    public MealPlan() {}

    public MealPlan(int idMealPlan, String mealName) {
        this.idMealPlan = idMealPlan;
        this.mealName = mealName;
    }

    public int getIdMealPlan() {
        return idMealPlan;
    }

    public void setIdMealPlan(int idMealPlan) {
        this.idMealPlan = idMealPlan;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    @Override
    public String toString() {
        return mealName;
    }
}
