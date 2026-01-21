package com.example.dsa_visual_lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Load the Home Page FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/dsa_visual_lab/view/home/home-view.fxml"));

        // 2. Create the Scene with a comfortable size for the Grid
        Scene scene = new Scene(fxmlLoader.load(), 1050, 700);

        // 3. Link the vibrant CSS file
        // This line connects the 'home.css' we created to the UI
        String css = Objects.requireNonNull(getClass().getResource("/com/example/dsa_visual_lab/view/styles/home.css")).toExternalForm();
        scene.getStylesheets().add(css);

        // 4. Configure the Stage (Window)
        stage.setTitle("DSA Visual Lab - Pro Version");
        stage.setScene(scene);

        // Prevents the UI from looking weird if made too small
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}