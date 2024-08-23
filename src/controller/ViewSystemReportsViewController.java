package controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import manager.MedicineManager;
import model.tableModel.IncomeTM;
import util.JasperUtil;

import java.util.ArrayList;

public class ViewSystemReportsViewController {

    public TableView<IncomeTM> tblIncomeDetailList;
    public TableColumn<IncomeTM,String> colDate;
    public TableColumn<IncomeTM,String> colTime;
    public TableColumn<IncomeTM,Double> colIncome;
    public Label lblTotal;
    public JFXButton btnMedicineDetails;
    public JFXButton btnMedicineStocks;
    public JFXButton btnMedicineOrders;

    ObservableList<IncomeTM> obList= FXCollections.observableArrayList();

    public void initialize(){
        viewAllIncomes();
        calculateTotalIncome();

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colIncome.setCellValueFactory(new PropertyValueFactory<>("income"));
    }

    private void calculateTotalIncome() {
        double total=0;
        for(IncomeTM incomeTM: obList) {
            total+=incomeTM.getIncome();
        }
        lblTotal.setText("Rs. "+String.format("%.2f",total));
    }

    private void viewAllIncomes() {
        ArrayList<IncomeTM> incomeList=new MedicineManager().getAllIncomes();
        obList.addAll(incomeList);
        tblIncomeDetailList.setItems(obList);
    }

    public void viewMedicineDetailsOnAction(ActionEvent actionEvent) {
        /*try{
            JasperDesign design= JRXmlLoader.load(getClass().getResourceAsStream("report/MedicineReport.jrxml"));
            JasperReport compileReport= JasperCompileManager.compileReport(design);
            JasperPrint fillReport= JasperFillManager.fillReport(compileReport, null, new JREmptyDataSource(1));
            JasperViewer.viewReport(fillReport,false);
        }catch(JRException e){
            e.printStackTrace();
        }*/

        new JasperUtil().loadReport("MedicineReport");
    }

    public void viewMedicineStocksOnAction(ActionEvent actionEvent) {
        new JasperUtil().loadReport("StockReport");
    }

    public void viewMedicineOrdersOnAction(ActionEvent actionEvent) {
        new JasperUtil().loadReport("OrderReport");
    }
}
