package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


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

    private controller.Main controller;
//TODO nie robic nowych 'instancji' controllera tylko brac juz istniejace i do nich dodawac nowe wartosci do comboboxa
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnSave.setDisable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        try {
            loader.load();
            controller = loader.getController();
            loader.setController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }


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
        controller.addDbToCombobox(base);
        try (OutputStream output = new FileOutputStream( "src/main/resources/" + dbName + "_config")) {
            Properties prop = new Properties();
            prop.setProperty("db.name", dbName);
            prop.setProperty("db.driver", driver);
            prop.setProperty("db.url", url);
            prop.setProperty("db.username", username);
            prop.setProperty("db.password", password);

            prop.store(output, null);
            System.out.println(prop);

        }catch (IOException io){
            io.printStackTrace();
        }

        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

}
