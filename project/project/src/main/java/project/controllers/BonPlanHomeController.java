package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import project.models.BonPlan;
import project.service.BonPlanService;

public class BonPlanHomeController {

    @FXML
    private Label bonPlanIdLabel; // Si vous voulez afficher l'ID dans un Label

    private Integer bonPlanId;
    private BonPlanService bonPlanService = new BonPlanService(); // Instance de votre service

    public void setBonPlanId(Integer id) {
        this.bonPlanId = id;
        // Vous pouvez appeler displayBonPlanDetails() ici si vous voulez
        // que les détails se chargent immédiatement après avoir reçu l'ID.
        // displayBonPlanDetails();
    }

    public Integer getBonPlanId() {
        return bonPlanId;
    }

    @FXML
    public void initialize() {
        // Initialisations si nécessaire pour BonPlanHome
        // Par exemple, configurer des listeners, etc.
    }

    public void displayBonPlanDetails() {
        if (bonPlanId != null) {
            // Récupérer les détails du BonPlan en utilisant l'ID
            BonPlan bonPlan = bonPlanService.getById(bonPlanId);

            if (bonPlan != null) {
                // Afficher les détails dans l'interface BonPlanHome.fxml
                // en utilisant les éléments @FXML que vous aurez définis.
                if (bonPlanIdLabel != null) {
                    bonPlanIdLabel.setText("ID du Bon Plan: " + bonPlan.getId());
                }
                // Vous devrez également afficher les autres détails du bon plan
                // (titre, description, etc.) dans les éléments correspondants
                // de votre interface bonPlanHome.fxml.
            } else {
                // Gérer le cas où le BonPlan avec cet ID n'est pas trouvé
                if (bonPlanIdLabel != null) {
                    bonPlanIdLabel.setText("Bon Plan non trouvé avec l'ID: " + bonPlanId);
                }
            }
        } else {
            // Gérer le cas où l'ID n'a pas encore été reçu
            if (bonPlanIdLabel != null) {
                bonPlanIdLabel.setText("Aucun ID de Bon Plan sélectionné.");
            }
        }
    }
}