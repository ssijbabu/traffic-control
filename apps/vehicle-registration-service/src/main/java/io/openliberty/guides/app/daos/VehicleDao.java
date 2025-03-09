package io.openliberty.guides.app.daos;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import io.openliberty.guides.domains.models.Vehicle;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class VehicleDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;
    
    public void createVehicle(Vehicle vehicle) {
        em.persist(vehicle);
    }

    public Vehicle readVehicle(String licenseNumber) {
        return em.find(Vehicle.class, licenseNumber);
    }
    
    public void updateVehicle(Vehice vehicle) {
        em.merge(vehicle);
    }

    public void deleteVehicle(Vehicle vehicle) {        
        em.remove(vehicle);
    }

    public List<Vehicle> readAllVehicles() {
        return em.createNamedQuery("Vehicles.findAll", Vehicle.class).getResultList();
    }

    public List<Vehicle> findVehicle(String licenseNumber) {
        return em.createNamedQuery("Vehicles.findVehicle", Vehicle.class)
            .setParameter("licenseNumber", licenseNumber);
    }
}