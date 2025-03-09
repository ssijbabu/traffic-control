package io.openliberty.guides.app.models;

import java.util.Objects;
import java.time.LocalDateTime;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class VehicleRegistered {

    private static final Jsonb JSONB = JsonbBuilder.create();

    private String licenseNumber;

    private int lane;

    private LocalDateTime timeStamp;

    // Constructors
    public VehicleRegistered() {}

    public VehicleRegistered(String licenseNumber, int lane, LocalDateTime timeStamp) {
        this.licenseNumber = licenseNumber;
        this.lane = lane;
        this.timeStamp = timeStamp;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleRegistered that = (VehicleRegistered) o;
        return lane == that.lane && Objects.equals(licenseNumber, that.licenseNumber) && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, lane, timeStamp);
    }

    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    public static VehicleRegistered fromJson(String json) {
        return JSONB.fromJson(json, VehicleRegistered.class);
    }
}