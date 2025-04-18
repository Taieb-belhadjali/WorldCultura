package project.models;

import java.time.LocalDateTime;

public class FeedBack {
    private int id;
    private int bonPlanId;
    private String commentaire;
    private int rating;
    private LocalDateTime dateCreation;

    // Constructors
    public FeedBack() {}

    public FeedBack(int id, int bonPlanId, String commentaire, int rating, LocalDateTime dateCreation) {
        this.id = id;
        this.bonPlanId = bonPlanId;
        this.commentaire = commentaire;
        this.rating = rating;
        this.dateCreation = dateCreation;
    }

    public FeedBack(int bonPlanId, String commentaire, int rating, LocalDateTime dateCreation) {
        this.bonPlanId = bonPlanId;
        this.commentaire = commentaire;
        this.rating = rating;
        this.dateCreation = dateCreation;
    }

    // Getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBonPlanId() { return bonPlanId; }
    public void setBonPlanId(int bonPlanId) { this.bonPlanId = bonPlanId; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }


    public String RatetoString() {
        return "Note: " + rating + " ★ - " + commentaire;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", bonPlanId=" + bonPlanId +
                ", commentaire='" + commentaire + '\'' +
                ", rating=" + rating +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
