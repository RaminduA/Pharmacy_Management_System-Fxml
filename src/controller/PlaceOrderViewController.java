package controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import manager.CustomerManager;
import manager.MedicineManager;
import manager.OrderManager;
import model.Customer;
import model.Medicine;
import model.OrderDetail;
import model.Order;
import model.tableModel.CartTM;
import util.DateTimeUtil;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class PlaceOrderViewController {

    public TableView<CartTM> tblCart;
    public TableColumn<CartTM,String> colMedicineCode;
    public TableColumn<CartTM,String> colName;
    public TableColumn<CartTM,Double> colUnitPrice;
    public TableColumn<CartTM,Integer> colQuantity;
    public TableColumn<CartTM,Double> colPrice;

    public TextField txtCustId;
    public TextField txtCustName;
    public TextField txtCustAddress;
    public TextField txtCustContact;

    public TextField txtMedicineCode;
    public TextField txtMedName;
    public TextField txtMedDescription;
    public TextField txtQtyOnHand;
    public TextField txtMedUnitPrice;

    public TextField txtQuantity;

    public JFXButton btnClearRecord;
    public JFXButton btnAddToCart;
    public JFXButton btnPlaceOrder;
    public Label lblTotal;
    public Label lblOrderId;
    public ComboBox<String> cmbCustomer;
    public ComboBox<String> cmbMedicine;


    LinkedHashMap<TextField, Pattern> validationList=new LinkedHashMap<>();

    Pattern idPattern=Pattern.compile("^(C-)[0-9]{5}$");
    Pattern namePattern=Pattern.compile("^[A-Z][a-z]*([ ][A-Z][a-z]*)*$");
    Pattern addressPattern=Pattern.compile("^[A-Z][A-z0-9,/ ]*$");
    Pattern contactPattern=Pattern.compile("^(07)[01245678]([-][0-9]{7})$");
    Pattern codePattern=Pattern.compile("^(M-)[0-9]{5}$");
    Pattern descPattern=Pattern.compile("^[A-Z][A-z0-9, ]*$");
    Pattern pricePattern=Pattern.compile("^[1-9][0-9]{0,6}([.][0-9]{2})?$");
    Pattern quantityPattern=Pattern.compile("^[1-9][0-9]*$");

    ObservableList<CartTM> obList= FXCollections.observableArrayList();
    private CartTM selectedRecord;

    public void initialize(){
        storeValidations();
        setOrderId();
        calculateTotal();

        colMedicineCode.setCellValueFactory(new PropertyValueFactory<>("medicineCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        cmbCustomer.getItems().addAll(new CustomerManager().getAllCustomerIds());
        cmbMedicine.getItems().addAll(new MedicineManager().getAllMedicineCodes());

        cmbCustomer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setCustomerData(newValue);
        });
        cmbMedicine.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setMedicineData(newValue);
        });

        tblCart.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedRecord=newValue;
        });

        listenFieldChange(validationList);

        listenDetailChange(txtQuantity);

        /*System.out.println(new DashboardController().lblDate.getText());
        System.out.println(new DashboardController().lblTime.getText());*/
    }

    private void storeValidations() {
        validationList.put(txtCustId,idPattern);
        validationList.put(txtCustName,namePattern);
        validationList.put(txtCustAddress,addressPattern);
        validationList.put(txtCustContact,contactPattern);
        validationList.put(txtMedicineCode,codePattern);
        validationList.put(txtMedName,namePattern);
        validationList.put(txtMedDescription,descPattern);
        validationList.put(txtQtyOnHand,quantityPattern);
        validationList.put(txtMedUnitPrice,pricePattern);
        validationList.put(txtQuantity,quantityPattern);
    }

    private void listenDetailChange(TextField field) {
        field.textProperty().addListener((observable,oldValue,newValue)->{
            Medicine medicine=new MedicineManager().getMedicine(txtMedicineCode.getText());
            if(medicine==null) {
                new Alert(Alert.AlertType.WARNING, "No medicine selected...").show();
            }else if(!field.getText().equals("") && Integer.parseInt(txtQtyOnHand.getText())<Integer.parseInt(field.getText())){
                field.getParent().setStyle("-fx-border-color: #DB0F0F");
                AnchorPane parent=(AnchorPane) field.getParent();
                Label tag=(Label)parent.getChildren().get(2);
                tag.setStyle("-fx-text-fill: #DB0F0F");
                new Alert(Alert.AlertType.WARNING, "Insufficient quantity...").show();
            }
        });
    }

    private void listenFieldChange(LinkedHashMap<TextField,Pattern> list) {
        for(TextField key: list.keySet()) {
            key.textProperty().addListener((observable,oldValue,newValue)->{
                ValidationUtil.validate(key,list);
            });
        }
    }

    private void calculateTotal() {
        double total=0;
        for(CartTM record:obList){
            total+=record.getPrice();
        }
        lblTotal.setText("Rs. "+String.format("%.2f",total));
    }

    private void setOrderId() {
        lblOrderId.setText(new OrderManager().getOrderId());
    }

    private void setCustomerData(String customerId) {
        Customer customer=new CustomerManager().getCustomer(customerId);
        txtCustId.setText(customer.getId());
        txtCustName.setText(customer.getName());
        txtCustAddress.setText(customer.getAddress());
        txtCustContact.setText(customer.getContact());
    }
    private void setMedicineData(String medicineCode) {
        Medicine medicine=new MedicineManager().getMedicine(medicineCode);
        txtMedicineCode.setText(medicine.getCode());
        txtMedName.setText(medicine.getName());
        txtMedDescription.setText(medicine.getDescription());
        setQuantityOnHand(medicine);
        txtMedUnitPrice.setText(String.format("%.2f",medicine.getUnitSalePrice()));
        txtQuantity.clear();
    }

    private void setQuantityOnHand(Medicine medicine) {
        int newQty=medicine.getQtyOnHand();
        for(CartTM tm: obList){
            if(medicine.getCode().equals(tm.getMedicineCode())){
                newQty-=tm.getQuantity();
            }
        }
        txtQtyOnHand.setText(Integer.toString(newQty));
    }

    private boolean isFormNotFilled(LinkedHashMap<TextField,Pattern> validationList) {
        for(TextField key: validationList.keySet()){
            if(!key.getParent().getStyle().equals("-fx-border-color: #028f02")){
                return true;
            }
        }
        return false;
    }

    private boolean isExists(CartTM cart) {
        for(CartTM cartTM : obList){
            if (cart.getMedicineCode().equals(cartTM.getMedicineCode())) {
                return true;
            }
        }
        return false;
    }

    public void clearRecordOnAction(ActionEvent actionEvent) {
        if(obList.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Cart is Empty.").show();
        }else if(selectedRecord==null){
            new Alert(Alert.AlertType.WARNING, "Please select a row.").show();
        }else{
            Medicine medicine=new MedicineManager().getMedicine(selectedRecord.getMedicineCode());
            for(int i=0; i<obList.size(); i++) {
                if(selectedRecord.getMedicineCode().equals(obList.get(i).getMedicineCode())) {
                    obList.remove(i);
                }
            }
            tblCart.setItems(obList);
            if(txtMedicineCode.getText().equals(medicine.getCode())){
                setQuantityOnHand(medicine);
            }
            calculateTotal();
        }
    }

    public void addToCartOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList)){
            new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
        }else {
            double price=Double.parseDouble(txtQuantity.getText()) * Double.parseDouble(txtMedUnitPrice.getText());
            CartTM cart=new CartTM(
                    txtMedicineCode.getText(),
                    txtMedName.getText(),
                    Double.parseDouble(txtMedUnitPrice.getText()),
                    Integer.parseInt(txtQuantity.getText()),
                    price
            );
            if(isExists(cart)){
                int index=0;
                for(int i=0; i<obList.size(); i++) {
                    if(cart.getMedicineCode().equals(obList.get(i).getMedicineCode())) {
                        index=i;
                    }
                }
                CartTM temp=obList.get(index);
                CartTM newCart=new CartTM(
                        temp.getMedicineCode(),
                        temp.getName(),
                        temp.getUnitPrice(),
                        temp.getQuantity()+cart.getQuantity(),
                        temp.getPrice()+cart.getPrice()
                );
                obList.remove(index);
                obList.add(newCart);
            }else {
                obList.add(cart);
            }
            tblCart.setItems(obList);
            calculateTotal();
            Medicine medicine=new MedicineManager().getMedicine(txtMedicineCode.getText());
            setQuantityOnHand(medicine);
            txtQuantity.clear();
        }
    }

    private void clearAllFields() {
        for(TextField field : validationList.keySet()){
            field.clear();
        }
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        ArrayList<OrderDetail> detailList=new ArrayList<>();
        for(CartTM temp:obList){
            detailList.add(new OrderDetail(
                            lblOrderId.getText(),
                            temp.getMedicineCode(),
                            temp.getUnitPrice(),
                            temp.getQuantity(),
                            temp.getPrice()
                    )
            );
        }
        Order order=new Order(
                lblOrderId.getText(),
                txtCustId.getText(),
                DateTimeUtil.currentDate(1),
                DateTimeUtil.currentTime(1),
                Double.parseDouble(lblTotal.getText().split(" ")[1]),
                detailList
        );
        if (new OrderManager().placeOrder(order)){
            new Alert(Alert.AlertType.CONFIRMATION, "Order placed...").show();
            setOrderId();
            clearAllFields();
            obList.clear();
            tblCart.setItems(obList);
        }else{
            new Alert(Alert.AlertType.WARNING, "Try Again...").show();
        }
    }
}

