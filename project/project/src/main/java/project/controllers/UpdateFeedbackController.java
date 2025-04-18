package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.models.FeedBack;
import project.service.FeedBackService;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;

public class UpdateFeedbackController {

    @FXML
    private TextField idTextField;
    @FXML
    private TextField bonPlanIdTextField;
    @FXML
    private TextField utilisateurIdTextField;
    @FXML
    private TextArea commentaireTextArea;
    @FXML
    private Slider ratingSlider;
    @FXML
    private Label ratingValueLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private FeedBack feedbackToUpdate;
    private FeedBackService feedBackService = new FeedBackService();

    // Add a reference to the ShowBonPlanController
    private ShowBonPlanController showBonPlanController;

    // Setter for the ShowBonPlanController
    public void setShowBonPlanController(ShowBonPlanController controller) {
        this.showBonPlanController = controller;
    }

    @FXML
    public void initialize() {
        // Bind the label to the slider's value
        ratingValueLabel.textProperty().bind(
                Bindings.createStringBinding(() -> String.valueOf((int) ratingSlider.getValue()), ratingSlider.valueProperty())
        );
    }

    public void setFeedbackToUpdate(FeedBack feedback) {
        this.feedbackToUpdate = feedback;
    }

    public void populateFields() {
        if (feedbackToUpdate != null) {
            idTextField.setText(String.valueOf(feedbackToUpdate.getId()));
            bonPlanIdTextField.setText(String.valueOf(feedbackToUpdate.getBonPlanId()));
            commentaireTextArea.setText(feedbackToUpdate.getCommentaire());
            ratingSlider.setValue(feedbackToUpdate.getRating());
        }
    }

    @FXML
    void handleUpdateFeedback(ActionEvent event) {
        if (feedbackToUpdate != null) {
            feedbackToUpdate.setCommentaire(commentaireTextArea.getText());
            feedbackToUpdate.setRating((int) ratingSlider.getValue());
            feedBackService.update(feedbackToUpdate);

            // Close the window after update
            closeUpdateWindow(event);
            // Notify the ShowBonPlanController to refresh the table
            if (showBonPlanController != null) {
                showBonPlanController.loadFeedbacksForBonPlan();
            }
        }
    }

    @FXML
    void handleCancelUpdate(ActionEvent event) {
        // Close the window without updating
        closeUpdateWindow(event);
    }

    private void closeUpdateWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}

