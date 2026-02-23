package com.example.travel;

import com.example.travel.controllers.PopularDestinationsController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class TravelApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(PopularDestinationsController.createShapePopularDestinations(), 920, 600);
        scene.getStylesheets().add("/styles.css");
        stage.setTitle("Travel – там, где просыпаешься с улыбкой.");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/residential.png"))));
        stage.setScene(scene);
        stage.show();
    }
}
