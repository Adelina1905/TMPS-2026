package org.example.Lab4.domain.state;

import org.example.Lab4.domain.ApplicationStatus;

// State: the state interface. Each method is one event in the application's life.
// A state either returns the next state or throws IllegalStateException if the event is not allowed from it.
public interface ApplicationState {
    ApplicationStatus status();

    ApplicationState apply();

    ApplicationState scheduleInterview();

    ApplicationState receiveOffer();

    ApplicationState reject();

    ApplicationState withdraw();

    // What the user should do next while the application is in this state.
    String nextStep();

    // A final state accepts no more events.
    boolean isFinal();
}
