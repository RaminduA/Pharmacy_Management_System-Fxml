package controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;

import java.io.IOException;

public class AssistantDashboardController extends DashboardController{
    public JFXButton btnManageCustomers;
    public JFXButton btnPlaceOrder;
    public JFXButton btnManageOrders;

    public void initialize(){
        loadDateAndTime();
        setUserId();
        launchEffects();
        setButtonList();
    }

    public void manageCustomersOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageCustomers);
        loadUI("ManageCustomersView");
    }
    public void placeOrderOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnPlaceOrder);
        loadUI("PlaceOrderView");
    }
    public void manageOrdersOnAction(ActionEvent actionEvent) throws IOException {
        selectButton(btnManageOrders);
        loadUI("ManageOrdersView");
    }

    @Override
    public void setButtonList() {
        buttonList.add(btnManageCustomers);
        buttonList.add(btnPlaceOrder);
        buttonList.add(btnManageOrders);
    }
}
