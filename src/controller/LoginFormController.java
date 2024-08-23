package controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import manager.LoginManager;
import model.UserDetail;

import java.io.IOException;

public class LoginFormController {
    public AnchorPane loginView;
    public AnchorPane loginContext;
    public JFXTextField txtUsername;
    public JFXPasswordField txtPassword;
    public JFXButton loginButton;

    public void initialize(){
        FadeIn fade=new FadeIn(loginView);
        fade.setSpeed(0.8);
        fade.play();
        Pulse pulse=new Pulse(loginContext);
        pulse.setSpeed(0.4);
        pulse.play();
    }
    public void loadDashboardsOnAction(ActionEvent actionEvent) throws IOException{
        String username=txtUsername.getText();
        String password=txtPassword.getText();
        Object respond=new LoginManager().verifyLogin(username,password);
        if(respond instanceof UserDetail){
            UserDetail ud=(UserDetail)respond;
            DashboardController.user=ud;
            LoginManager.login(ud.getUserId());
            loginView.getChildren().add(FXMLLoader.load(getClass().getResource("../view/"+ud.getRole()+"Dashboard.fxml")));
        }else if(respond instanceof Boolean){
            new Alert(Alert.AlertType.WARNING,"Username & Password you entered does not match. Please try again...").show();
        }


        /*String pw=txtPassword.getText();
        switch(pw){
            case "user1":
                loginView.getChildren().add(FXMLLoader.load(getClass().getResource("../view/AdminDashboard.fxml")));
                break;
            case "user2":
                loginView.getChildren().add(FXMLLoader.load(getClass().getResource("../view/PharmacistDashboard.fxml")));
                break;
            case "user3":
                loginView.getChildren().add(FXMLLoader.load(getClass().getResource("../view/AssistantDashboard.fxml")));
                break;
        }*/
    }
}
