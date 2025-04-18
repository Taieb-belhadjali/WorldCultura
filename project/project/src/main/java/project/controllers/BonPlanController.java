package project.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.models.BonPlan;
import project.service.BonPlanService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BonPlanController {

    @FXML
    private TableView<BonPlan> tableBonPlans;

    @FXML
    private TableColumn<BonPlan, Integer> idColumn;
    @FXML
    private TableColumn<BonPlan, String> colTitre;
    @FXML
    private TableColumn<BonPlan, String> colDescription;
    @FXML
    private TableColumn<BonPlan, String> colLieu;
    @FXML
    private TableColumn<BonPlan, LocalDate> colDateExp;
    @FXML
    private TableColumn<BonPlan, LocalDateTime> dateCreationColumn;
    @FXML
    private TableColumn<BonPlan, String> colCategorie;
    @FXML
    private Button consulterButton;

    private BonPlanService bonPlanService = new BonPlanService();
    private ObservableList<BonPlan> bonPlanList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadBonPlans();
        consulterButton.setDisable(true);

        tableBonPlans.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            consulterButton.setDisable(newSelection == null);
        });
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLieu.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colDateExp.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        dateCreationColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
    }

    private void loadBonPlans() {
        bonPlanList.clear();
        List<BonPlan> bonPlans = bonPlanService.getAll();
        if (bonPlans != null) {
            bonPlanList.addAll(bonPlans);
        }
        tableBonPlans.setItems(bonPlanList);
    }

    @FXML
    public void consulterBonPlan(ActionEvent event) {
        BonPlan selectedBonPlan = tableBonPlans.getSelectionModel().getSelectedItem();
        if (selectedBonPlan != null) {
            try {
                //  Get the URL of the FXML file.
                URL fxmlURL = getClass().getResource("/ShowBonPlan.fxml");

                if (fxmlURL == null) {
                    System.err.println("FXML file not found: /ShowBonPlan.fxml");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(fxmlURL);
                Parent root = loader.load();


                ShowBonPlanController showBonPlanController = loader.getController();
                showBonPlanController.setBonPlanId(selectedBonPlan.getId());

                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Bon Plan Details");
                stage.show();

            } catch (IOException e) {
                System.err.println("Error loading FXML: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}

