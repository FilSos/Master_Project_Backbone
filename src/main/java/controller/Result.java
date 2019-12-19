package controller;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.util.stream.Collectors;

//TODO rozpracować sposób, w którym wyświetlanie się parametrów nie zaburza pracy programu(licznik godzin: 4)
public class Result implements Initializable {

    private static TableColumn index = new TableColumn("Student");
    private static TableColumn exNumber = new TableColumn("Numer zadania");
    private static TableColumn queryString = new TableColumn("Zapytanie");
    private static TableColumn<QueryData, Boolean> parsed = new TableColumn("Czy parsowanie się udało");
    private static TableColumn matchedColumns = new TableColumn("Zgodność kolumn");
    private static TableColumn matchedTables = new TableColumn("Zgodność tabeli");
    private static TableColumn resultMatchScore = new TableColumn("Zgodność z zapytaniem referencyjnym");
    private static TableColumn fragments = new TableColumn("Zgodność fragmentów");
    private static TableColumn<QueryData, String> fragment = new TableColumn("Fragment");
    private static TableColumn<QueryData, String> overlapCoefficient = new TableColumn("Współczynnik pokrycia");
    private static TableColumn<QueryData, String> jaroWinklerSimilarity = new TableColumn("Podobieństwo Jaro - Winklera");
    private static TableColumn typos = new TableColumn("Literówki");
    private static TableColumn result = new TableColumn("Poprawność %");
    private static ObservableList<QueryData> items = null;
    private static int size = 1;

    @FXML
    public TableView<QueryData> resultList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultList.setEditable(true);
        parsed.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isValid()));
        fragments.getColumns().addAll(fragment, overlapCoefficient, jaroWinklerSimilarity);
        resultList.getColumns().addAll(index, exNumber, queryString, parsed, matchedColumns, matchedTables, resultMatchScore, typos, result);
        this.resultList.setItems(items);
    }

    public static void showResultTable(List<QueryData> resultList) {
        //size = 1;
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
        fragment.setCellValueFactory(
                (TableColumn.CellDataFeatures<QueryData, String> data) ->
                {
                    List<FragmentValidationResult> fragmentValidationResults = data.getValue().getFragmentValidationResults();
                    String val = fragmentValidationResults
                            .stream()
                            .map(item -> item.getFragment().getQueryFragment())
                            //.reduce( "", ( acc, item ) -> acc + ", " + item );
                            .collect(Collectors.joining(", "));
                    return new ReadOnlyStringWrapper(val);
                });
        overlapCoefficient.setCellValueFactory(
                (TableColumn.CellDataFeatures<QueryData, String> data) ->
                {
                    List<FragmentValidationResult> fragmentValidationResults = data.getValue().getFragmentValidationResults();
                    String val = fragmentValidationResults
                            .stream()
                            .map(item -> item.getOverlapCoefficient().toString())
                            //.reduce( "", ( acc, item ) -> acc + ", " + item );
                            .collect(Collectors.joining(", "));
                    return new ReadOnlyStringWrapper(val);
                });
        jaroWinklerSimilarity.setCellValueFactory(
                (TableColumn.CellDataFeatures<QueryData, String> data) ->
                {
                    List<FragmentValidationResult> fragmentValidationResults = data.getValue().getFragmentValidationResults();
                    String val = fragmentValidationResults
                            .stream()
                            .map(item -> item.getJaroWinklerSimilarity().toString())
                            //.reduce( "", ( acc, item ) -> acc + ", " + item );
                            .collect(Collectors.joining(", "));
                    return new ReadOnlyStringWrapper(val);
                });

//        fragment.setCellFactory(col -> new TableCell<QueryData, List<FragmentValidationResult>>() {
//            @Override
//            public void updateItem(List<FragmentValidationResult> fragmentValidationResults, boolean empty) {
//                super.updateItem(fragmentValidationResults, empty);
//                if (size != items.size()) {
//                    if (empty) {
//                        setText("brak");
//                    } else {
//                        setText(fragmentValidationResults.stream().map(fragmentValidationResult -> fragmentValidationResult.getFragment().getQueryFragment())
//                                .collect(Collectors.joining(", ")));
//                    }
//                    size++;
//                } else {
//                    return;
//                }
//            }
//        });
//        overlapCoefficient.setCellValueFactory(new PropertyValueFactory<QueryData, List<FragmentValidationResult>>("fragmentValidationResults"));
//        overlapCoefficient.setCellFactory(col -> new TableCell<QueryData, List<FragmentValidationResult>>() {
//            @Override
//            public void updateItem(List<FragmentValidationResult> fragmentValidationResults, boolean empty) {
//                super.updateItem(fragmentValidationResults, empty);
//                if (size != items.size()) {
//                    if (empty) {
//                        setText("brak");
//                    } else {
//                        setText(fragmentValidationResults.stream().map(fragmentValidationResult -> fragmentValidationResult.getOverlapCoefficient().toString())
//                                .collect(Collectors.joining(", ")));
//                    }
//                    size++;
//                } else {
//                    return;
//                }
//            }
//        });
//        jaroWinklerSimilarity.setCellValueFactory(new PropertyValueFactory<QueryData, List<FragmentValidationResult>>("fragmentValidationResults"));
//        jaroWinklerSimilarity.setCellFactory(col -> new TableCell<QueryData, List<FragmentValidationResult>>() {
//            @Override
//            public void updateItem(List<FragmentValidationResult> fragmentValidationResults, boolean empty) {
//                super.updateItem(fragmentValidationResults, empty);
//                if (size != items.size()) {
//                    if (empty) {
//                        setText("brak");
//                    } else {
//                        setText(fragmentValidationResults.stream().map(fragmentValidationResult -> fragmentValidationResult.getJaroWinklerSimilarity().toString())
//                                .collect(Collectors.joining(", ")));
//                    }
//                    size++;
//                } else {
//                    return;
//                }
//            }
//        });

        typos.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("typos"));
        result.setCellValueFactory(new PropertyValueFactory<QueryData, Integer>("score"));
    }
}
