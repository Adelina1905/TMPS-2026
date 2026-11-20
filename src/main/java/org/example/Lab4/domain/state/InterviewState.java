package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state: an interview was scheduled or is in progress.
public class InterviewState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.INTERVIEW;
    }

    @Override
    public ApplicationState receiveOffer() {
        return new OfferState();
    }

    @Override
    public ApplicationState reject() {
        return new RejectedState();
    }

    @Override
    public ApplicationState withdraw() {
        return new WithdrawnState();
    }

    @Override
    public String nextStep() {
        return "Research the company and practise answers for the interview.";
    }
}
