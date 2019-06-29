package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Base;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static controller.Main mainController;
    @FXML
    public ComboBox<Base> baseSelect = new ComboBox<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        baseSelect.setConverter(new StringConverter<Base>() {
            @Override
            public String toString(Base object) {
                return object.getName();
            }

            @Override
            public Base fromString(String string) {
                return null;
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SQLminator - parser for query data");
        //primaryStage.setMaximized(true);
        mainController = loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
