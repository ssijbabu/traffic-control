package io.openliberty.guides.app;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.logging.Level;

import io.openliberty.guides.app.clients.VehicleRegistrationClient;
import io.openliberty.guides.app.domains.FineCalculator;
import io.openliberty.guides.app.models.SpeedingViolation;
import io.openliberty.guides.app.models.Vehicle;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/fines") // Added base path.
public class FineCollectionResource {

    private static Logger logger = Logger.getLogger(FineCollectionResource.class.getName());

    @Inject
    private VehicleRegistrationClient vehicleRegistrationClient;

    @Inject
    private FineCalculator fineCollector;

    @Incoming("speedingViolationIn")
    public void collectFine(SpeedingViolation speedingViolation) {
        try {
            double fine = fineCollector.CalculateFine(speedingViolation.getViolationInKmh());

            Vehicle vehicle = vehicleRegistrationClient.getVehicle(speedingViolation.getLicenseNumber());

            String fineString = fine == 0 ? "tbd by the prosecutor" : fine + " Euro";

            String logMessage = MessageFormat.format(
                    "Sent speeding ticket to {0}. Road: {1}, Licensenumber: {2}, Vehicle: {3} {4}, Violation: {5} Km/h, Fine: {6}, On: {7} at {8}.",
                    vehicle.getOwnerName(),
                    speedingViolation.getRoadId(),
                    speedingViolation.getLicenseNumber(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    speedingViolation.getViolationInKmh(),
                    fineString,
                    speedingViolation.getTimeStamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    speedingViolation.getTimeStamp().format(DateTimeFormatter.ofPattern("hh:mm:ss"))
            );

            logger.info(logMessage);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An unexpected error occurred while collecting fine for license number: " + speedingViolation.getLicenseNumber(), ex);
        }
    }
}