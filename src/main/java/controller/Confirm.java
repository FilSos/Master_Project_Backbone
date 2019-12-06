package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static view.Start.mainController;

public class Confirm implements Initializable {

    @FXML
    public Button btnYes;

    @FXML
    public Button btnNo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnYes.setDisable(false);
        btnNo.setDisable(false);

    }

    @FXML
    public void btnYesClick() {
        System.out.println("Usuń");
        mainController.deleteDbFromCombobox(null);
        Stage stage = (Stage) btnYes.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void btnNoClick() {
        System.out.println("Nie usuwaj");
        Stage stage = (Stage) btnNo.getScene().getWindow();
        stage.close();
    }
}
