package com.example.javafx_projcet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Employee.fxml")));
            Scene scene = new Scene(parent);
            stage.setTitle("FXML PROJECT");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
            System.out.println("the app is not working");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
