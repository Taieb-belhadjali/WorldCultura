package project.models;

import java.util.Objects;

public class participation {
    private int id;
    private int event_id;
    private String nom;
    private String email;
    private int telephone;

    public participation(int id, int event_id, String nom, String email, int telephone) {
        this.id = id;
        this.event_id = event_id;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
    }


    public participation(int telephone, String email, String nom, int event_id) {
        this.telephone = telephone;
        this.email = email;
        this.nom = nom;
        this.event_id = event_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        participation that = (participation) o;
        return id == that.id && event_id == that.event_id && telephone == that.telephone && Objects.equals(nom, that.nom) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event_id, nom, email, telephone);
    }

    @Override
    public String toString() {
        return "participation{" +
                "id=" + id +
                ", event_id=" + event_id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", telephone=" + telephone +
                '}';
    }


}
