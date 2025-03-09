package io.openliberty.guides.app.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.openliberty.guides.app.models.Vehicle;

@RegisterRestClient(configKey = "vehicleRegistrationClient",
                     baseUri = "http://localhost:9080/api")
@RegisterProvider(UnknownUriExceptionMapper.class)
@Path("/vehicle-info")
public interface VehicleRegistrationClient extends AutoCloseable {

  @GET
  @Path("/{licenseNumber}")
  @Produces(MediaType.APPLICATION_JSON)
  Vehicle getVehicle(@PathParam("licenseNumber") String licenseNumber) throws UnknownUriException, ProcessingException;
}
