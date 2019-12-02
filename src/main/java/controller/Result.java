package controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.CodeFragment;
import model.FragmentValidationResult;
import query.QueryData;

import java.net.URL;
import java.util.*;


public class Result implements Initializable {

    private static TableColumn index = new TableColumn("Student");
    private static TableColumn exNumber = new TableColumn("Numer zadania");
    private static TableColumn queryString = new TableColumn("Zapytanie");
    private static TableColumn<QueryData, Boolean> parsed = new TableColumn("Czy parsowanie się udało");
    private static TableColumn matchedColumns = new TableColumn("Zgodność kolumn");
    private static TableColumn matchedTables = new TableColumn("Zgodność tabeli");
    private static TableColumn resultMatchScore = new TableColumn("Zgodność z zapytaniem referencyjnym");
    private static TableColumn fragments = new TableColumn("Zgodność fragmentów");
    private static TableColumn fragment = new TableColumn("Fragment");
    private static TableColumn overlapCoefficient = new TableColumn("Współczynnik przesunięcia");
    private static TableColumn jaroWinklerSimilarity = new TableColumn("Odległóść Jaro - Winklera");
    private static TableColumn typos = new TableColumn("Literówki");
    private static TableColumn result = new TableColumn("Poprawność %");
    private static ObservableList<QueryData> items = null;
    private String queryDataName = "fragmentValidationResults";

    @FXML
    public TableView<QueryData> resultList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultList.setEditable(true);
        parsed.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isValid()));
        fragments.getColumns().addAll(fragment, overlapCoefficient, jaroWinklerSimilarity);
        resultList.getColumns().addAll(index, exNumber, queryString, parsed, matchedColumns, matchedTables, resultMatchScore, fragments, typos, result);
        this.resultList.setItems(items);
    }

    public static void showResultTable(List<QueryData> resultList) {

        items = FXCollections.observableArrayList(resultList);
        index.setCellValueFactory(new PropertyValueFactory<QueryData, String>("identifier"));
        exNumber.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("exNumber"));
        queryString.setCellValueFactory(new PropertyValueFactory<QueryData, String>("queryString"));
        parsed.setCellFactory(col -> new TableCell<QueryData, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Tak" : "Nie");
            }
        });
        matchedColumns.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("matchedColumns"));
        matchedTables.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("matchedTables"));
        resultMatchScore.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("resultMatchScore"));
        //TODO ogarnac wyciaganie danych z listy FragmentValidationResults
        for (QueryData data : resultList) {
            if (data.getFragmentValidationResults().size() != 0) {
                for (FragmentValidationResult result : data.getFragmentValidationResults()) {
                    fragment.setCellValueFactory(new PropertyValueFactory<CodeFragment, String>("queryFragment"));
                    overlapCoefficient.setCellValueFactory(new PropertyValueFactory<FragmentValidationResult, Double>("overlapCoefficient"));
                    jaroWinklerSimilarity.setCellValueFactory(new PropertyValueFactory<FragmentValidationResult, Double>("jaroWinklerSimilarity"));
                }
            }

        }

        typos.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("typos"));
        result.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("score"));
    }
}
