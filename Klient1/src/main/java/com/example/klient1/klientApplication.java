package com.example.klient1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class klientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(klientApplication.class.getResource("klient-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Klient");
        stage.setScene(scene);
        stage.show();

        klientController controller = fxmlLoader.getController();
        controller.setStage(stage);
    }
    public static void main(String[] args)
    {
        launch();
    }
}