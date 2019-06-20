package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Base;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main implements Initializable {

//    public void btnEmpClick() throws Exception{
//        System.out.println("BtnEmplClick");
//        Stage primaryStage=new Stage();
//        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("empl.fxml")));
//        primaryStage.setTitle("Employees - Departements Management System");
//        primaryStage.setMaximized(true);
//        primaryStage.setScene(new Scene(root));
//        primaryStage.initModality(Modality.APPLICATION_MODAL);
//        primaryStage.show();
//    }

    private ObservableList<ComboLoader.Item> obsItems;
    //TODO Dynamiczne dodawanie itemow do comboboxa podczas intitializacji: odczytywanie danych z plikow properties i dodawnie odpowiednich pol do obiektu base, dodanie kazdego obiektu base do listy , po czym wczytanie jej do comboboxa
    @FXML
    public ComboBox<model.Base> baseSelect = new ComboBox<>();
    private Text textNamePrice = new Text();

//    public Main() {
//
//        obsItems = FXCollections.observableArrayList(createItems());
//    }
//
//    private List<ComboLoader.Item> createItems() {
//        return IntStream.rangeClosed(0, 5)
//                .mapToObj(i -> "Item " + i)
//                .map(ComboLoader.Item::new)
//                .collect(Collectors.toList());
//    }
//
//    //name of this methods corresponds to itemLoader.items in fxml.
//    //if xml name was itemLoader.a this method should have been
//    //getA(). A bit odd
//    public ObservableList<ComboLoader.Item> getItems() {
//
//        return obsItems;
//    }
//
//    public static class Item {
//
//        private final StringProperty name = new SimpleStringProperty();
//
//        public Item(String name) {
//            this.name.set(name);
//        }
//
//        public final StringProperty nameProperty() {
//            return name;
//        }
//    }

    void addDbToCombobox(model.Base base) {
        baseSelect.getItems().add(base);
        ObservableList<Base> items = baseSelect.getItems();
        for (Base b : items) {
            System.out.println("pokaz:" + b.getName());
        }

        System.out.println("wielkosc listy: " + items.size());
        System.out.println("Dodano baze o nazwie: " + base.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
//
//        model.Base base = new model.Base();
//        base.setUrl("dupa.pl");
//        base.setName("dupa");
//        baseSelect.getItems().add(base);
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

    public void btnProgramStartClick() {
        System.out.println("Wystartuj program");
    }

    public void btnAddCSVClick() {
        System.out.println("Dodaj plik CSV");
    }

}
