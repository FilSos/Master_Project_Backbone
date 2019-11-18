package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Base;
import model.ParsingParameters;
import model.QueryDataSet;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import parser.JsonExtractor;
import parser.SqlDissecter;
import query.QueryData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Main implements Initializable {

    @FXML
    public ComboBox<model.Base> baseList = new ComboBox<>();

    @FXML
    public Label csvStatus;

    @FXML
    public Label parametersFileStatus;

    @FXML
    public Label startProgramStatus;

    @FXML
    public Button btnEdit;

    @FXML
    public Button btnDelete;

    @FXML
    public Button btnAddBase;

    private List<File> queryFiles = null;
    private ArrayList<String> fileNames = new ArrayList<>();
    private File parametersFile = null;
    private JSONParser jsonParser = new JSONParser();
    private Gson gson = new Gson();
    private SqlDissecter sqlDissecter = new SqlDissecter();

    List<QueryData> resultList = new ArrayList<>();

    private Configuration cfg;

    void addDbToCombobox(model.Base base) {
        baseList.getItems().add(base);
        ObservableList<Base> items = baseList.getItems();
        for (Base b : items) {
            System.out.println("pokaz:" + b.getName());
        }

        System.out.println("wielkosc listy: " + items.size());
        System.out.println("Dodano baze o nazwie: " + base.getName());
    }

    void deleteDbFromCombobox(String dbName) {
        btnDelete.setVisible(false);
        btnEdit.setVisible(false);
        Base selectedItem = baseList.getSelectionModel().getSelectedItem();
        if (null == dbName) {
            dbName = baseList.getSelectionModel().getSelectedItem().getName();
        }
        File dir = new File("src/main/resources/" + dbName + ".properties");
        if (dir.delete()) {
            baseList.getItems().remove(selectedItem);
            System.out.println(dbName + " deleted from list");
            System.out.println(dir.getName() + " file deleted from resources");
        } else {
            System.out.println(dir.getName() + " doesn't exist");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnDelete.setVisible(false);
        btnEdit.setVisible(false);
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
                    String url = p.getProperty("db.url");
                    String queryString = url.substring(url.indexOf("/") + 2);
                    base.setPassword(p.getProperty("db.password"));
                    base.setUrl(url);
                    base.setName(p.getProperty("db.name"));
                    base.setUsername(p.getProperty("db.username"));
                    base.setDriver(p.getProperty("db.driver"));
                    base.setDialect(p.getProperty("db.dialect"));
                    base.setQueryString(queryString);
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
        //TODO Dołożyłem plik z parametrami(parametersFile). Dostosuj sobie jak chcesz czytać jeden i drugi
        System.out.println("Wystartuj program");
        if (queryFiles != null && parametersFile != null) {
            cfg.buildSessionFactory();

            Map<QueryDataSet, List<QueryData>> dataMap = new HashMap<>();

            scoreQueries(dataMap,  extractParams());

            showResults();

        } else {
            startProgramStatus.setText("Nie wybrano pliku z zapytaniami i/lub parametrami!");
        }
    }

    private void showResults() throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("result.fxml")));
        primaryStage.setTitle("Wynik");
        primaryStage.setMaximized(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        // primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }

    private void scoreQueries(Map<QueryDataSet, List<QueryData>> dataMap, ParsingParameters parsingParameters) {
        for (File queryFile :queryFiles) {
            try (FileReader reader = new FileReader(queryFile)) {
                List<QueryDataSet> queryDataSets = extractData(reader);
                dataMap.putAll(getMap(queryDataSets));
                List<QueryData> queryData = new ArrayList<>(dataMap.values()).get(0);
                resultList.addAll(sqlDissecter.evaluateQueries(queryData, parsingParameters));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private ParsingParameters extractParams() throws IOException {
        ParsingParameters parameters;
        try(FileReader reader = new FileReader(parametersFile)){
            parameters = gson.fromJson(reader, ParsingParameters.class);
        }
        return parameters;
    }

    private List<QueryDataSet> extractData(FileReader reader) throws IOException, ParseException {
        JSONArray obj = (JSONArray) jsonParser.parse(reader);
        JsonArray exerciseList = gson.fromJson(obj.toString(), JsonArray.class);
        JsonExtractor jsonExtractor = new JsonExtractor();
        System.out.println(exerciseList);
        return jsonExtractor.extractData(exerciseList);
    }

    private Map<QueryDataSet, List<QueryData>> getMap(List<QueryDataSet> queryDataSets) {
        Map<QueryDataSet, List<QueryData>> dataMap = new HashMap<>();
        for (QueryDataSet dataSet : queryDataSets) {
            List<QueryData> queryDataList = dataSet.getExcercises().stream()
                    .map(exercise -> mapToQueryData(exercise, dataSet.getEmail(),dataSet.getExcercises().indexOf(exercise)))
                    .collect(Collectors.toList());
            dataMap.put(dataSet, queryDataList);
        }
        return dataMap;
    }

    private QueryData mapToQueryData(String string, String id, int exNumber) {
        if(id.equals("reference")){
            return new QueryData(string,id,true, exNumber);
        }
        return new QueryData(string, id, false, exNumber);
    }

    public void btnAddFileClick() {
        System.out.println("Dodaj pliki z zapytaniami");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json files", "*.json"),
                new FileChooser.ExtensionFilter("txt files", "*.txt"),
                new FileChooser.ExtensionFilter("csv files", "*.csv")
        );
        queryFiles = fileChooser.showOpenMultipleDialog(null);
        if (queryFiles != null) {
            for (File file : queryFiles) {
                fileNames.add(file.getName());
            }
        }
        if (!fileNames.isEmpty()) {
            StringBuilder filesSelected = new StringBuilder();
            for (String name : fileNames) {
                filesSelected.append(name).append("\n");
            }
            csvStatus.setText("Wybrano: " + filesSelected);
            System.out.println("Wybrano: " + filesSelected);
        } else {
            csvStatus.setText("Nie wybrano żadnego pliku z zapytaniami");
            System.out.println("Nie wybrano żadnego pliku z zapytaniami");
        }
    }

    public void btnAddParametersClick() {
        System.out.println("Dodaj plik z parametrami");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json files", "*.json"),
                new FileChooser.ExtensionFilter("txt files", "*.txt"),
                new FileChooser.ExtensionFilter("csv files", "*.csv"));
        parametersFile = fileChooser.showOpenDialog(null);
        if (parametersFile != null) {
            parametersFileStatus.setText("Wybrano: " + parametersFile.getName());
            System.out.println("Wybrano: " + parametersFile.getName());
        } else {
            parametersFileStatus.setText("Nie wybrano żadnego pliku z parametrami");
            System.out.println("Nie wybrano żadnego pliku z parametrami");
        }
    }

    public void baseDataClick() {
        if (null != baseList.getSelectionModel().getSelectedItem()) {
            String dbName = baseList.getSelectionModel().getSelectedItem().getName();
            String username = baseList.getSelectionModel().getSelectedItem().getUsername();
            String password = baseList.getSelectionModel().getSelectedItem().getPassword();
            String driver = baseList.getSelectionModel().getSelectedItem().getDriver();
            String dialect = baseList.getSelectionModel().getSelectedItem().getDialect();
            String url = baseList.getSelectionModel().getSelectedItem().getUrl();
            cfg = new Configuration();
            cfg.configure("Hibernate.cfg.xml"); //hibernate config xml file name
            cfg.getProperties().setProperty("hibernate.connection.username", username);
            cfg.getProperties().setProperty("hibernate.connection.password", password);
            cfg.getProperties().setProperty("hibernate.connection.driver_class", driver);
            cfg.getProperties().setProperty("hibernate.connection.url", url);
            cfg.getProperties().setProperty("hibernate.dialect", dialect);
            System.out.println("Wybrano bazę " + dbName);
            btnDelete.setVisible(true);
            btnEdit.setVisible(true);
        } else {
            System.out.println("Nowa baza danych");
        }
    }

    public void btnEditClick() throws IOException {
        System.out.println("Edytuj baze");
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("base.fxml")));
        primaryStage.setTitle("Edytuj bazę danych");
        primaryStage.setMaximized(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        // primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }

    public void btnDeleteClick() throws IOException {
        System.out.println("Potwierdź usunięcie");
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("confirm.fxml")));
        primaryStage.setTitle("Usuń bazę");
        primaryStage.setMaximized(false);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
        // primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }
}
