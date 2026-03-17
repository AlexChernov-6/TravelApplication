package com.example.travel;

import com.example.travel.controllers.PopularDestinationsController;
import com.example.travel.util.ConfigManager;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class TravelApplication extends Application {
    private final ConfigManager manager = new ConfigManager();
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(PopularDestinationsController.createShapePopularDestinations()
                , manager.getWindowWidth(), manager.getWindowHeight());
        scene.getStylesheets().add("/styles.css");
        stage.setTitle("Travel – там, где просыпаешься с улыбкой.");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass()
                .getResourceAsStream("/images/residential.png"))));
        stage.setScene(scene);
        stage.show();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/map.html", exchange -> {
            try (InputStream is = getClass().getResourceAsStream("/com/example/travel/map.html")) {
                if (is == null) {
                    String error = "File not found";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    System.err.println("map.html не найден в ресурсах");
                    return;
                }
                byte[] response = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        });
        server.setExecutor(null);
        System.out.println("HTTP Server started on http://localhost:8080");
        server.start();
    }
}
