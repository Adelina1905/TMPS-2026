package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state (final): an offer was received.
public class OfferState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.OFFER;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public String nextStep() {
        return "Compare the offer and give your answer.";
    }
}
