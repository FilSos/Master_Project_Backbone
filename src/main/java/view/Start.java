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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Logger logger = LogManager.getLogger(Main.class);
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
        primaryStage.setTitle("SQLminator - parser for query data: ver. " + Main.CURRENT_VERSION + ", " + Main.LAST_CHANGE_DATE);
        //primaryStage.setMaximized(true);
        mainController = loader.getController();
        Parameters parameters = this.getParameters();
        List<String> rawNames = parameters.getRaw();
        if (!rawNames.isEmpty()) {
            List<File> files = new ArrayList<>();
            for (String fileName : rawNames) {
                File file = new File(fileName);
                files.add(file);
            }
            mainController.queryFiles = files;
            mainController.csvStatus.setText("Wybranych plików z zapytaniami: " + mainController.queryFiles.size());
        }

        logger.info(parameters.getRaw());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
