package io.openliberty.guides.app.models;

import java.util.Objects;
import java.time.LocalDateTime;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

public class SpeedingViolation {

    private static final Jsonb JSONB = JsonbBuilder.create();

    private String licenseNumber;

    private String roadId;

    private int violationInKmh;

    private LocalDateTime timeStamp;

    // Constructors
    public SpeedingViolation() {}

    public SpeedingViolation(String licenseNumber, String roadId, int violationInKmh, LocalDateTime timeStamp) {
        this.licenseNumber = licenseNumber;
        this.roadId = roadId;
        this.violationInKmh = violationInKmh;
        this.timeStamp = timeStamp;
    }

    // Getters and Setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getRoadId() {
        return roadId;
    }

    public void setRoadId(String roadId) {
        this.roadId = roadId;
    }

    public int getViolationInKmh() {
        return violationInKmh;
    }

    public void setViolationInKmh(int violationInKmh) {
        this.violationInKmh = violationInKmh;
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
        SpeedingViolation that = (SpeedingViolation) o;
        return violationInKmh == that.violationInKmh && Objects.equals(licenseNumber, that.licenseNumber) && Objects.equals(roadId, that.roadId) && Objects.equals(timeStamp, that.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, roadId, violationInKmh, timeStamp);
    }

    @Override
    public String toString() {
        return JSONB.toJson(this);
    }

    public static SpeedingViolation fromJson(String json) {
        return JSONB.fromJson(json, SpeedingViolation.class);
    }

    public static class SpeedingViolationSerializer implements Serializer<Object> {
        @Override
        public byte[] serialize(String topic, Object data) {
          return JSONB.toJson(data).getBytes();
        }
    }

    public static class SpeedingViolationDeserializer implements Deserializer<SpeedingViolation> {
        @Override
        public SpeedingViolation deserialize(String topic, byte[] data) {
            if (data == null) {
                return null;
            }
            return JSONB.fromJson(new String(data), SpeedingViolation.class);
        }
    }
}