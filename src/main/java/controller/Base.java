package controller;

import converter.DbDataConverter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DbData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import static view.Main.mainController;


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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbData.setConverter(new DbDataConverter());
        btnSave.setDisable(false);
        dbData.getItems().addAll(createdDbList());
    }

    //TODO add data to dbData combobox
    private ArrayList<DbData> createdDbList() {
        ArrayList<DbData> dbDataList = new ArrayList<>();

        dbDataList.add(new DbData("MySQL", "dupa", "dupa1", "dupa2"));
        dbDataList.add(new DbData("MySQL2", "dupa3", "dupa4", "dupa5"));

        return dbDataList;
    }

    @FXML
    public void btnSaveClick() {
        System.out.println("Zapisz dane do bazy");

        String dbName = this.dbName.getText();
        String dialect = dbData.getSelectionModel().getSelectedItem().getDialect();
        String driver = dbData.getSelectionModel().getSelectedItem().getDriver();
        String url = dbData.getSelectionModel().getSelectedItem().getUrl();
        String username = this.username.getText();
        String password = this.password.getText();
        System.out.println("Show values: " + "\n" + "DB Name: " + dbName + "\n" + "Url: " + url + "\n" +
                "Username: " + username + "\n" + "Password: " + password + "\n" + "Driver: " + driver + "\n" + "Dialect: " + dialect);
        model.Base base = new model.Base();
        base.setName(dbName);
        base.setDriver(driver);
        base.setUrl(url);
        base.setDialect(dialect);
        base.setUsername(username);
        base.setPassword(password);
        mainController.addDbToCombobox(base);
        try (OutputStream output = new FileOutputStream("src/main/resources/" + dbName + ".properties")) {
            Properties prop = new Properties();
            prop.setProperty("db.name", dbName);
            prop.setProperty("db.dialect", dialect);
            prop.setProperty("db.driver", driver);
            prop.setProperty("db.url", url);
            prop.setProperty("db.username", username);
            prop.setProperty("db.password", password);

            prop.store(output, null);
            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }


}
