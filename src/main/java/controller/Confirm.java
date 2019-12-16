package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static view.Start.mainController;

public class Confirm implements Initializable {

    @FXML
    public Button btnYes;

    @FXML
    public Button btnNo;

    private static Logger logger = LogManager.getLogger(Confirm.class);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnYes.setDisable(false);
        btnNo.setDisable(false);

    }

    @FXML
    public void btnYesClick() {
        logger.info("Usuń");
        mainController.deleteDbFromCombobox(null);
        Stage stage = (Stage) btnYes.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void btnNoClick() {
        logger.info("Nie usuwaj");
        Stage stage = (Stage) btnNo.getScene().getWindow();
        stage.close();
    }
}
