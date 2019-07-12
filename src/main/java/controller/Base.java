package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static view.Main.mainController;


public class Base implements Initializable {

    @FXML
    public TextField dbName;
    @FXML
    public TextField driver;
    @FXML
    public TextField url;
    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public Button btnSave;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSave.setDisable(false);
    }

    @FXML
    public void btnSaveClick() {
        System.out.println("Zapisz dane do bazy");
        String dbName = this.dbName.getText();
        String driver = this.driver.getText();
        String url = this.url.getText();
        String username = this.username.getText();
        String password = this.password.getText();
        System.out.println("Show values: " + "\n" + "DB Name: " + dbName + "\n" + "Url: " + url + "\n" +
                "Username: " + username + "\n" + "Password: " + password + "\n" + "Driver: " + driver);
        model.Base base = new model.Base();
        base.setName(dbName);
        base.setDriver(driver);
        base.setUrl(url);
        base.setUsername(username);
        base.setPassword(password);
        mainController.addDbToCombobox(base);
        try (OutputStream output = new FileOutputStream("src/main/resources/" + dbName + ".properties")) {
            Properties prop = new Properties();
            prop.setProperty("db.name", dbName);
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
