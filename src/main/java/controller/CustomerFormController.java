package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;
import model.tm.CustomerTm;

import java.sql.*;

public class CustomerFormController {

    public TableView<CustomerTm> tblCustomer;
    @FXML
    private TextField txtid;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtaddress;

    @FXML
    private TextField txtsalary;

    @FXML
    private TableColumn colid;

    @FXML
    private TableColumn colname;

    @FXML
    private TableColumn coladdress;

    @FXML
    private TableColumn colsalary;

    @FXML
    private TableColumn coloption;

    @FXML
    void SavebtnOnAction(ActionEvent event) {
        Customer c = new Customer(txtid.getText(),
                txtname.getText(),
                txtaddress.getText(),
                Double.parseDouble(txtsalary.getText())
        );
        String sql = "INSERT INTO customer VALUES('"+c.getId()+"','"+c.getName()+"','"+c.getAddress()+"',"+c.getSalary()+")";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
           Connection connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
           Statement stm = connection.createStatement();
            int result = stm.executeUpdate(sql);
            if(result>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved").show();
                loadCustomerTable();
                clearFields();
            }

            connection.close();
        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void reloadbtnOnAction(ActionEvent event) {
        loadCustomerTable();
        clearFields();
    }

    private void clearFields() {
        txtid.clear();
        txtname.clear();
        txtaddress.clear();
        txtsalary.clear();
        txtid.setEditable(true);
        tblCustomer.refresh();
    }

    @FXML
    void uploadbtnOnAction(ActionEvent event) {

    }

    public  void initialize(){
        colid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colname.setCellValueFactory(new PropertyValueFactory<>("name"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colsalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        coloption.setCellValueFactory(new PropertyValueFactory<>("btn"));


        loadCustomerTable();
        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
        setData(newValue);
        });

    }

    private void setData(CustomerTm newValue) {
       if (newValue!=null){
           txtid.setEditable(false);
           txtid.setText(newValue.getId());
           txtname.setText(newValue.getName());
           txtaddress.setText(newValue.getAddress());
           txtsalary.setText(String.valueOf(newValue.getSalary()));
       }
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            Statement stm = connection.createStatement();
            ResultSet result = stm.executeQuery(sql);

            while (result.next()){
                Button btn = new Button("Delete");
                CustomerTm c = new CustomerTm(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDouble(4),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });
                tmList.add(c);
            }
            connection.close();
            tblCustomer.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteCustomer(String id) {
        String sql = "DELETE from customer WHERE id='"+id+"'";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/thogakade","root","1234");
            Statement stm = connection.createStatement();
            int result = stm.executeUpdate(sql);
            if(result>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted").show();
                loadCustomerTable();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();

            }
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}

