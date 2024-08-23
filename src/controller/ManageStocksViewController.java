package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import manager.MedicineManager;
import manager.StockManager;
import model.Medicine;
import model.PriceDetail;
import model.Stock;
import model.StockDetail;
import util.DateTimeUtil;
import util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ManageStocksViewController {

    public TableView<StockDetail> tblDetailList;
    public TableColumn<StockDetail,String> colBatchNo2;
    public TableColumn<StockDetail,String> colMedicineCode;
    public TableColumn<StockDetail,String> colExpiryDate;
    public TableColumn<StockDetail,Double> colUnitPrice;
    public TableColumn<StockDetail,Integer> colQuantity;
    public TableColumn<StockDetail,Double> colPrice;

    public TableView<Stock> tblStockList;
    public TableColumn<Stock,String> colBatchNo1;
    public TableColumn<Stock,String> colSupplierId;
    public TableColumn<Stock,String> colShipmentDate;
    public TableColumn<Stock,String> colTime;
    public TableColumn<Stock,Double> colCost;

    public TextField txtBatchNo;
    public TextField txtSupId;
    public TextField txtShipmentDate;
    public TextField txtTime;
    public TextField txtStockCost;
    public TextField txtMedicineCode;
    public TextField txtExpiryDate;
    public TextField txtUnitPrice;
    public TextField txtQuantity;
    public TextField txtPrice;

    public JFXTextField stockSearchBar;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;

    LinkedHashMap<TextField, Pattern> validationList1=new LinkedHashMap<>();
    LinkedHashMap<TextField, Pattern> validationList2=new LinkedHashMap<>();

    Pattern batchPattern=Pattern.compile("^(B-)[0-9]{5}$");
    Pattern idPattern=Pattern.compile("^(S-)[0-9]{3}$");
    Pattern datePattern=Pattern.compile("^[0-9]{4}([-][0-9]{2}){2}$");
    Pattern timePattern=Pattern.compile("^[0-9]{2}([:][0-9]{2}){2}[ ](AM|PM)$");
    Pattern pricePattern=Pattern.compile("^[1-9][0-9]{0,6}([.][0-9]{2})?$");
    Pattern codePattern=Pattern.compile("^(M-)[0-9]{5}$");
    Pattern quantityPattern=Pattern.compile("^[1-9][0-9]*$");

    ObservableList<Stock> obListStock= FXCollections.observableArrayList();
    ObservableList<StockDetail> obListDetail= FXCollections.observableArrayList();
    StockDetail lastDetail;

    public void initialize(){
        storeValidations();
        viewAllStocks();

        colBatchNo1.setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colShipmentDate.setCellValueFactory(new PropertyValueFactory<>("shipmentDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

        colBatchNo2.setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        colMedicineCode.setCellValueFactory(new PropertyValueFactory<>("medicineCode"));
        colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        tblStockList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            clearAllFields();
            viewAllDetails(newValue);
            setStockData(newValue);
        });

        tblDetailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setStockDetailData(newValue);
            lastDetail=newValue;
        });

        listenFieldChange(validationList1);
        listenFieldChange(validationList2);

        listenStockChange(txtShipmentDate);
        listenStockChange(txtTime);

        listenDetailChange(txtMedicineCode);
        listenDetailChange(txtQuantity);

    }

    private void listenStockChange(TextField field) {
        field.textProperty().addListener((observable,oldValue,newValue)->{
            if(txtBatchNo.getParent().getStyle().equals("-fx-border-color: #028f02") && txtShipmentDate.getParent().getStyle().equals("-fx-border-color: #028f02") && txtTime.getParent().getStyle().equals("-fx-border-color: #028f02")){
                ArrayList<StockDetail> detailList=new StockManager().getAllStockDetails(txtBatchNo.getText());
                double cost=0;
                for(StockDetail sd: detailList){
                    int qty=sd.getQuantity();
                    Medicine m=new MedicineManager().getMedicine(sd.getMedicineCode());
                    PriceDetail pd=new MedicineManager().getPriceDetail(m,txtShipmentDate.getText(),txtTime.getText());
                    cost+=qty*pd.getUnitPrice();
                }
                txtStockCost.setText(String.format("%.2f",cost));
                if(txtMedicineCode.getParent().getStyle().equals("-fx-border-color: #028f02")){
                    Medicine medicine=new MedicineManager().getMedicine(txtMedicineCode.getText());
                    if(medicine != null){
                        PriceDetail pd=new MedicineManager().getPriceDetail(medicine,txtShipmentDate.getText(),txtTime.getText());
                        double unitPrice=pd.getUnitPrice();
                        txtUnitPrice.setText(String.format("%.2f",unitPrice));
                        String expiryDate=DateTimeUtil.dateAheadGivenDate(txtShipmentDate.getText(),medicine.getPreservedTime());
                        txtExpiryDate.setText(expiryDate);
                        if(txtQuantity.getParent().getStyle().equals("-fx-border-color: #028f02")){
                            setPriceFields(txtBatchNo.getText(),unitPrice);
                        }
                    }
                }
            }
        });
    }

    private void listenDetailChange(TextField field) {
        field.textProperty().addListener((observable,oldValue,newValue)->{
            if(txtBatchNo.getParent().getStyle().equals("-fx-border-color: #028f02") && lastDetail!=null){
                if(txtMedicineCode.getParent().getStyle().equals("-fx-border-color: #028f02")){
                    Medicine medicine=new MedicineManager().getMedicine(txtMedicineCode.getText());
                    if(medicine == null) {
                        txtMedicineCode.getParent().setStyle("-fx-border-color: #DB0F0F");
                        AnchorPane parent=(AnchorPane) txtMedicineCode.getParent();
                        Label tag=(Label)parent.getChildren().get(2);
                        tag.setStyle("-fx-text-fill: #DB0F0F");
                    }else{
                        boolean isExists=false;
                        ArrayList<StockDetail> detailList=new StockManager().getAllStockDetails(txtBatchNo.getText());
                        for(StockDetail sd: detailList){
                            if (!lastDetail.getMedicineCode().equals(medicine.getCode()) && sd.getMedicineCode().equals(medicine.getCode())) {
                                isExists = true;
                                break;
                            }
                        }
                        if(isExists){
                            if(!lastDetail.getMedicineCode().equals(medicine.getCode())){
                                txtMedicineCode.getParent().setStyle("-fx-border-color: #DB0F0F");
                                AnchorPane parent=(AnchorPane) txtMedicineCode.getParent();
                                Label tag=(Label)parent.getChildren().get(2);
                                tag.setStyle("-fx-text-fill: #DB0F0F");
                                new Alert(Alert.AlertType.WARNING, "Medicine already exists in the list...").show();
                                //txtMedicineCode.setText(lastDetail.getMedicineCode());
                            }
                        }else{
                            if(txtShipmentDate.getParent().getStyle().equals("-fx-border-color: #028f02")){
                                String expiryDate=DateTimeUtil.dateAheadGivenDate(txtShipmentDate.getText(),medicine.getPreservedTime());
                                txtExpiryDate.setText(expiryDate);
                                if(txtTime.getParent().getStyle().equals("-fx-border-color: #028f02")){
                                    PriceDetail pd=new MedicineManager().getPriceDetail(medicine,txtShipmentDate.getText(),txtTime.getText());
                                    double unitPrice=pd.getUnitPrice();
                                    txtUnitPrice.setText(String.format("%.2f",unitPrice));
                                    if(txtQuantity.getParent().getStyle().equals("-fx-border-color: #028f02")){
                                        setPriceFields(txtBatchNo.getText(),unitPrice);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void setPriceFields(String batchNo, double unitPrice) {
        ArrayList<StockDetail> detailList=new StockManager().getAllStockDetails(batchNo);
        int qty=Integer.parseInt(txtQuantity.getText());
        double price=qty*unitPrice;
        txtPrice.setText(String.format("%.2f",price));
        double cost=0;
        for(StockDetail sd: detailList){
            int sdQty=sd.getQuantity();
            Medicine m=new MedicineManager().getMedicine(sd.getMedicineCode());
            PriceDetail sdPD=new MedicineManager().getPriceDetail(m,txtShipmentDate.getText(),txtTime.getText());
            cost+=sdQty*sdPD.getUnitPrice();
        }
        int lastQty=lastDetail.getQuantity();
        Medicine lastMed=new MedicineManager().getMedicine(lastDetail.getMedicineCode());
        PriceDetail lastPD=new MedicineManager().getPriceDetail(lastMed,txtShipmentDate.getText(),txtTime.getText());
        double lastPrice=lastQty*lastPD.getUnitPrice();
        double finalCost=cost-lastPrice+price;
        txtStockCost.setText(String.format("%.2f",finalCost));
    }

    private boolean isAnyFieldEmpty(LinkedHashMap<TextField, Pattern> list) {
        for(TextField key: list.keySet()){
            if(!key.getParent().getStyle().equals("-fx-border-color: #028f02") && !key.getParent().getStyle().equals("-fx-border-color: #DB0F0F"))
            {
                return true;
            }
        }
        return false;
    }

    private void clearAllFields(){
        for(TextField key: validationList1.keySet()){
            key.clear();
        }
        for(TextField key: validationList2.keySet()){
            key.clear();
        }
    }

    private void setStockDetailData(StockDetail stockDetail) {
        if(stockDetail!=null){
            txtMedicineCode.setText(stockDetail.getMedicineCode());
            /*txtExpiryDate.setText(stockDetail.getExpiryDate());*/
            Medicine medicine=new MedicineManager().getMedicine(stockDetail.getMedicineCode());
            String expiryDate=DateTimeUtil.dateAheadGivenDate(txtShipmentDate.getText(),medicine.getPreservedTime());
            txtExpiryDate.setText(expiryDate);
            txtUnitPrice.setText(String.format("%.2f", stockDetail.getUnitPrice()));
            int qty=stockDetail.getQuantity();
            txtQuantity.setText(Integer.toString(qty));
            txtPrice.setText(String.format("%.2f", stockDetail.getPrice()));
            if(txtShipmentDate.getParent().getStyle().equals("-fx-border-color: #028f02") && txtTime.getParent().getStyle().equals("-fx-border-color: #028f02")){
                PriceDetail pd=new MedicineManager().getPriceDetail(medicine,txtShipmentDate.getText(),txtTime.getText());
                double unitPrice=pd.getUnitPrice();
                txtUnitPrice.setText(String.format("%.2f",unitPrice));
                double price=qty*unitPrice;
                txtPrice.setText(String.format("%.2f", price));
            }
        }
    }

    private void setStockData(Stock stock) {
        if(stock!=null){
            txtBatchNo.setText(stock.getBatchNo());
            txtSupId.setText(stock.getSupplierId());
            txtShipmentDate.setText(stock.getShipmentDate());
            txtTime.setText(stock.getTime());
            txtStockCost.setText(String.format("%.2f", stock.getCost()));
        }
    }

    private void clearAllDetails() {
        obListDetail.clear();
        obListDetail.addAll(new ArrayList<>());
        tblDetailList.setItems(obListDetail);
    }

    private void viewAllDetails(Stock stock) {
        if(stock!=null && obListDetail!=null){
            obListDetail.clear();
            ArrayList<StockDetail> detailList=stock.getDetailList();
            obListDetail.addAll(detailList);
            tblDetailList.setItems(obListDetail);
        }
    }

    private void viewAllStocks(){
        obListStock.clear();
        ArrayList<Stock> stocks=new StockManager().getAllStocks();
        obListStock.addAll(stocks);
        tblStockList.setItems(obListStock);
    }

    private void storeValidations() {
        validationList1.put(txtBatchNo,batchPattern);
        validationList1.put(txtSupId,idPattern);
        validationList1.put(txtShipmentDate,datePattern);
        validationList1.put(txtTime,timePattern);
        validationList1.put(txtStockCost,pricePattern);
        validationList2.put(txtMedicineCode,codePattern);
        validationList2.put(txtExpiryDate,datePattern);
        validationList2.put(txtUnitPrice,pricePattern);
        validationList2.put(txtQuantity,quantityPattern);
        validationList2.put(txtPrice,pricePattern);
    }

    private void listenFieldChange(LinkedHashMap<TextField,Pattern> list) {
        for(TextField key: list.keySet()) {
            key.textProperty().addListener((observable,oldValue,newValue)->{
                ValidationUtil.validate(key,list);
            });
        }
    }

    private boolean isAllFieldsEmpty(LinkedHashMap<TextField, Pattern> list) {
        for(TextField key: list.keySet()){
            if(key.getParent().getStyle().equals("-fx-border-color: #028f02") || key.getParent().getStyle().equals("-fx-border-color: #DB0F0F")){
                return false;
            }
        }
        return true;
    }

    private boolean isFormNotFilled(LinkedHashMap<TextField,Pattern> validationList) {
        for(TextField key: validationList.keySet()){
            if(!key.getParent().getStyle().equals("-fx-border-color: #028f02")){
                return true;
            }
        }
        return false;
    }

    public void updateOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList1)){
            new Alert(Alert.AlertType.WARNING, "Not selected any Stock..").show();
        }else{
            ArrayList<StockDetail> detailList=new StockManager().getAllStockDetails(txtBatchNo.getText());
            for(StockDetail detail: detailList) {
                Medicine medicine=new MedicineManager().getMedicine(detail.getMedicineCode());
                String period=medicine.getPreservedTime();
                String date=txtShipmentDate.getText();
                String newDate=DateTimeUtil.dateAheadGivenDate(date,period);
                detail.setExpiryDate(newDate);

                int qty=detail.getQuantity();
                PriceDetail pd=new MedicineManager().getPriceDetail(medicine,txtShipmentDate.getText(),txtTime.getText());
                double unitPrice=pd.getUnitPrice();
                detail.setUnitPrice(unitPrice);
                detail.setPrice(qty*unitPrice);
            }
            Stock stock=new Stock(
                    txtBatchNo.getText(),
                    txtSupId.getText(),
                    txtShipmentDate.getText(),
                    txtTime.getText(),
                    Double.parseDouble(txtStockCost.getText()),
                    detailList
            );
            if(isAllFieldsEmpty(validationList2)){
                if(new StockManager().updateStock(stock)){
                    new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                    viewAllStocks();
                    clearAllFields();
                    clearAllDetails();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try again...").show();
                }
            }else if(isFormNotFilled(validationList2)){
                new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
            }else{
                StockDetail newDetail=new StockDetail(
                        txtBatchNo.getText(),
                        txtMedicineCode.getText(),
                        txtExpiryDate.getText(),
                        Double.parseDouble(txtUnitPrice.getText()),
                        Integer.parseInt(txtQuantity.getText()),
                        Double.parseDouble(txtPrice.getText())
                );
                StockDetail oldDetail=null;
                for(StockDetail sd: detailList){
                    if(lastDetail.getMedicineCode().equals(sd.getMedicineCode())){
                        oldDetail=sd;
                    }
                }
                if(new StockManager().updateStockDetail(stock,oldDetail,newDetail)){
                    new Alert(Alert.AlertType.CONFIRMATION,"Updated...").show();
                    viewAllStocks();
                    clearAllFields();
                    clearAllDetails();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try again...").show();
                }
            }
        }
    }

    public void deleteOnAction(ActionEvent actionEvent) {
        if(isFormNotFilled(validationList1)){
            new Alert(Alert.AlertType.WARNING, "Not selected any Stock..").show();
        }else{
            Stock stock=new StockManager().getStock(txtBatchNo.getText());
            if(isAllFieldsEmpty(validationList2)){
                if(new StockManager().deleteStock(stock)){
                    new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                    viewAllStocks();
                    clearAllFields();
                    clearAllDetails();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try again...").show();
                }
            }else if(isFormNotFilled(validationList2)){
                new Alert(Alert.AlertType.WARNING, "Fields are not filled properly...").show();
            }else{
                boolean isExists=false;
                ArrayList<StockDetail> detailList=new StockManager().getAllStockDetails(stock.getBatchNo());
                for(StockDetail sd: detailList){
                    if(sd.getMedicineCode().equals(txtMedicineCode.getText())){
                        isExists=true;
                        break;
                    }
                }
                if(!isExists){
                    new Alert(Alert.AlertType.WARNING, "Medicine doesn't exist in the list...").show();
                }else{
                    StockDetail detail=new StockManager().getStockDetail(stock.getBatchNo(),txtMedicineCode.getText());
                    if(new StockManager().deleteStockDetail(detail)){
                        new Alert(Alert.AlertType.CONFIRMATION,"Deleted...").show();
                        viewAllStocks();
                        clearAllFields();
                        clearAllDetails();
                    }else{
                        new Alert(Alert.AlertType.WARNING, "Fucked Up").show();
                    }
                }
            }
        }
    }
}
