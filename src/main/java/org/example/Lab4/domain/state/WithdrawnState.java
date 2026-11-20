package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state (final): you stopped the process yourself.
public class WithdrawnState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.WITHDRAWN;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public String nextStep() {
        return "Nothing to do. Reapply if you change your mind.";
    }
}
