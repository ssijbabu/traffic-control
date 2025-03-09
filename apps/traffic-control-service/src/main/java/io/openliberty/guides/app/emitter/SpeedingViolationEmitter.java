package io.openliberty.guides.app.emitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.openliberty.guides.app.models.SpeedingViolation;

import java.time.LocalDateTime;

@ApplicationScoped
public class SpeedingViolationEmitter {

    @Inject
    @Channel("speedingViolationOut") // Matches the connector name
    Emitter<SpeedingViolation> emitter;

    public void sendMessage(SpeedingViolation violation) {
        emitter.send(violation);
    }
}