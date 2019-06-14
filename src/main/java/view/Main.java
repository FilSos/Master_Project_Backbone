package view;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private ComboBox<File> baseSelect = new ComboBox<>();;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")));
        primaryStage.setTitle("SQLminator - parser for query data");
        //primaryStage.setMaximized(true);
        Scene scene=new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
