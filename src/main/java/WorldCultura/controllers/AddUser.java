package WorldCultura.controllers;

import WorldCultura.interfaces.IService;
import WorldCultura.models.user;
import WorldCultura.services.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class AddUser {

    @FXML
    private TextField TFNom;

    @FXML
    private TextField TFPrenom;

    @FXML
    private TextField TFEmail;

    @FXML
    private TextField TFPassword;

    IService<user> us = new userService();
    @FXML
    private Label lbUser;
    @FXML
    public void add(ActionEvent event) {
        user U = new user();
        U.setNom(TFNom.getText());
        U.setPrenom(TFPrenom.getText());
        U.setEmail(TFEmail.getText());
        U.setPassword(TFPassword.getText());

        us.add(U);
    }

}
