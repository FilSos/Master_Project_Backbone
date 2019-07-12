package controller;

import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import query.QueryData;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static view.Main.mainController;

public class Result implements Initializable {


    @FXML
    public TableView<QueryData> resultList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultList.setEditable(true);

        TableColumn index = new TableColumn("Numer indeksu");
        TableColumn queryString = new TableColumn("Zapytanie");
        //TableColumn parsed = new TableColumn("Czy prasowanie się udało");
        TableColumn typos = new TableColumn("Literówki");
        TableColumn result = new TableColumn("Poprawność %");
        resultList.getColumns().addAll(index, queryString, typos, result);
        List<QueryData> resultList = mainController.resultList;
        ObservableList<QueryData> items = FXCollections.observableArrayList(resultList);
        index.setCellValueFactory(new PropertyValueFactory<QueryData, String>("identifier"));
        queryString.setCellValueFactory(new PropertyValueFactory<QueryData, String>("queryString"));
        //parsed.setCellValueFactory();
        typos.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("typos"));
        //TODO potrzebuje miec Integera, a nei liste
        result.setCellValueFactory(new PropertyValueFactory<QueryData,Integer>("score"));

        this.resultList.setItems(items);
    }
}
