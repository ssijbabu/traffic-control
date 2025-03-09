package io.openliberty.guides.app.models;

import java.util.Objects;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "Vehicles")
@NamedQuery(name = "Vehicles.findAll", query = "SELECT e FROM Vehicle e")
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "licenseNumber", nullable = false)
    private String licenseNumber;

    @Column(name = "make", nullable = false)
    private String make;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "ownerName", nullable = false)
    private String ownerName;

    @Column(name = "ownerEmail", unique = true, nullable = false)
    private String ownerEmail;

    // Constructors
    public Vehicle() {}

    public Vehicle(String licenseNumber, String make, String model, String ownerName, String ownerEmail) {
        this.licenseNumber = licenseNumber;
        this.make = make;
        this.model = model;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(licenseNumber, vehicle.licenseNumber) && Objects.equals(make, vehicle.make) && Objects.equals(model, vehicle.model) && Objects.equals(ownerName, vehicle.ownerName) && Objects.equals(ownerEmail, vehicle.ownerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, make, model, ownerName, ownerEmail);
    }

    // toString
    @Override
    public String toString() {
        return "Vehicle {" +
                "licenseNumber='" + licenseNumber + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }
}