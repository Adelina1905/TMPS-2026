package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// Concrete state: the application was sent and we are waiting for an answer.
public class AppliedState extends BaseApplicationState {
    @Override
    public ApplicationStatus status() {
        return ApplicationStatus.APPLIED;
    }

    @Override
    public ApplicationState scheduleInterview() {
        return new InterviewState();
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
        return "Wait for an answer; follow up if there is no reply in a week.";
    }
}
