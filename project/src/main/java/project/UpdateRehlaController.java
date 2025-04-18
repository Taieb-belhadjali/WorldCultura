package project; // Assuming 'project' is your main package

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UpdateRehlaController extends Application {

    private Stage updateStage;
    private Object rehlaToEdit; // You'll need to define your Rehla model here

    public void setRehlaToEdit(Object rehla) { // Replace Object with your actual Rehla model
        this.rehlaToEdit = rehla;
        if (updateStage != null && updateStage.isShowing()) {
            // Optionally update the UI if the stage is already showing
            // This depends on how your UpdateRehlaForm.fxml is structured
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.updateStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateRehlaForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 400, 450); // Adjust size as needed
            primaryStage.setTitle("Modifier le Vol");
            primaryStage.setScene(scene);
            primaryStage.show();

            // You might need to access the controller loaded by FXMLLoader here
            // to pass the rehlaToEdit data directly after loading.
            // UpdateRehlaFormController controller = loader.getController();
            // controller.setRehla(rehlaToEdit);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error loading FXML
        }
    }

    public void show() {
        if (updateStage == null) {
            try {
                start(new Stage()); // Create a new stage if it doesn't exist
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            updateStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}