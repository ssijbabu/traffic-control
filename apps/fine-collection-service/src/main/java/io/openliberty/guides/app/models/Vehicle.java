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
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class Vehicle {

    private static final Jsonb JSONB = JsonbBuilder.create();

    private String licenseNumber;

    private String make;

    private String model;

    private String ownerName;

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
        return JSONB.toJson(this);
    }
}