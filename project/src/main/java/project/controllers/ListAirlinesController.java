package project.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty; // Import for ImageView property
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane; // Import manquant !
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import project.models.compagnie_aerienne;
import project.service.compagnie_areienneservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Controller class for the List Airlines view.
 * This class handles the display of airline data in a table, searching, adding,
 * deleting, updating, and viewing details of airlines.
 */
public class ListAirlinesController implements javafx.fxml.Initializable {

    @FXML
    private BorderPane rootPane;

    // Root pane of the view

    @FXML
    private TextField searchField; // Text field for searching airlines

    @FXML
    private TableView<compagnie_aerienne> airlineTable; // Table to display airline data

    @FXML
    private TableColumn<compagnie_aerienne, Integer> idColumn; // Column for airline ID



    @FXML
    private TableColumn<compagnie_aerienne, String> nomColumn; // Column for airline name

    @FXML
    private TableColumn<compagnie_aerienne, ImageView> logoColumn; // Column to display airline logo as an image

    @FXML
    private TableColumn<compagnie_aerienne, String> descriptionColumn; // Column for airline description

    @FXML
    private TableColumn<compagnie_aerienne, String> contactColumn; // Column for airline contact information

    @FXML
    private TableColumn<compagnie_aerienne, Void> actionColumn; // Column for action buttons (Delete, Update, Details)

    private final compagnie_areienneservice service = new compagnie_areienneservice(); // Service to interact with airline data
    private final ObservableList<compagnie_aerienne> airlineList = FXCollections.observableArrayList(); // Observable list to hold airline data for the table

    /**
     * Initializes the controller after the FXML file has been loaded.
     * Sets up the cell value factories for each column in the table and loads the initial airline data.
     * Also sets up the search functionality and the action buttons for each row.
     * Applies the stylesheet to the root pane.
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Apply the stylesheet to the rootPane
        if (rootPane != null) {
            rootPane.getStylesheets().add(getClass().getResource("/styles/styleback.css").toExternalForm());
        } else {
            System.err.println("rootPane n'a pas été injecté ! Vérifiez votre fichier FXML.");
        }

        // Set the cell value factory for the ID column to display the 'id' property of the CompagnieAerienne object.
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        // Set the cell value factory for the name column to display the 'nom' property.
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        // Set the cell value factory for the description column to display the 'description' property.
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        // Set the cell value factory for the contact column to display the 'contact_du_responsable' property.
        contactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContact_du_responsable()));

        // Set the cell value factory for the logo column to display the image.
        logoColumn.setCellValueFactory(data -> {
            String imagePath = data.getValue().getLogo();
            System.out.println("Logo Path for " + data.getValue().getNom() + ": " + imagePath); // DEBUGGING log

            ImageView imageView = new ImageView();
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                try {
                    // Check if the image path is relative (starts with "images/")
                    if (imagePath.startsWith("images/")) {
                        // Load the image from the resources folder
                        InputStream inputStream = getClass().getResourceAsStream("/" + imagePath);
                        if (inputStream != null) {
                            imageView.setImage(new Image(inputStream));
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("Image not found in resources: " + imagePath);
                            // Optionally set a placeholder image here
                            // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                        }
                    }
                    // If the path is absolute
                    else {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            imageView.setImage(new Image(imageFile.toURI().toString()));
                        } else {
                            System.out.println("Image not found: " + imageFile.getAbsolutePath());
                            // Optionally set a placeholder image here
                            // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                        }
                    }
                    // Set the fit width and height for the displayed image
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    // Preserve the aspect ratio of the image
                    imageView.setPreserveRatio(true);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                    // Optionally set a placeholder image in case of an error
                    // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                }
            } else {
                // Optionally set a placeholder image if no logo path is available
                // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                System.out.println("Logo path is null or empty for " + data.getValue().getNom());
            }
            return new SimpleObjectProperty<>(imageView);
        });

        // Initialize the action column with Delete, Update, and Details buttons for each row.
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");
            private final Button detailsButton = new Button("Détails");
            private final HBox buttonBox = new HBox(deleteButton, updateButton, detailsButton); // Use HBox to arrange buttons horizontally

            {
                buttonBox.setSpacing(5); // Set spacing between buttons
                buttonBox.getStyleClass().add("action-buttons"); // Apply a CSS class to the button box

                deleteButton.getStyleClass().add("delete-button"); // Apply a CSS class to the delete button
                updateButton.getStyleClass().add("update-button"); // Apply a CSS class to the update button
                detailsButton.getStyleClass().add("details-button"); // Apply a CSS class to the details button

                // Event handler for the Delete button
                deleteButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem(); // Get the airline associated with the current row
                    if (airline != null) {
                        handleDeleteAirline(airline.getId()); // Call the delete function with the airline's ID
                    }
                });

                // Event handler for the Update button
                updateButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem(); // Get the airline associated with the current row
                    if (airline != null) {
                        handleUpdateAirlineForm(airline); // Call the update form function with the airline object
                    }
                });

                // Event handler for the Details button
                detailsButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem(); // Get the airline associated with the current row
                    if (airline != null) {
                        handleShowAirlineDetails(airline); // Call the show details function with the airline object
                    }
                });
            }

            /**
             * Updates the item in the cell. If the cell is not empty, it sets the graphic of the cell to the button box.
             * @param item  The new item being passed into the cell.
             * @param empty Whether or not this cell represents data from a row. If it does not, then this cell is empty.
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // If the cell is empty, don't display any graphic
                } else {
                    setGraphic(buttonBox); // If the cell is not empty, display the button box
                }
            }
        });

        loadAirlines(); // Load the initial airline data into the table

        // Set up a listener for the search field to filter airlines based on the entered text.
        searchField.setOnKeyReleased(this::handleSearch);
    }

    /**
     * Loads all airlines from the service and populates the table view.
     */
    private void loadAirlines() {
        List<compagnie_aerienne> compagnies = service.getAll(); // Retrieve all airlines from the service
        airlineList.setAll(compagnies); // Set the retrieved airlines to the observable list
        airlineTable.setItems(airlineList); // Set the observable list as the items for the table view
    }

    /**
     * Handles the search functionality. Filters the airline list based on the text entered in the search field.
     * @param event The KeyEvent triggered when a key is released in the search field.
     */
    private void handleSearch(KeyEvent event) {
        String keyword = searchField.getText().toLowerCase(); // Get the search keyword in lowercase
        List<compagnie_aerienne> filtered = airlineList.stream()
                .filter(c -> c.getNom().toLowerCase().contains(keyword)) // Filter airlines whose name contains the keyword (case-insensitive)
                .collect(Collectors.toList()); // Collect the filtered airlines into a new list
        airlineTable.setItems(FXCollections.observableArrayList(filtered)); // Update the table view with the filtered list
    }

    /**
     * Handles the action of adding a new airline. Loads the Add Airline Form view.
     */
    @FXML
    private void handleAddAirline() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddAirlineForm.fxml")); // Load the FXML file for adding an airline
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une compagnie"); // Set the title of the new window
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Show the add airline form and wait for it to be closed
            loadAirlines(); // Reload the airline data in the table after the add form is closed
        } catch (IOException e) {
            e.printStackTrace(); // Print any IO exceptions that occur during loading
        }
    }

    /**
     * Handles the deletion of an airline. Prompts the user for confirmation before deleting.
     * @param id The ID of the airline to be deleted.
     */
    private void handleDeleteAirline(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Create a confirmation alert dialog
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette compagnie ?"); // Set the header text
        alert.setContentText("ID de la compagnie : " + id); // Set the content text with the ID of the airline to be deleted

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.delete(id); // If the user clicks OK, delete the airline using the service
                loadAirlines(); // Reload the airline data in the table after deletion
            }
        });
    }

    /**
     * Handles the deletion of the currently selected airline in the table.
     * Shows a warning if no airline is selected.
     */
    @FXML
    private void handleDeleteSelectedAirline() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem(); // Get the currently selected airline
        if (selectedAirline != null) {
            handleDeleteAirline(selectedAirline.getId()); // If an airline is selected, call the delete function
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING); // If no airline is selected, show a warning alert
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour la supprimer.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action of updating an airline. Loads the Update Airline Form for the selected airline.
     * Shows a warning if no airline is selected.
     */
    @FXML
    private void handleUpdateAirline() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem(); // Get the currently selected airline
        if (selectedAirline != null) {
            handleUpdateAirlineForm(selectedAirline); // If an airline is selected, call the update form function
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING); // If no airline is selected, show a warning alert
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour la modifier.");
            alert.showAndWait();
        }
    }

    /**
     * Loads the Update Airline Form and populates it with the data of the airline to be edited.
     * @param airline The CompagnieAerienne object to be updated.
     */
    private void handleUpdateAirlineForm(compagnie_aerienne airline) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateAirline.fxml")); // Load the FXML file for updating an airline
            Parent root = loader.load();

            updateairlinecontroller controller = loader.getController(); // Get the controller of the update form
            controller.setAirlineToEdit(airline); // Pass the airline data to the update form controller

            Stage stage = new Stage();
            stage.setTitle("Modifier la compagnie"); // Set the title of the update form window
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Show the update form and wait for it to be closed
            loadAirlines(); // Reload the airline data in the table after the update form is closed
        } catch (IOException e) {
            e.printStackTrace(); // Print any IO exceptions that occur during loading
        }
    }

    /**
     * Handles the action of showing details of the selected airline. Loads the Show Airline Details view.
     * Shows a warning if no airline is selected.
     */
    @FXML
    private void handleShowSelectedAirlineDetails() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem(); // Get the currently selected airline
        if (selectedAirline != null) {
            handleShowAirlineDetails(selectedAirline); // If an airline is selected, call the show details function
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING); // If no airline is selected, show a warning alert
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour afficher les détails.");
            alert.showAndWait();
        }
    }

    /**
     * Loads the Show Airline Details view and populates it with the details of the given airline.
     * @param airline The CompagnieAerienne object whose details are to be shown.
     */
    @FXML
    private void handleShowAirlineDetails(compagnie_aerienne airline) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/showairline.fxml")); // Load the FXML file for showing airline details
            Parent root = loader.load();

            showairline controller = loader.getController(); // Get the controller of the show details view
            controller.setAirline(airline); // Pass the airline data to the show details controller

            Stage stage = new Stage();
            stage.setTitle("Détails de la compagnie"); // Set the title of the details window
            stage.setScene(new Scene(root));
            stage.show(); // Show the details window
        } catch (IOException e) {
            e.printStackTrace(); // Print any IO exceptions that occur during loading
        }
    }
}