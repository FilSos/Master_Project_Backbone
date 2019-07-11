package controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Base;
import parser.SqlDissecter;
import query.QueryData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Main implements Initializable {

    @FXML
    public ComboBox<model.Base> baseList = new ComboBox<>();

    @FXML
    public Label csvStatus;

    @FXML
    public Label startProgramStatus;

    private File queryFile = null;

    void addDbToCombobox(model.Base base) {
        baseList.getItems().add(base);
        ObservableList<Base> items = baseList.getItems();
        for (Base b : items) {
            System.out.println("pokaz:" + b.getName());
        }

        System.out.println("wielkosc listy: " + items.size());
        System.out.println("Dodano baze o nazwie: " + base.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        baseList.setConverter(new StringConverter<Base>() {
            @Override
            public String toString(Base object) {
                return object.getName();
            }

            @Override
            public Base fromString(String string) {
                return null;
            }
        });
        File dir = new File("src/main/resources/");

        File[] propertyFiles = dir.listFiles((dir1, name) -> name.endsWith(".properties"));
        if (propertyFiles != null) {
            for (File propertyFile : propertyFiles) {
                FileReader reader;
                try {
                    reader = new FileReader(propertyFile);
                    Properties p = new Properties();
                    p.load(reader);
                    model.Base base = new model.Base();
                    base.setPassword(p.getProperty("db.password"));
                    base.setUrl(p.getProperty("db.url"));
                    base.setName(p.getProperty("db.name"));
                    base.setUsername(p.getProperty("db.username"));
                    base.setDriver(p.getProperty("db.driver"));
                    baseList.getItems().add(base);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    public void btnAddBaseClick() throws IOException {
        System.out.println("Dodaj baze");
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("base.fxml")));
        primaryStage.setTitle("Dodaj bazę danych");
        primaryStage.setMaximized(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        // primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }

    public void btnProgramStartClick() throws IOException {
        System.out.println("Wystartuj program");

        if (queryFile != null) {
            List<QueryData> queries = Files.lines(queryFile.toPath())
                    .map(this::mapToQueryData)
                    .collect(Collectors.toList());
            startProgramStatus.setText("");
            SqlDissecter sqlDissecter = new SqlDissecter();
            Platform.runLater(() -> sqlDissecter.evaluateQueries(queries));
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("result.fxml")));
            primaryStage.setTitle("Wynik");
            primaryStage.setMaximized(false);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);
            // primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.show();

        } else {
            startProgramStatus.setText("Nie wybrano pliku z zapytaniami!");
        }
    }

    private QueryData mapToQueryData(String string) {
        String[] split = string.split("\\|");
        if (split[0].equals("0")) {
            return new QueryData(split[1], split[0], true);
        }
        return new QueryData(split[1], split[0], false);
    }

    public void btnAddFileClick() {
        System.out.println("Dodaj plik");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt files", "*.txt"),
                new FileChooser.ExtensionFilter("csv files", "*.csv"));
        queryFile = fileChooser.showOpenDialog(null);
        if (queryFile != null) {
            csvStatus.setText("Wybrano: " + queryFile.getName());
            System.out.println("Wybrano: " + queryFile.getName());
        } else {
            csvStatus.setText("Nie wybrano żadnego pliku");
            System.out.println("Nie wybrano żadnego pliku");
        }
    }

}
