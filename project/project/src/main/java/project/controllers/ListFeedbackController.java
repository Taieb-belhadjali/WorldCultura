package project.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.models.FeedBack;
import project.service.FeedBackService;

import java.util.List;

public class ListFeedbackController { // Changed class name to ListFeedbackController

    @FXML
    private TableView<FeedBack> feedbackTableView;

    @FXML
    private TableColumn<FeedBack, Integer> idColumn;

    @FXML
    private TableColumn<FeedBack, Integer> bonPlanIdColumn;

    @FXML
    private TableColumn<FeedBack, Integer> utilisateurIdColumn;

    @FXML
    private TableColumn<FeedBack, String> commentaireColumn;

    @FXML
    private TableColumn<FeedBack, Integer> ratingColumn;


    private FeedBackService feedbackService = new FeedBackService();
    private ObservableList<FeedBack> feedbackList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableColumns();
        loadFeedbacks();
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bonPlanIdColumn.setCellValueFactory(new PropertyValueFactory<>("bonPlanId"));
        utilisateurIdColumn.setCellValueFactory(new PropertyValueFactory<>("utilisateurId"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadFeedbacks() {
        List<FeedBack> feedbacks = feedbackService.getAll();
        feedbackList.addAll(feedbacks);
        feedbackTableView.setItems(feedbackList);
    }
}
