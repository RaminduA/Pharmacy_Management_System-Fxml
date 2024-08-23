package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;

import java.io.IOException;

public class PharmacistDashboardController extends DashboardController{
    public JFXButton btnManageSuppliers;
    public JFXButton btnImportMedicine;
    public JFXButton btnManageStocks;

    public void initialize(){
        loadDateAndTime();
        setUserId();
        launchEffects();
        setButtonList();
    }

    public void manageSuppliersOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageSuppliers);
        loadUI("ManageSuppliersView");
    }
    public void importMedicineOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnImportMedicine);
        loadUI("ImportMedicineView");
    }
    public void manageStocksOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageStocks);
        loadUI("ManageStocksView");
    }

    @Override
    public void setButtonList() {
        buttonList.add(btnManageSuppliers);
        buttonList.add(btnImportMedicine);
        buttonList.add(btnManageStocks);
    }
}
