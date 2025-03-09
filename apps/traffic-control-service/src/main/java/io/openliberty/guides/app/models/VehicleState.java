package io.openliberty.guides.app.models;

import java.util.Objects;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "VehicleStates")
@NamedQuery(name = "VehicleStates.findAll", query = "SELECT vs FROM VehicleState vs")
public class VehicleState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "licenseNumber", nullable = false)
    private String licenseNumber;

    @Column(name = "entryTimeStamp", nullable = false)
    private LocalDateTime entryTimeStamp;

    @Column(name = "exitTimeStamp", nullable = true)
    private LocalDateTime exitTimeStamp;

    // Constructors
    public VehicleState() {}

    public VehicleState(String licenseNumber, LocalDateTime entryTimeStamp, LocalDateTime exitTimeStamp) {
        this.licenseNumber = licenseNumber;
        this.entryTimeStamp = entryTimeStamp;
        this.exitTimeStamp = exitTimeStamp;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDateTime getEntryTimeStamp() {
        return entryTimeStamp;
    }

    public void setEntryTimeStamp(LocalDateTime entryTimeStamp) {
        this.entryTimeStamp = entryTimeStamp;
    }

    public LocalDateTime getExitTimeStamp() {
        return exitTimeStamp;
    }

    public void setExitTimeStamp(LocalDateTime exitTimeStamp) {
        this.exitTimeStamp = exitTimeStamp;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleState that = (VehicleState) o;
        return Objects.equals(licenseNumber, that.licenseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber);
    }

    // toString
    @Override
    public String toString() {
        return "VehicleState {" +
                "licenseNumber='" + licenseNumber + '\'' +
                ", entryTimeStamp=" + entryTimeStamp +
                ", exitTimeStamp=" + exitTimeStamp +
                '}';
    }
}