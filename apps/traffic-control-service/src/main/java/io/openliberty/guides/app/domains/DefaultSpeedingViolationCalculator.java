package io.openliberty.guides.app.domains;

import java.time.LocalDateTime;
import java.time.Duration;

public class DefaultSpeedingViolationCalculator implements SpeedingViolationCalculator {
    private String roadId = "A1";
    private int sectionLengthInKm = 10;
    private int maxAllowedSpeedInKmh = 100;
    private int legalCorrectionInKmh = 5;

    public DefaultSpeedingViolationCalculator(String roadId, int sectionLengthInKm, int maxAllowedSpeedInKmh, int legalCorrectionInKmh) {
        this.roadId = roadId;
        this.sectionLengthInKm = sectionLengthInKm;
        this.maxAllowedSpeedInKmh = maxAllowedSpeedInKmh;
        this.legalCorrectionInKmh = legalCorrectionInKmh;
    }

    @Override
    public int determineSpeedingViolationInKmh(LocalDateTime entryTimestamp, LocalDateTime exitTimestamp) {
        Duration elapsed = Duration.between(entryTimestamp, exitTimestamp);
        double elapsedSeconds = elapsed.getSeconds();
        double avgSpeedInKmh = Math.round((double) sectionLengthInKm / elapsedSeconds * 3600); // 3600 seconds in an hour
        int violation = (int) (avgSpeedInKmh - maxAllowedSpeedInKmh - legalCorrectionInKmh);
        return violation;
    }

    @Override
    public String getRoadId() {
        return roadId;
    }
}