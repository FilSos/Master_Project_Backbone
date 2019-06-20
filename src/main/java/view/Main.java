package view;

import controller.ComboLoader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Base;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Text textNamePrice = new Text();
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
      //  loader.setController(this);
        Parent root = loader.load();
        primaryStage.setTitle("SQLminator - parser for query data");
            //primaryStage.setMaximized(true);
//            Base base = new model.Base();
//        base.setUrl("dupa.pl");
//        base.setName("dupa");
//        baseSelect.setPromptText("Wybierz baze danych");
//        baseSelect.setItems(FXCollections.observableArrayList(base));
//
//        baseSelect.valueProperty().
//
//            addListener((obs, oldVal, newVal) ->
//
//            {
//                String selectionText = "Price of the " + newVal.getName() + " is : " + newVal.getUrl();
//
//                System.out.println(selectionText);
//                textNamePrice.setText(selectionText);
//
//            });

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        }
    }
