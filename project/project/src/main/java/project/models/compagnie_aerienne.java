package project.models;

public class compagnie_aerienne {
    private Integer id;
    private String nom;
    private String logo;
    private String description;
    private String contact_du_responsable;

    public compagnie_aerienne() {
        // No more list of voles
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact_du_responsable() {
        return contact_du_responsable;
    }

    public void setContact_du_responsable(String contact_du_responsable) {
        this.contact_du_responsable = contact_du_responsable;
    }


}