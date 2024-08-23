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
import manager.MedicineManager;
import model.Medicine;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ManageMedicineViewController {
    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;

    public TableView<Medicine> tblMedicineList;
    public TableColumn<Medicine,String> colMedicineCode;
    public TableColumn<Medicine,String> colName;
    public TableColumn<Medicine,String> colDescription;
    public TableColumn<Medicine,String> colPreservedTime;
    public TableColumn<Medicine,Double> colUnitPrice;
    public TableColumn<Medicine,Double> colUnitSalePrice;

    public JFXTextField medicineSearchBar;
    public TextField txtMedicineCode1;
    public TextField txtName1;
    public TextField txtDescription1;
    public TextField txtPreservedTime1;
    public TextField txtUnitPrice1;
    public TextField txtUnitSalePrice1;
    public TextField txtMedicineCode2;
    public TextField txtName2;
    public TextField txtDescription2;
    public TextField txtPreservedTime2;
    public TextField txtUnitPrice2;
    public TextField txtUnitSalePrice2;

    LinkedHashMap<TextField, Pattern> validationList1=new LinkedHashMap<>();
    LinkedHashMap<TextField, Pattern> validationList2=new LinkedHashMap<>();

    Pattern codePattern=Pattern.compile("^(M-)[0-9]{5}$");
    Pattern namePattern=Pattern.compile("^[A-Z][a-z]*([ ][A-Z][a-z]*)*$");
    Pattern descPattern=Pattern.compile("^[A-Z][A-z0-9, ]*$");
    Pattern timePeriodPattern=Pattern.compile("^[0-9]{1,3}[ ](weeks|months|years)$");
    Pattern pricePattern=Pattern.compile("^[1-9][0-9]{0,6}([.][0-9]{2})?$");

    ObservableList<Medicine> obList= FXCollections.observableArrayList();

    public void initialize(){
        storeValidations();
        setMedicineCode();
        viewAllMedicine();

        colMedicineCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPreservedTime.setCellValueFactory(new PropertyValueFactory<>("preservedTime"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colUnitSalePrice.setCellValueFactory(new PropertyValueFactory<>("unitSalePrice"));

        tblMedicineList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)-> {
                    setMedicineData(newValue);
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

    private void setMedicineData(Medicine m) {
        if(m!=null){
            txtMedicineCode2.setText(m.getCode());
            txtName2.setText(m.getName());
            txtDescription2.setText(m.getDescription());
            txtPreservedTime2.setText(m.getPreservedTime());
            txtUnitPrice2.setText(String.format("%.2f",m.getUnitPrice()));
            txtUnitSalePrice2.setText(String.format("%.2f",m.getUnitSalePrice()));
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
        validationList1.put(txtMedicineCode1,codePattern);
        validationList1.put(txtName1,namePattern);
        validationList1.put(txtDescription1,descPattern);
        validationList1.put(txtPreservedTime1,timePeriodPattern);
        validationList1.put(txtUnitPrice1,pricePattern);
        validationList1.put(txtUnitSalePrice1,pricePattern);
        validationList2.put(txtMedicineCode2,codePattern);
        validationList2.put(txtName2,namePattern);
        validationList2.put(txtDescription2,descPattern);
        validationList2.put(txtPreservedTime2,timePeriodPattern);
        validationList2.put(txtUnitPrice2,pricePattern);
        validationList2.put(txtUnitSalePrice2,pricePattern);
    }

    private void viewAllMedicine(){
        obList.clear();
        ArrayList<Medicine> medicineList=new MedicineManager().getAllMedicine();
        obList.addAll(medicineList);
        tblMedicineList.setItems(obList);
    }

    private void setMedicineCode() {
        txtMedicineCode1.setText(new MedicineManager().getMedicineCode());
        ValidationUtil.validate(txtMedicineCode1,validationList1);
    }

    private void clearFields(LinkedHashMap<TextField,Pattern> list) {
        for(TextField key: list.keySet()){
            key.clear();
        }
    }

    public void addMedicineOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList1)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            Medicine medicine=new Medicine(
                    txtMedicineCode1.getText(),
                    txtName1.getText(),
                    txtDescription1.getText(),
                    txtPreservedTime1.getText(),
                    0,
                    Double.parseDouble(txtUnitPrice1.getText()),
                    Double.parseDouble(txtUnitSalePrice1.getText())
            );
            if(new MedicineManager().addMedicine(medicine)){
                new Alert(Alert.AlertType.CONFIRMATION,"Saved...").show();
                clearFields(validationList1);
                viewAllMedicine();
                setMedicineCode();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }
    public void updateMedicineOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList2)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            Medicine medicine=new Medicine(
                    txtMedicineCode2.getText(),
                    txtName2.getText(),
                    txtDescription2.getText(),
                    txtPreservedTime2.getText(),
                    0,
                    Double.parseDouble(txtUnitPrice2.getText()),
                    Double.parseDouble(txtUnitSalePrice2.getText())
            );
            if(new MedicineManager().updateMedicine(medicine)){
                new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                clearFields(validationList2);
                viewAllMedicine();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }
    public void deleteMedicineOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList2)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else{
            String medicineCode=txtMedicineCode2.getText();
            if(new MedicineManager().deleteMedicine(medicineCode)){
                new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                clearFields(validationList2);
                viewAllMedicine();
            }else{
                new Alert(Alert.AlertType.WARNING,"Try again...").show();
            }
        }
    }
}
