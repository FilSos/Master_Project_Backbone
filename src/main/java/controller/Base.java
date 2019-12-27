package controller;

import converter.DbDataConverter;
import converter.JarPathConverter;
import converter.PasswordEncryption;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DbData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static view.Start.mainController;


public class Base implements Initializable {

    @FXML
    public TextField dbName;
    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public ComboBox<DbData> dbData;
    @FXML
    public Button btnSave;
    @FXML
    public TextField queryString;
    @FXML
    public Label dbNameError;

    private String programPath = JarPathConverter.getPathToResources();

    private String encryptedPassword;

    private boolean isUsed;

    private ArrayList<DbData> dbDataList = new ArrayList<>();
    private static Logger logger = LogManager.getLogger(Base.class);


    public Base() throws URISyntaxException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbNameError.setVisible(false);
        dbData.setConverter(new DbDataConverter());
        btnSave.setDisable(false);
        dbData.getItems().addAll(createdDbList());
        model.Base selectedBase;
        if (null != mainController.baseList.getSelectionModel().getSelectedItem() && mainController.btnEdit.isArmed()) {
            selectedBase = mainController.baseList.getSelectionModel().getSelectedItem();
            dbName.setText(selectedBase.getName());
            username.setText(selectedBase.getUsername());
            password.setText(selectedBase.getPassword());
            encryptedPassword = password.getText();
            OptionalInt indexOpt = IntStream.range(0, dbDataList.size())
                    .filter(i -> selectedBase.getDriver().equals(dbDataList.get(i).getDriver()))
                    .findFirst();
            if (indexOpt.isPresent()) {
                this.dbData.getSelectionModel().select(indexOpt.getAsInt());
            }
            queryString.setText(selectedBase.getQueryString());
        } else if (mainController.btnAddBase.isArmed()) {
            mainController.baseList.getSelectionModel().clearSelection();
            mainController.btnDelete.setVisible(false);
            mainController.btnEdit.setVisible(false);
        }
    }

    private ArrayList<DbData> createdDbList() {
        dbDataList.add(new DbData("DB2", "com.ibm.db2.jcc.DB2Driver", "jdbc:db2://", "org.hibernate.dialect.DB2Dialect"));
        dbDataList.add(new DbData("PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql://", "org.hibernate.dialect.PostgreSQLDialect"));
        dbDataList.add(new DbData("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://", "org.hibernate.dialect.MySQLDialect"));
        dbDataList.add(new DbData("Microsoft SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://", "org.hibernate.dialect.SQLServerDialect"));
        dbDataList.add(new DbData("SAP DB", "com.sap.db.jdbc.Driver", "jdbc:sap://", "org.hibernate.dialect.SAPDBDialect"));
        //TODO  driver do ogarniecia
        //dbDataList.add(new DbData("Oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@127.0.0.1:55/", "org.hibernate.dialect.OracleDialect"));
//        dbDataList.add(new DbData("Sybase", "test3", "jdbc:mysql://localhost:3306/", "org.hibernate.dialect.SybaseDialect"));
//        dbDataList.add(new DbData("Informix", "test3", "jdbc:mysql://localhost:3306/", "org.hibernate.dialect.InformixDialect"));
//        dbDataList.add(new DbData("HypersonicSQL", "test3", "jdbc:mysql://localhost:3306/", "org.hibernate.dialect.HSQLDialect"));
//        dbDataList.add(new DbData("Ingres", "test3", "jdbc:mysql://localhost:3306/", "org.hibernate.dialect.IngresDialect"));
//        dbDataList.add(new DbData("Firebird", "test3", "jdbc:mysql://localhost:3306/", "org.hibernate.dialect.FirebirdDialect"));

        return dbDataList;
    }

    public void btnSaveClick() throws Exception {
        logger.info("Zapisz dane bazy");
        isUsed = false;
        String dbName = this.dbName.getText();
        ComboBox<model.Base> baseList = mainController.baseList;
        ObservableList<model.Base> baseListItems = baseList.getItems();
        for (model.Base base : baseListItems) {
            if (base.getName().equals(dbName) && null != mainController.baseList.getSelectionModel().getSelectedItem() && !mainController.baseList.getSelectionModel().getSelectedItem().getName().equals(dbName)) {
                dbNameError.setVisible(true);
                logger.info("Baza o podanej nazwie już istnieje!");
                isUsed = true;
            } else if (base.getName().equals(dbName) && null == mainController.baseList.getSelectionModel().getSelectedItem()) {
                dbNameError.setVisible(true);
                logger.info("Baza o podanej nazwie już istnieje!");
                isUsed = true;
            }
        }
        if (!isUsed) {

            String dialect = dbData.getSelectionModel().getSelectedItem().getDialect();
            String driver = dbData.getSelectionModel().getSelectedItem().getDriver();
            String url = dbData.getSelectionModel().getSelectedItem().getUrl();
            String username = this.username.getText();
            String password = this.password.getText();
            if (encryptedPassword != null && encryptedPassword.equals(password)) {
                logger.info("Hasło nie zostało zmienione, szyfrowanie pominięte");
            } else {
                encryptedPassword = PasswordEncryption.encryptPassword(password);
            }
            String queryString = this.queryString.getText();
            logger.info("Show values: " + "\n" + "DB Name: " + dbName + "\n" + "Url: " + url + "\n" + "Query string: " + queryString + "\n" +
                    "Username: " + username + "\n" + "Password: " + encryptedPassword + "\n" + "Driver: " + driver + "\n" + "Dialect: " + dialect);

            if (null == mainController.baseList.getSelectionModel().getSelectedItem()) {
                logger.info("Nowa baza");
                model.Base base = new model.Base();
                base.setName(dbName);
                base.setDriver(driver);
                base.setUrl(url);
                base.setDialect(dialect);
                base.setUsername(username);
                base.setPassword(encryptedPassword);
                base.setQueryString(queryString);
                mainController.addDbToCombobox(base);
            } else {
                logger.info("Istniejąca baza");
                model.Base selectedBase = mainController.baseList.getSelectionModel().getSelectedItem();
                String oldDbName = selectedBase.getName();
                selectedBase.setName(dbName);
                selectedBase.setDriver(driver);
                selectedBase.setUrl(url);
                selectedBase.setDialect(dialect);
                selectedBase.setUsername(username);
                selectedBase.setPassword(encryptedPassword);
                selectedBase.setQueryString(queryString);
                mainController.deleteDbFromCombobox(oldDbName);
                mainController.addDbToCombobox(selectedBase);

            }

            try (OutputStream output = new FileOutputStream(programPath + "/" + dbName + ".properties")) {
                Properties prop = new Properties();
                prop.setProperty("db.name", dbName);
                prop.setProperty("db.dialect", dialect);
                prop.setProperty("db.driver", driver);
                prop.setProperty("db.url", url);
                prop.setProperty("db.username", username);
                prop.setProperty("db.password", encryptedPassword);
                prop.setProperty("db.queryString", queryString);

                prop.store(output, null);
                logger.info(prop);

            } catch (IOException io) {
                io.printStackTrace();
            }
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
    }

}
