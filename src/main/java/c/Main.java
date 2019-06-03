package c;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class Main {

    public void btnEmpClick() throws Exception{
        System.out.println("BtnEmplClick");
        Stage primaryStage=new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("empl.fxml")));
        primaryStage.setTitle("Employees - Departements Management System");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.show();
    }

    public void btnDeptClick(){
        System.out.println("btnDeptClick");
    }

}
