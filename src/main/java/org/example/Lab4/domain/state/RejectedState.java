package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state (final): the company said no.
public class RejectedState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.REJECTED;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public String nextStep() {
        return "Ask for feedback; you can reapply later.";
    }
}
