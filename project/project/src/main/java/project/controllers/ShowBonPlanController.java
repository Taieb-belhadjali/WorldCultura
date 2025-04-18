package project.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.models.BonPlan;
import project.models.FeedBack;
import project.service.BonPlanService;
import project.service.FeedBackService;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import javafx.beans.binding.Bindings;
import java.io.IOException;
import java.net.URL;

public class ShowBonPlanController {

    @FXML
    private Label idLabelLabel;
    @FXML
    private TextField idTextField;
    @FXML
    private Label titreLabelLabel;
    @FXML
    private TextField titreTextField;
    @FXML
    private Label descriptionLabelLabel;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Label lieuLabelLabel;
    @FXML
    private TextField lieuTextField;
    @FXML
    private Label dateCreationLabelLabel;
    @FXML
    private TextField dateCreationTextField;
    @FXML
    private Label dateExpirationLabelLabel;
    @FXML
    private TextField dateExpirationTextField;
    @FXML
    private Label categorieLabelLabel;
    @FXML
    private TextField categorieTextField;

    @FXML
    private TableView<FeedBack> feedbackTable;
    @FXML
    private TableColumn<FeedBack, Integer> feedbackIdColumn;
    @FXML
    private TableColumn<FeedBack, String> feedbackCommentaireColumn;
    @FXML
    private TableColumn<FeedBack, Integer> feedbackRatingColumn;
    @FXML
    private TableColumn<FeedBack, LocalDateTime> feedbackDateCreationColumn;

    @FXML
    private TextArea addFeedbackCommentaireTextArea;
    @FXML
    private Slider addFeedbackRatingSlider;
    @FXML
    private Label addFeedbackRatingValue;
    @FXML
    private Button addFeedbackButton;
    @FXML
    private Button goToUpdateFeedbackButton;

    private int bonPlanId;
    private BonPlanService bonPlanService = new BonPlanService();
    private FeedBackService feedBackService = new FeedBackService();
    private ObservableList<FeedBack> feedbackList = FXCollections.observableArrayList();
    private boolean isInitialized = false;

    public void setBonPlanId(int id) {
        this.bonPlanId = id;
        if (isInitialized) {
            loadIndividualBonPlanDetails();
            loadFeedbacksForBonPlan();
        }
    }

    @FXML
    public void initialize() {
        configureFeedbackTableView();
        initializeFeedbackInputFields();
        isInitialized = true;
        if (bonPlanId != 0) {
            loadIndividualBonPlanDetails();
            loadFeedbacksForBonPlan();
        }
    }

    private void loadIndividualBonPlanDetails() {
        if (bonPlanId != 0) {
            BonPlan bonPlan = bonPlanService.getById(bonPlanId);
            if (bonPlan != null) {
                idTextField.setText(String.valueOf(bonPlan.getId()));
                titreTextField.setText(bonPlan.getTitre());
                descriptionTextField.setText(bonPlan.getDescription());
                lieuTextField.setText(bonPlan.getLieu());
                dateCreationTextField.setText(bonPlan.getDateCreation() != null ? bonPlan.getDateCreation().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                dateExpirationTextField.setText(bonPlan.getDateExpiration() != null ? bonPlan.getDateExpiration().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
                categorieTextField.setText(bonPlan.getCategorie());
            } else {
                clearBonPlanDetails();
                showErrorAlert("Error", "Bon Plan with ID " + bonPlanId + " not found.");
            }
        } else {
            clearBonPlanDetails();
        }
    }

    private void clearBonPlanDetails() {
        idTextField.clear();
        titreTextField.clear();
        descriptionTextField.clear();
        lieuTextField.clear();
        dateCreationTextField.clear();
        dateExpirationTextField.clear();
        categorieTextField.clear();
    }

    private void configureFeedbackTableView() {
        feedbackIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        feedbackCommentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        feedbackRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        feedbackDateCreationColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        feedbackTable.setItems(feedbackList);
        feedbackTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            goToUpdateFeedbackButton.setDisable(newSelection == null);
        });
    }

    public void loadFeedbacksForBonPlan() {
        if (bonPlanId != 0) {
            feedbackList.clear();
            List<FeedBack> feedbacks = feedBackService.getFeedbacksByBonPlanId(bonPlanId);
            if (feedbacks != null) {
                feedbackList.addAll(feedbacks);
            }
        } else {
            feedbackList.clear();
        }
    }

    private void initializeFeedbackInputFields() {
        addFeedbackCommentaireTextArea.clear();
        addFeedbackRatingSlider.setValue(3);
        addFeedbackRatingValue.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf((int) addFeedbackRatingSlider.getValue()), addFeedbackRatingSlider.valueProperty()));
    }

    @FXML
    public void handleAddFeedback(ActionEvent event) {
        String commentaire = addFeedbackCommentaireTextArea.getText();
        int rating = (int) addFeedbackRatingSlider.getValue();
        if (isFeedbackCommentaireValid(commentaire)) {
            FeedBack newFeedback = new FeedBack();
            newFeedback.setBonPlanId(bonPlanId);
            newFeedback.setCommentaire(commentaire);
            newFeedback.setRating(rating);
            newFeedback.setDateCreation(LocalDateTime.now());
            feedBackService.add(newFeedback);
            loadFeedbacksForBonPlan();
            initializeFeedbackInputFields();
        } else {
            showErrorAlert("Error", "Please enter a valid comment for the feedback.");
        }
    }

    @FXML
    public void goToUpdateFeedbackPage(ActionEvent event) {
        FeedBack selectedFeedback = feedbackTable.getSelectionModel().getSelectedItem();
        if (selectedFeedback != null) {
            try {
                URL fxmlURL = getClass().getResource("/UpdateFeedback.fxml");
                if (fxmlURL == null) {
                    System.err.println("FXML file not found: /UpdateFeedback.fxml");
                    return;
                }
                FXMLLoader loader = new FXMLLoader(fxmlURL);
                Parent root = loader.load();
                UpdateFeedbackController updateFeedbackController = loader.getController();
                updateFeedbackController.setFeedbackToUpdate(selectedFeedback);
                updateFeedbackController.populateFields();
                updateFeedbackController.setShowBonPlanController(this); // Pass the instance here
                Stage stage = new Stage();
                stage.setTitle("Update Feedback");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.err.println("Error loading UpdateFeedback.fxml: " + e.getMessage());
                e.printStackTrace();
                showErrorAlert("Error", "Could not load the update feedback page.");
            }
        } else {
            showErrorAlert("Warning", "Please select a feedback item to update.");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isFeedbackCommentaireValid(String commentaire) {
        return commentaire != null && !commentaire.trim().isEmpty();
    }
}

