package controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Pulse;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import manager.LoginManager;
import model.UserDetail;
import util.DateTimeUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class DashboardController {
    public AnchorPane dashboardContext;
    public AnchorPane performanceContext;
    public AnchorPane sidePanel;

    public JFXButton logoutButton;
    public Label lblUserId;
    public Label lblDate;
    public Label lblTime;

    public ArrayList<JFXButton> buttonList=new ArrayList<>();
    public static UserDetail user;

    public void initialize(){

    }

    public void launchEffects(){
        FadeIn fade=new FadeIn(dashboardContext);
        fade.setSpeed(0.8);
        fade.play();
        SlideInLeft slide=new SlideInLeft(sidePanel);
        slide.setSpeed(0.5);
        slide.play();
    }
    private void animatePerformance(){
        FadeIn fade=new FadeIn(performanceContext);
        fade.setSpeed(0.6);
        fade.play();
    }

    public void backToLoginForm(ActionEvent actionEvent) throws IOException {
        LoginManager.logout(user.getUserId());
        dashboardContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml")));
    }
    public void loadUI(String view) throws IOException {
        performanceContext.getChildren().add(FXMLLoader.load(getClass().getResource("../view/"+view+".fxml")));
        animatePerformance();
    }

    public void loadDateAndTime() {
        lblDate.setText(DateTimeUtil.currentDate(2));
        Timeline time=new Timeline(new KeyFrame(Duration.ZERO, e -> {
                LocalTime currentTime=LocalTime.now();
                lblTime.setText(currentTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a")));//ISO_LOCAL_TIME.substring(0,8)
            }),
            new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    public void setButtonList() {

    }

    public void selectButton(JFXButton button) {
        for(JFXButton btn:buttonList) {
            btn.setStyle("-fx-background-color:  #4a98c8");
        }
        button.setStyle("-fx-background-color: #006da7");
        Pulse pulse=new Pulse(button);
        pulse.setSpeed(1.5);
        pulse.play();
    }

    public void setUserId() {
        if(user!=null) lblUserId.setText("user : "+user.getUserId());
    }
}
