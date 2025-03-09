
package io.openliberty.guides.app;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.MessageFormat;

import io.openliberty.guides.app.daos.VehicleStateDao;
import io.openliberty.guides.app.domains.SpeedingViolationCalculator;
import io.openliberty.guides.app.models.SpeedingViolation;
import io.openliberty.guides.app.models.VehicleRegistered;
import io.openliberty.guides.app.models.VehicleState;
import io.openliberty.guides.app.emitter.SpeedingViolationEmitter;

@RequestScoped
public class TrafficControlResource {

    private static Logger logger = Logger.getLogger(TrafficControlResource.class.getName());

    @Inject
    private VehicleStateDao vehicleStateDAO;

    @Inject
    SpeedingViolationEmitter emitter;

    @Inject
    SpeedingViolationCalculator speedingViolationCalculator;

    @POST
    @Path("/entrycam")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response vehicleEntered(VehicleRegistered vehicleRegistered) {
        try {
            logger.info(MessageFormat.format("ENTRY detected in lane {0} at {1} of vehicle with license-number {2}.",
                vehicleRegistered.getLane(), vehicleRegistered.getTimeStamp(), vehicleRegistered.getLicenseNumber()));

            // Store vehicle state
            VehicleState vehicleState = new VehicleState(vehicleRegistered.getLicenseNumber(), vehicleRegistered.getTimeStamp(), null);
            vehicleStateDAO.createVehicleState(vehicleState);

            return Response.ok().build();
        } catch (Exception ex) {
            return Response.serverError().entity("Error occurred while processing ENTRY").build();
        }
    }

    @POST
    @Path("/exitcam")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response vehicleExited(VehicleRegistered vehicleRegistered) {
        try {
            String licenseNumber =  vehicleRegistered.getLicenseNumber();

            VehicleState vehicleState = vehicleStateDAO.readVehicleState(licenseNumber);
            if(vehicleState == null) {
                Response.status(Response.Status.NOT_FOUND).entity("Vehicle with licenseNumber " + licenseNumber + " not found").build();
            }

            logger.info(MessageFormat.format("EXIT detected in lane {0} at {1} of vehicle with license-number {2}.",
                vehicleRegistered.getLane(), vehicleRegistered.getTimeStamp(), vehicleRegistered.getLicenseNumber()));

            vehicleState.setExitTimeStamp(vehicleRegistered.getTimeStamp());
            vehicleStateDAO.updateVehicleState(vehicleState);

            int speedingViolation = speedingViolationCalculator.determineSpeedingViolationInKmh(vehicleState.getEntryTimeStamp(), vehicleState.getExitTimeStamp());
            if(speedingViolation > 0) {
                logger.info(MessageFormat.format("Speeding violation detected ({0} KMh) of vehicle with license-number {1}.", speedingViolation, licenseNumber));

                SpeedingViolation violation = new SpeedingViolation(licenseNumber, speedingViolationCalculator.getRoadId(), speedingViolation, vehicleRegistered.getTimeStamp());

                emitter.sendMessage(violation);
            }

            return Response.ok().build();
            
        } catch (Exception ex) {            
            return Response.serverError().entity("Error occurred while processing EXIT").build();
        }
    }    
}