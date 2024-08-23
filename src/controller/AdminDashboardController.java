package controller;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import manager.MedicineManager;
import manager.StockManager;
import manager.SupplierManager;
import model.Stock;
import model.tableModel.IncomeTM;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminDashboardController extends DashboardController{
    public JFXButton btnManageEmployees;
    public JFXButton btnViewReports;
    public JFXButton btnViewLoginDetails;
    public JFXButton btnManageMedicine;
    public PieChart pieChart;
    public Label lblAnnualIncome;
    public Label lblYear;
    public Label lblMonthlyIncome;
    public Label lblMonth;
    public Label lblDailyIncome;
    public Label lblDay;

    public void initialize(){
        loadDateAndTime();
        setUserId();
        launchEffects();
        setButtonList();
        loadPieChart();
        setIncomeLabels();
    }

    private void setIncomeLabels() {
        double annualIncome=0;
        double monthlyIncome=0;
        double dailyIncome=0;
        ArrayList<IncomeTM> incomeList=new MedicineManager().getAllIncomes();
        for(IncomeTM tm: incomeList){
            String[] dateArray=tm.getDate().split("-");
            if(new SimpleDateFormat("yyyy").format(new Date()).equals(dateArray[0])){
                annualIncome+=tm.getIncome();
                if(new SimpleDateFormat("MM").format(new Date()).equals(dateArray[1])){
                    monthlyIncome+=tm.getIncome();
                    if(new SimpleDateFormat("dd").format(new Date()).equals(dateArray[2])){
                        dailyIncome+=tm.getIncome();
                    }
                }
            }
        }
        lblAnnualIncome.setText("Rs. "+String.format("%.2f",annualIncome));
        lblMonthlyIncome.setText("Rs. "+String.format("%.2f",monthlyIncome));
        lblDailyIncome.setText("Rs. "+String.format("%.2f",dailyIncome));
        lblYear.setText(new SimpleDateFormat("yyyy").format(new Date()));
        lblMonth.setText(new SimpleDateFormat("MMMM yyyy").format(new Date()));
        lblDay.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date()));
    }

    private void loadPieChart() {
        ObservableList<PieChart.Data> list=FXCollections.observableArrayList();
        ArrayList<Stock> stockList=new StockManager().getAllStocks();
        ArrayList<String> supplierList=new SupplierManager().getAllSupplierIds();
        for(String supplier:supplierList) {
            double total=0;
            for(Stock stock:stockList) {
                if(stock.getSupplierId().equals(supplier)){
                    total+=stock.getCost();
                }
            }
            list.add(new PieChart.Data(supplier,total));
        }

        list.forEach(data -> data.nameProperty().bind(
                Bindings.concat(data.getName()," : Rs.",data.pieValueProperty())
        ));

        pieChart.getData().addAll(list);
        //performanceContext.getChildren().add(pieChart);
    }

    public void manageEmployeesOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageEmployees);
        loadUI("ManageEmployeesView");
    }
    public void viewReportsOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnViewReports);
        loadUI("ViewSystemReportsView");
    }
    public void viewLoginDetailsOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnViewLoginDetails);
        loadUI("ViewLoginDetailsView");
    }
    public void manageMedicineOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageMedicine);
        loadUI("ManageMedicineView");
    }

    @Override
    public void setButtonList() {
        buttonList.add(btnManageEmployees);
        buttonList.add(btnViewReports);
        buttonList.add(btnViewLoginDetails);
        buttonList.add(btnManageMedicine);
    }
}
