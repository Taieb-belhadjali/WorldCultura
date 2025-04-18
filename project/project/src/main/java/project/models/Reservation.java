package project.models;

public class Reservation {
    private Integer id;
    private rehla rehla; // Changed type to your rehla class
    private String userName;
    private String email;
    private String contact;
    private Integer userId;

    public Reservation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public rehla getRehla() {
        return rehla;
    }

    public void setRehla(rehla rehla) {
        this.rehla = rehla;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}