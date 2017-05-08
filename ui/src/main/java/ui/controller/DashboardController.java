package ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import library.User;
import ui.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by guillaimejanssen on 30/04/2017.
 */
public class DashboardController implements Initializable {

    private Main main;
    private User user;

    @FXML
    public Button logoutBtn;
    @FXML
    public VBox calamityBtn;
    @FXML
    public VBox sendRescuerBtn;

    public DashboardController(Main main, User user) {
        this.main = main;
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutBtn.setOnAction(this::handleLogoutAction);
        calamityBtn.setOnMouseClicked(this::handleCalamityBtnAction);
        sendRescuerBtn.setOnMouseClicked(this::handleInformRescuerBtnAction);
    }

    private void handleCalamityBtnAction(MouseEvent mouseEvent) {
        Stage stage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/CalamityList.fxml"));
        CalamityListController calamityListController = new CalamityListController(user);
        fxmlLoader.setController(calamityListController);

        stage.setResizable(false);
        stage.setTitle("Securoserve");
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();

        // Center it
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void handleInformRescuerBtnAction(MouseEvent mouseEvent){
        try {
            main.loadInformRescuer(this.user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogoutAction(ActionEvent actionEvent) {
        try {
            main.loadLogin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
