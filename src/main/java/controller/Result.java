package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import query.QueryData;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static view.Main.mainController;

public class Result implements Initializable {


    @FXML
    public ListView<String> resultList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Map<Boolean, List<QueryData>> resultList = mainController.resultList;
        ObservableList<String> items = FXCollections.observableArrayList();
        resultList.forEach((key, value) -> value.forEach(query ->
                items.addAll(query.getQueryString(),query.getIdentifier(),String.valueOf(query.getTypos()),"Poprawność zapytania: " + (100-(query.getTypos()*5)) + "%")
        ));

        this.resultList.setItems(items);
    }
}
