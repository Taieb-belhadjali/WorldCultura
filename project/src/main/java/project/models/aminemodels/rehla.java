package project.models.aminemodels;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class rehla {
    private Integer id;
    private compagnie_aerienne agence; // Changed type to your compagnie_aerienne class
    private String depart;
    private String destination;
    private LocalDateTime depart_date; // Using Java's LocalDateTime for date and time
    private LocalDateTime arrival_date;
    private Float price;
    private List<Reservation> reservations; // Assuming you have a Reservation class

    public rehla() {
        this.reservations = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public compagnie_aerienne getAgence() {
        return agence;
    }

    public void setAgence(compagnie_aerienne agence) {
        this.agence = agence;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepart_date() {
        return depart_date;
    }

    public void setDepart_date(LocalDateTime depart_date) {
        this.depart_date = depart_date;
    }

    public LocalDateTime getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(LocalDateTime arrival_date) {
        this.arrival_date = arrival_date;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        if (!this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
            reservation.setRehla(this); // Assuming Reservation class has setRehla method
        }
    }

    public void removeReservation(Reservation reservation) {
        if (this.reservations.remove(reservation)) {
            if (reservation.getRehla() != null && reservation.getRehla().equals(this)) {
                reservation.setRehla(null);
            }
        }
    }
}