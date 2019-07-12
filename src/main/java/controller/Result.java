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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
        TableColumn result = new TableColumn("Result");
//        index.setCellValueFactory(new PropertyValueFactory("1"));
//        queryString.setCellValueFactory(new PropertyValueFactory("2"));
//        typos.setCellValueFactory(new PropertyValueFactory("3"));
//        result.setCellValueFactory(new PropertyValueFactory("4"));
        resultList.getColumns().setAll(index, queryString, typos,result);
        Map<Boolean, List<QueryData>> resultList = mainController.resultList;
        ObservableList<QueryData> items = FXCollections.observableArrayList();
        resultList.forEach((key, value) -> value.forEach(query ->
                items.setAll(query.getIdentifier(),query.getQueryString(),String.valueOf(query.getTypos()),"Poprawność zapytania: " + (100-(query.getTypos()*5)) + "%")
        ));

        this.resultList.setItems(items);
    }
}
