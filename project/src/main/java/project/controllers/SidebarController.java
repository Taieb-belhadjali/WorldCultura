package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SidebarController {

    private MainViewController mainViewController;

    @FXML
    private VBox volManagementLinks;

    private boolean volManagementVisible = false;

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    private void toggleVolManagement(ActionEvent event) {
        volManagementVisible = !volManagementVisible;
        volManagementLinks.setVisible(volManagementVisible);
    }

    @FXML
    private void loadAirlines(ActionEvent event) {
        if (mainViewController != null) {
            mainViewController.loadView("views/amineviews/ListAirlines.fxml");
        } else {
            System.err.println("MainViewController n'est pas initialisé dans SidebarController.");
        }
    }

    @FXML
    private void loadFlights(ActionEvent event) {
        if (mainViewController != null) {
            mainViewController.loadView("views/amineviews/ListRehla.fxml");
        } else {
            System.err.println("MainViewController n'est pas initialisé dans SidebarController.");
        }
    }

    @FXML
    private void loadReservations(ActionEvent event) {
        if (mainViewController != null) {
            mainViewController.loadView("views/amineviews/list_reservations.fxml");
        } else {
            System.err.println("MainViewController n'est pas initialisé dans SidebarController.");
        }
    }
}