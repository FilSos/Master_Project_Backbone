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

        TableColumn index = new TableColumn("Index number");
        TableColumn queryString = new TableColumn("Query");
        TableColumn typos = new TableColumn("Typos");
        TableColumn result = new TableColumn("Result %");
        resultList.getColumns().addAll(index, queryString, typos, result);
        Map<Boolean, List<QueryData>> resultList = mainController.resultList;
        List<QueryData> list = resultList.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        ObservableList<QueryData> items = FXCollections.observableArrayList(list);
        index.setCellValueFactory(new PropertyValueFactory<QueryData,String>("identifier"));
        queryString.setCellValueFactory(new PropertyValueFactory<QueryData,String>("queryString"));
        typos.setCellValueFactory(new PropertyValueFactory<QueryData,Integer>("typos"));
        //TODO potrzebuje miec Integera, a nei liste
        result.setCellValueFactory(new PropertyValueFactory<QueryData,List>("result"));

        this.resultList.setItems(items);
    }
}
