package io.openliberty.guides.app.domains;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface FineCalculator {

    public int CalculateFine(int violationInKmh);
}