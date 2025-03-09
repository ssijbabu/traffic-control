
package io.openliberty.guides.app;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;

import io.openliberty.guides.app.daos.VehicleDao;
import io.openliberty.guides.app.models.Vehicle;

@RequestScoped
@Path("vehicle-info")
public class VehicleRegistrationResource {

    @Inject
    private VehicleDao vehicleDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewVehicle(Vehicle vehicle) {
        try {
            // Check if vehicle already exists.
            if (vehicleDAO.readVehicle(vehicle.getLicenseNumber()) != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Vehicle with licenseNumber " + vehicle.getLicenseNumber() + " already exists").build();
            }

            vehicleDAO.createVehicle(vehicle);

            // Return CREATED (201) with the created vehicle
            return Response.status(Response.Status.CREATED).entity(vehicle).build();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return Response.serverError().entity("Error creating vehicle: " + e.getMessage()).build(); // 500 Internal Server Error
        }
    }

    @PUT
    @Path("{licenseNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateVehicle(@PathParam("licenseNumber") String licenseNumber, Vehicle vehicle) {
        try {
            // Check if vehicle already exists.
            if (vehicleDAO.readVehicle(vehicle.getLicenseNumber()) == null) {
                vehicleDAO.createVehicle(vehicle);
            }
            else {
                vehicleDAO.updateVehicle(vehicle);
            }           

            // Return CREATED (201) with the created vehicle
            return Response.status(Response.Status.CREATED).entity(vehicle).build();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return Response.serverError().entity("Error creating vehicle: " + e.getMessage()).build(); // 500 Internal Server Error
        }
    }

    @DELETE
    @Path("/{licenseNumber}")
    @Transactional
    public Response deleteVehicle(@PathParam("licenseNumber") String licenseNumber) { // Changed to String
        try {
            Vehicle vehicle = vehicleDAO.readVehicle(licenseNumber);

            if (vehicle == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Vehicle with licenseNumber " + licenseNumber + " does not exist").build();
            }

            vehicleDAO.deleteVehicle(vehicle);
            return Response.status(Response.Status.NO_CONTENT).build();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return Response.serverError().entity("Error deleting vehicle: " + e.getMessage()).build(); // 500 Internal Server Error
        }
    }

    @GET
    @Path("/{licenseNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getVehicle(@PathParam("licenseNumber") String licenseNumber) {
        try {
            Vehicle vehicle = vehicleDAO.readVehicle(licenseNumber);

            if (vehicle == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Vehicle with licenseNumber " + licenseNumber + " not found").build();
            }

            return Response.ok(vehicle, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return Response.serverError().entity("Error retrieving vehicle: " + e.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getVehicles() {
        try {
            List<Vehicle> vehicles = vehicleDAO.readAllVehicles();

            if (vehicles.isEmpty()) {
                return Response.noContent().build(); // 204 No Content
            }

            return Response.ok(vehicles, MediaType.APPLICATION_JSON).build(); // 200 OK with List<Vehicle>
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return Response.serverError().entity("Error retrieving vehicles: " + e.getMessage()).build(); // 500 Internal Server Error
        }
    }
}