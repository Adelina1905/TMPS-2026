package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state: the job is saved but the application was not sent yet.
public class WishlistState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.WISHLIST;
    }

    @Override
    public ApplicationState apply() {
        return new AppliedState();
    }

    @Override
    public ApplicationState withdraw() {
        return new WithdrawnState();
    }

    @Override
    public String nextStep() {
        return "Prepare your CV and send the application.";
    }
}
