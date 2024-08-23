package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import manager.SupplierManager;
import model.Supplier;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ManageSuppliersViewController {
    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;

    public TableView<Supplier> tblSupplierList;
    public TableColumn<Supplier,String> colSupplierId;
    public TableColumn<Supplier,String> colName;
    public TableColumn<Supplier,String> colAddress;
    public TableColumn<Supplier,String> colContact;

    public JFXTextField supplierSearchBar;
    public TextField txtSupId1;
    public TextField txtSupName1;
    public TextField txtSupAddress1;
    public TextField txtSupContact1;
    public TextField txtSupId2;
    public TextField txtSupName2;
    public TextField txtSupAddress2;
    public TextField txtSupContact2;

    LinkedHashMap<TextField, Pattern> validationList1=new LinkedHashMap<>();
    LinkedHashMap<TextField, Pattern> validationList2=new LinkedHashMap<>();

    Pattern idPattern=Pattern.compile("^(S-)[0-9]{3}$");
    Pattern namePattern=Pattern.compile("^[A-Z][a-z]*([ ][A-Z][a-z]*)*$");
    Pattern addressPattern=Pattern.compile("^[A-Z][A-z0-9,/ ]*$");
    Pattern contactPattern=Pattern.compile("^(07)[01245678]([-][0-9]{7})$");

    ObservableList<Supplier> obList= FXCollections.observableArrayList();

    public void initialize(){
        storeValidations();
        setSupplierId();
        viewAllSuppliers();

        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        tblSupplierList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> {
                    setSupplierData(newValue);
                }
        );

        listenFieldChange(validationList1);
        listenFieldChange(validationList2);

    }

    private void listenFieldChange(LinkedHashMap<TextField,Pattern> list) {
        for(TextField key: list.keySet()) {
            key.textProperty().addListener((observable,oldValue,newValue)->{
                ValidationUtil.validate(key,list);
            });
        }
    }

    private void setSupplierData(Supplier s) {
        if(s!=null){
            txtSupId2.setText(s.getId());
            txtSupName2.setText(s.getName());
            txtSupAddress2.setText(s.getAddress());
            txtSupContact2.setText(s.getContact());
        }
    }

    private boolean isFormNotFilled(LinkedHashMap<TextField,Pattern> validationList) {
        for(TextField key: validationList.keySet()){
            if(!key.getParent().getStyle().equals("-fx-border-color: #028f02")){
                return true;
            }
        }
        return false;
    }

    private void storeValidations() {
        validationList1.put(txtSupId1,idPattern);
        validationList1.put(txtSupName1,namePattern);
        validationList1.put(txtSupAddress1,addressPattern);
        validationList1.put(txtSupContact1,contactPattern);
        validationList2.put(txtSupId2,idPattern);
        validationList2.put(txtSupName2,namePattern);
        validationList2.put(txtSupAddress2,addressPattern);
        validationList2.put(txtSupContact2,contactPattern);
    }

    public void addSupplierOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList1)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            Supplier supplier=new Supplier(
                    txtSupId1.getText(),
                    txtSupName1.getText(),
                    txtSupAddress1.getText(),
                    txtSupContact1.getText()
            );
            if(new SupplierManager().addSupplier(supplier)){
                new Alert(Alert.AlertType.CONFIRMATION,"Saved...").show();
                clearFields(validationList1);
                viewAllSuppliers();
                setSupplierId();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }

    public void updateSupplierOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList2)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            Supplier supplier=new Supplier(
                    txtSupId2.getText(),
                    txtSupName2.getText(),
                    txtSupAddress2.getText(),
                    txtSupContact2.getText()
            );
            if(new SupplierManager().updateSupplier(supplier)){
                new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                clearFields(validationList2);
                viewAllSuppliers();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }

    public void deleteSupplierOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList2)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            String supplierId=txtSupId2.getText();
            if(new SupplierManager().deleteSupplier(supplierId)){
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                clearFields(validationList2);
                viewAllSuppliers();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }

    private void viewAllSuppliers(){
        obList.clear();
        ArrayList<Supplier> suppliers=new SupplierManager().getAllSuppliers();
        obList.addAll(suppliers);
        tblSupplierList.setItems(obList);
    }

    private void setSupplierId() {
        txtSupId1.setText(new SupplierManager().getSupplierId());
        ValidationUtil.validate(txtSupId1,validationList1);
    }

    private void clearFields(LinkedHashMap<TextField,Pattern> list) {
        for(TextField key: list.keySet()){
            key.clear();
        }
    }
}
