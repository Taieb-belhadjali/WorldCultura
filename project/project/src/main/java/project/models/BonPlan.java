package project.models;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BonPlan {
    private int id;
    private String titre;
    private String description;
    private String lieu;
    private LocalDateTime dateCreation;
    private LocalDate dateExpiration;
    private String categorie;
    private double RatingMoy ;

    // Liste des feedbacks associés
    private List<FeedBack> feedbacks;

    // Constructeurs
    public BonPlan() {}

    public BonPlan( String titre, String description, String lieu, LocalDateTime dateCreation, LocalDate dateExpiration, String categorie) {

        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.categorie = categorie;
        this.RatingMoy = 0.0;
    }

    public BonPlan(int id , String titre, String description, String lieu, LocalDateTime dateCreation, LocalDate dateExpiration, String categorie) {

        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.categorie = categorie;
        this.RatingMoy = 0.0;
    }

    // Getters et setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getRatingMoy() { return RatingMoy; }
    public void setRatingMoy(double ratingMoy) { RatingMoy = ratingMoy; }
    public double calculerRatingMoyen() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        double total = 0;
        for (FeedBack feedback : feedbacks) {
            total += feedback.getRating();
        }
        return total / feedbacks.size();
    }


    public List<FeedBack> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<FeedBack> feedbacks) { this.feedbacks = feedbacks; }
    public void addFeedback(FeedBack feedback) {
        this.feedbacks.add(feedback);
       this.setRatingMoy(this.getRatingMoy() );
    }

    @Override
    public String toString() {
        return "BonPlan{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", lieu='" + lieu + '\'' +
                ", dateCreation=" + dateCreation +
                ", dateExpiration=" + dateExpiration +
                ", categorie='" + categorie + '\'' +
                ", feedbacks=" + (feedbacks != null ? feedbacks.size() + " feedback(s)" : "aucun feedback") +
                ", RatingMoy=" + RatingMoy +
                '}';
    }




}

