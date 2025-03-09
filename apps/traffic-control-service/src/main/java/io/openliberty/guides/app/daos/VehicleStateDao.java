package io.openliberty.guides.app.daos;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import io.openliberty.guides.app.models.VehicleState;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class VehicleStateDao {

    @PersistenceContext(name = "jpa-unit-tc")
    private EntityManager em;
    
    public void createVehicleState(VehicleState vehicleState) {
        em.persist(vehicleState);
    }

    public VehicleState readVehicleState(String licenseNumber) {
        return em.find(VehicleState.class, licenseNumber);
    }
    
    public void updateVehicleState(VehicleState vehicleState) {
        em.merge(vehicleState);
    }

    public void deleteVehicleState(VehicleState vehicleState) {        
        em.remove(vehicleState);
    }

    public List<VehicleState> readAllVehicleStates() {
        return em.createNamedQuery("VehicleStates.findAll", VehicleState.class).getResultList();
    }
}