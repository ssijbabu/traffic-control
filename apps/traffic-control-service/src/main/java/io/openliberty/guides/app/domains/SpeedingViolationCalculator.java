package io.openliberty.guides.app.domains;

import java.time.LocalDateTime;

public interface SpeedingViolationCalculator {
    int determineSpeedingViolationInKmh(LocalDateTime entryTimestamp, LocalDateTime exitTimestamp);
    String getRoadId();
}