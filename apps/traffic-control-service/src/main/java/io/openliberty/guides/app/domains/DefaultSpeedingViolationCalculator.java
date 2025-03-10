package io.openliberty.guides.app.domains;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.Duration;

@ApplicationScoped 
public class DefaultSpeedingViolationCalculator implements SpeedingViolationCalculator {
    private String roadId;
    private int sectionLengthInKm;
    private int maxAllowedSpeedInKmh;
    private int legalCorrectionInKmh;
    
    public DefaultSpeedingViolationCalculator() {
        this.roadId = "A1"; // Default road ID
        this.sectionLengthInKm = 10; // Default section length
        this.maxAllowedSpeedInKmh = 100; // Default max speed
        this.legalCorrectionInKmh = 5; // Default legal correction
    }

    
    public DefaultSpeedingViolationCalculator(String roadId, int sectionLengthInKm, int maxAllowedSpeedInKmh, int legalCorrectionInKmh) {
        this.roadId = roadId;
        this.sectionLengthInKm = sectionLengthInKm;
        this.maxAllowedSpeedInKmh = maxAllowedSpeedInKmh;
        this.legalCorrectionInKmh = legalCorrectionInKmh;
    }

    @Override
    public int determineSpeedingViolationInKmh(LocalDateTime entryTimestamp, LocalDateTime exitTimestamp) {
        if (entryTimestamp == null || exitTimestamp == null) {
            return 0;
        }

        Duration elapsed = Duration.between(entryTimestamp, exitTimestamp);
        double elapsedSeconds = elapsed.getSeconds();
        double avgSpeedInKmh = Math.round((double) sectionLengthInKm / elapsedSeconds * 3600);
        int violation = (int) (avgSpeedInKmh - maxAllowedSpeedInKmh - legalCorrectionInKmh);

        if (violation < 0) {
            return 0;
        }

        return violation;
    }

    @Override
    public String getRoadId() {
        return roadId;
    }
}