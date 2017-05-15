package ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.ConfirmationMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import jdk.internal.org.objectweb.asm.TypeReference;
import library.Building;
import library.Calamity;
import library.User;
import library.UserType;
import requests.LoginRequest;
import requests.UserRequest;
import ui.Main;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private Main main;

    @FXML
    public Button loginBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public TextField user;
    @FXML
    public TextField password;

    public LoginController(Main main) {
        this.main = main;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginBtn.setOnAction(this::handleLoginAction);
        cancelBtn.setOnAction(this::handleExitAction);
    }

    private void handleExitAction(ActionEvent actionEvent) {
        System.exit(1);
    }

    public void handleLoginAction(ActionEvent event) {
        LoginRequest loginRequest = new LoginRequest();
        ConfirmationMessage result = loginRequest.login(user.getText(), password.getText());
        System.out.println(result.getReturnObject());

        if (result.getStatus().equals(ConfirmationMessage.StatusType.SUCCES)) {

            String token = (String) result.getReturnObject();
            UserRequest userRequest = new UserRequest();
            result = userRequest.getUser(token);

            Object values = result.getReturnObject();

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.convertValue(values, User.class);

            try {
                main.loadDashBoard(user);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (result.getStatus().equals(ConfirmationMessage.StatusType.ERROR)) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Something went wrong");
            a.setResizable(true);
            String content = "The username and password are incorrect! Please try again.";
            a.setContentText(content);
            a.showAndWait();
        }
    }
}