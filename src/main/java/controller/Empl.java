package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Dept;
import model.EmplOperations;

import java.net.URL;
import java.util.ResourceBundle;


public class Empl implements Initializable {

    @FXML
    public static TableView emplTv;
    @FXML
    public Button btnUpdateEmpl;

    public static model.Empl employeeToUpdate;

    public static String addOrEdit;

    TableColumn cId,cFname,cLname,cIdDept;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        btnUpdateEmpl.setDisable(true);

        new Thread(()->{


        //System.out.println(getClass().getResource("../view/style.css").toExternalForm());

            Platform.runLater(()->{
                emplTv.getParent().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
                emplTv.setVisible(true);
                Label l=new Label("Loading...");
                l.setFont(new Font("Arial",35.0));
                emplTv.setPlaceholder(l);
                cId=new TableColumn("ID");
                cFname=new TableColumn("First Name");
                cLname=new TableColumn("Last Name");
                cIdDept=new TableColumn("Departement");

                emplTv.getColumns().add(cId);
                emplTv.getColumns().add(cFname);
                emplTv.getColumns().add(cLname);
                emplTv.getColumns().add(cIdDept);

                emplTv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println(emplTv.getSelectionModel().getSelectedIndex());

                        System.out.println(((model.Empl)emplTv.getSelectionModel().getSelectedItem()).getFname());
                    }
                });
            });




        ObservableList<model.Empl> data = FXCollections.observableArrayList();
        EmplOperations emplOperations=new EmplOperations();

        data.addAll(emplOperations.getAll());

//        for (model.Empl e:emplOperations.getAll()) {
//            System.out.println(e.getId());
//        }

        cId.setCellValueFactory(new PropertyValueFactory<model.Empl,Integer>("id"));
        cFname.setCellValueFactory(new PropertyValueFactory<model.Empl,String>("fname"));
        cLname.setCellValueFactory(new PropertyValueFactory<model.Empl,String>("lname"));
//        cIdDept.setCellValueFactory(new PropertyValueFactory<model.Empl, Dept>("dept"));

            Platform.runLater(()->{
                emplTv.setItems(data);

                TableColumn cIdDeptId=new TableColumn("ID");
                TableColumn cIdDeptLabel=new TableColumn("Label");
                cIdDept.getColumns().addAll(cIdDeptId,cIdDeptLabel);
                cIdDeptId.setCellValueFactory(new PropertyValueFactory<model.Empl, Dept>("id_dept"));
                cIdDeptLabel.setCellValueFactory(new PropertyValueFactory<model.Empl, Dept>("label_dept"));


                cIdDeptId.setCellFactory(new Callback<TableColumn, TableCell>() {
                    @Override
                    public TableCell call(TableColumn param) {
                        return new TableCell(){
                            @Override
                            protected void updateItem(Object item, boolean empty) {
                                super.updateItem(item, empty);
                                if(empty) return;
                                if(Integer.parseInt(item.toString())>=2){
                                    getStyleClass().add("c1");

                                }
                                    setText(item.toString());
                            }
                        };
                    }
                });

                emplTv.setRowFactory(new Callback<TableView, TableRow>() {
                    @Override
                    public TableRow call(TableView param) {
                        return new TableRow<model.Empl>(){
                            @Override
                            protected void updateItem(model.Empl item, boolean empty) {
                                super.updateItem(item, empty);
                                if(empty==true) return;
                                if (getIndex() % 2==0) {
                                    getStyleClass().add("t2");
                                } else {
                                    getStyleClass().add("t1");
                                }
                            }
                        };
                    }
                });


            });

        }).start();


        emplTv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(emplTv.getSelectionModel().getSelectedItem()==null){
                    btnUpdateEmpl.setDisable(true);
                }else{
                    btnUpdateEmpl.setDisable(false);
                }
            }
        });



    }

    public static Stage primaryStage=new Stage();
    public void btnUpdateEmplClick() throws Exception{
        if(emplTv.getSelectionModel().getSelectedItem()==null){
            Tooltip t=new Tooltip("Hello");
            btnUpdateEmpl.setTooltip(t);
            return ;
        }
        model.Empl employee = (model.Empl) emplTv.getSelectionModel().getSelectedItem();
        System.out.println("You want to update the employee: ");
        System.out.println("{\n"+employee.getId());
        System.out.println(employee.getFname());
        System.out.println(employee.getLname());
        System.out.println(employee.getDept().getId());
        System.out.println(employee.getDept().getLabel()+"}");
        this.employeeToUpdate=employee;

        addOrEdit="EDIT";
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/empl_update.fxml"));
        primaryStage.setTitle("Update the selected employee - Departements Management System");
        //primaryStage.setMaximized(true);
        try{
            primaryStage.initModality(Modality.APPLICATION_MODAL);
        }catch (Exception e){

        }
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

    }


    public void btnAddEmplClick() throws Exception{
        addOrEdit="ADD";
        Parent root = FXMLLoader.load(getClass().getResource("../../resources/empl_update.fxml"));
        primaryStage.setTitle("Add a new employee - Departements Management System");
        //primaryStage.setMaximized(true);
        try{
            primaryStage.initModality(Modality.APPLICATION_MODAL);
        }catch (Exception e){

        }
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



}
