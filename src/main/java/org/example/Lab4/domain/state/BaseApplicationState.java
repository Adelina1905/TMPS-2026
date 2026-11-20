package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// State: by default every event is forbidden. A concrete state overrides only the events it allows,
// so the transition rules of one status live in one class instead of a switch.
public abstract class BaseApplicationState implements ApplicationState {

    @Override
    public ApplicationState apply() {
        throw forbidden(ApplicationStatus.APPLIED);
    }

    @Override
    public ApplicationState scheduleInterview() {
        throw forbidden(ApplicationStatus.INTERVIEW);
    }

    @Override
    public ApplicationState receiveOffer() {
        throw forbidden(ApplicationStatus.OFFER);
    }

    @Override
    public ApplicationState reject() {
        throw forbidden(ApplicationStatus.REJECTED);
    }

    @Override
    public ApplicationState withdraw() {
        throw forbidden(ApplicationStatus.WITHDRAWN);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    protected IllegalStateException forbidden(ApplicationStatus target) {
        return new IllegalStateException(status() + " -> " + target + " not allowed");
    }

    @Override
    public String toString() {
        return status().toString();
    }
}
