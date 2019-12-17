package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import converter.ExcelImport;
import converter.JarPathConverter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static view.Start.mainController;

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

    private static Logger logger = LogManager.getLogger(Main.class);


    private List<File> queryFiles = null;
    public ArrayList<String> fileNames = null;
    private File parametersFile = null;
    private JSONParser jsonParser = new JSONParser();
    private Gson gson = new Gson();
    private SqlDissecter sqlDissecter = new SqlDissecter();
    private String programPath = JarPathConverter.getPathToResources();

    List<List<QueryData>> resultLists = null;
    protected Configuration cfg;

    public Main() throws URISyntaxException {
    }

    void addDbToCombobox(model.Base base) {
        baseList.getItems().add(base);
        ObservableList<Base> items = baseList.getItems();
        for (Base b : items) {
            logger.info("pokaz:" + b.getName());
        }

        logger.info("wielkosc listy: " + items.size());
        logger.info("Dodano baze o nazwie: " + base.getName());
    }

    void deleteDbFromCombobox(String dbName) {
        btnDelete.setVisible(false);
        btnEdit.setVisible(false);
        Base selectedItem = baseList.getSelectionModel().getSelectedItem();
        if (null == dbName) {
            dbName = baseList.getSelectionModel().getSelectedItem().getName();
        }
        File dir = new File(programPath + "/" + dbName + ".properties");
        if (dir.delete()) {
            baseList.getItems().remove(selectedItem);
            logger.info(dbName + " deleted from list");
            logger.info(dir.getName() + " file deleted from resources");
        } else {
            logger.info(dir.getName() + " doesn't exist");
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
        File dir = new File("/" + programPath);

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
                    base.setDialect(p.getProperty("db.dialect"));
                    base.setQueryString(p.getProperty("db.queryString"));
                    baseList.getItems().add(base);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void btnAddBaseClick() throws IOException {
        logger.info("Dodaj baze");
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

    public void btnProgramStartClick() throws IOException, URISyntaxException {
        logger.info("Wystartuj program");
        if (queryFiles != null && parametersFile != null && baseList.getSelectionModel().getSelectedItem() != null) {
            startProgramStatus.setText("");
            String property = cfg.getProperties().getProperty("hibernate.connection.url");
            logger.info("Link: " + property);
            cfg.buildSessionFactory();
            resultLists = new ArrayList<>();
            scoreQueries(extractParams());
            logger.info("Pokaz wyniki");
            for (List<QueryData> resultList : resultLists) {
                showResults(resultList);
                ExcelImport.doImport(resultList);
            }
            for (String name : fileNames) {
                logger.info("Created excel for " + name);
            }
        } else {
            startProgramStatus.setText("Brakuje plików lub nie wybrano bazy!");
        }
    }

    private void showResults(List<QueryData> resultList) throws IOException {
        Result.showResultTable(resultList);
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

    private void scoreQueries(ParsingParameters parsingParameters) {
        for (File queryFile : queryFiles) {
            Map<QueryDataSet, List<QueryData>> dataMap = new HashMap<>();
            try (FileReader reader = new FileReader(queryFile)) {
                List<QueryData> resultList = new ArrayList<>();
                List<QueryDataSet> queryDataSets = extractData(reader);
                dataMap.putAll(getMap(queryDataSets));
                List<QueryData> queryData = new ArrayList<>(dataMap.values()).stream().flatMap(Collection::stream).collect(Collectors.toList());
                resultList.addAll(sqlDissecter.evaluateQueries(queryData, parsingParameters));
                resultLists.add(resultList);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private ParsingParameters extractParams() throws IOException {
        ParsingParameters parameters;
        try (FileReader reader = new FileReader(parametersFile)) {
            parameters = gson.fromJson(reader, ParsingParameters.class);
        }
        return parameters;
    }

    private List<QueryDataSet> extractData(FileReader reader) throws IOException, ParseException {
        JSONArray obj = (JSONArray) jsonParser.parse(reader);
        JsonArray exerciseList = gson.fromJson(obj.toString(), JsonArray.class);
        JsonExtractor jsonExtractor = new JsonExtractor();
        logger.info(exerciseList);
        return jsonExtractor.extractData(exerciseList);
    }

    private Map<QueryDataSet, List<QueryData>> getMap(List<QueryDataSet> queryDataSets) {
        Map<QueryDataSet, List<QueryData>> dataMap = new HashMap<>();
        for (QueryDataSet dataSet : queryDataSets) {
            List<QueryData> queryDataList = dataSet.getExcercises().stream()
                    .map(exercise -> mapToQueryData(exercise, dataSet.getEmail(), dataSet.getExcercises().indexOf(exercise)))
                    .collect(Collectors.toList());
            dataMap.put(dataSet, queryDataList);
        }
        return dataMap;
    }

    private QueryData mapToQueryData(String string, String id, int exNumber) {
        if (id.equals("reference")) {
            return new QueryData(string, id, true, exNumber);
        }
        return new QueryData(string, id, false, exNumber);
    }

    public void btnAddFileClick() {
        queryFiles = null;
        fileNames = new ArrayList<>();
        csvStatus.setText("");
        logger.info("Dodaj pliki z zapytaniami");
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
            csvStatus.setText("Wybranych plików z zapytaniami: " + queryFiles.size());
            logger.info("Wybranych plików z zapytaniami: " + queryFiles.size());
        } else {
            csvStatus.setText("Nie wybrano żadnego pliku z zapytaniami");
            logger.info("Nie wybrano żadnego pliku z zapytaniami");
        }
    }

    public void btnAddParametersClick() {
        parametersFile = null;
        parametersFileStatus.setText("");
        logger.info("Dodaj plik z parametrami");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("json files", "*.json"),
                new FileChooser.ExtensionFilter("txt files", "*.txt"),
                new FileChooser.ExtensionFilter("csv files", "*.csv"));
        parametersFile = fileChooser.showOpenDialog(null);
        if (parametersFile != null) {
            parametersFileStatus.setText("Plik parametrów: " + parametersFile.getName());
            logger.info("Plik parametrów: " + parametersFile.getName());
        } else {
            parametersFileStatus.setText("Nie wybrano żadnego pliku z parametrami");
            logger.info("Nie wybrano żadnego pliku z parametrami");
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
            String queryString = baseList.getSelectionModel().getSelectedItem().getQueryString();
            cfg = new Configuration();
            cfg.addResource("Hibernate.cfg.xml");
            //cfg.configure("Hibernate.cfg.xml"); //hibernate config xml file name
            cfg.getProperties().setProperty("hibernate.connection.username", username);
            cfg.getProperties().setProperty("hibernate.connection.password", password);
            cfg.getProperties().setProperty("hibernate.connection.driver_class", driver);
            cfg.getProperties().setProperty("hibernate.connection.url", url + queryString);
            cfg.getProperties().setProperty("hibernate.dialect", dialect);
            HibernateUtil.modifyConfiguration(cfg);
            logger.info("Wybrano bazę " + dbName);
            btnDelete.setVisible(true);
            btnEdit.setVisible(true);
        } else {
            logger.info("Nowa baza danych");
        }
    }

    public void btnEditClick() throws IOException {
        logger.info("Edytuj baze");
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
        logger.info("Potwierdź usunięcie");
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
