package project.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class event {
    private int ID;
    private String Name;
    private String date_debut;
    private String date_fin;
    private String Description;
    private String image;
    private List<participation> participations = new ArrayList<>();

    public event() {}

    public event(int ID, String name, String date_debut, String date_fin, String description, String image) {
        this.ID = ID;
        Name = name;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        Description = description;
        this.image = image;
    }
    public event(String name, String date_debut, String date_fin, String description, String image) {
        this.Name = name;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.Description = description;
        this.image = image;
    }

    // Getters & Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }
    public String getName() { return Name; }
    public void setName(String name) { Name = name; }
    public String getDate_debut() { return date_debut; }
    public void setDate_debut(String date_debut) { this.date_debut = date_debut; }
    public String getDate_fin() { return date_fin; }
    public void setDate_fin(String date_fin) { this.date_fin = date_fin; }
    public String getDescription() { return Description; }
    public void setDescription(String description) { Description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // Gestion des participations
    public List<participation> getParticipations() { return participations; }
    public void setParticipations(List<participation> participations) { this.participations = participations; }
    public void addParticipation(participation p) { this.participations.add(p); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        event event = (event) o;
        return ID == event.ID &&
                Objects.equals(Name, event.Name) &&
                Objects.equals(date_debut, event.date_debut) &&
                Objects.equals(date_fin, event.date_fin) &&
                Objects.equals(Description, event.Description) &&
                Objects.equals(image, event.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, Name, date_debut, date_fin, Description, image);
    }

    @Override
    public String toString() {
        return "event{" +
                "ID=" + ID +
                ", Name='" + Name + '\'' +
                ", date_debut='" + date_debut + '\'' +
                ", date_fin='" + date_fin + '\'' +
                ", Description='" + Description + '\'' +
                ", image='" + image + '\'' +
                ", participations=" + participations.size() +
                '}';
    }
}