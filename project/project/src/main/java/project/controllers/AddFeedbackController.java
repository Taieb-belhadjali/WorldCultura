package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import project.models.FeedBack;
import project.service.FeedBackService;

import java.time.LocalDateTime;

public class AddFeedbackController {

    @FXML
    private TextField bonPlanIdTextField;

    @FXML
    private TextArea commentaireTextArea;

    @FXML
    private TextField ratingTextField;

    @FXML
    private Button addButton;

    private FeedBackService feedbackService = new FeedBackService();

    @FXML
    public void initialize() {
        // Initialization logic (if needed)
    }

    @FXML
    private void addFeedback(ActionEvent event) {
        try {
            // Parse the input from the text fields
            int bonPlanId = Integer.parseInt(bonPlanIdTextField.getText());
            String commentaire = commentaireTextArea.getText();
            int rating = Integer.parseInt(ratingTextField.getText());
            LocalDateTime dateCreation = LocalDateTime.now(); // Set the feedback creation date

            // Create a new FeedBack object
            FeedBack newFeedback = new FeedBack();
            newFeedback.setBonPlanId(bonPlanId);
            newFeedback.setCommentaire(commentaire);
            newFeedback.setRating(rating);
            newFeedback.setDateCreation(dateCreation);

            // Add the feedback to the database
            feedbackService.add(newFeedback);

            // Show a success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Feedback added successfully!");
            alert.showAndWait();

            // Clear the input fields after successful addition
            bonPlanIdTextField.clear();
            commentaireTextArea.clear();
            ratingTextField.clear();

        } catch (NumberFormatException e) {
            // Handle invalid input (e.g., non-integer values)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid input. Please enter valid numbers for Bon Plan ID,  and Rating.");
            alert.showAndWait();
        } catch (Exception e) {
            // Handle other exceptions (e.g., database error)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while adding the feedback: " + e.getMessage());
            alert.showAndWait();
        }
    }
}

