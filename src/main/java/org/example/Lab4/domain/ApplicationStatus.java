package org.example.Lab4.domain;

import org.example.Lab4.domain.state.AppliedState;
import org.example.Lab4.domain.state.ApplicationState;
import org.example.Lab4.domain.state.InterviewState;
import org.example.Lab4.domain.state.OfferState;
import org.example.Lab4.domain.state.RejectedState;
import org.example.Lab4.domain.state.WishlistState;
import org.example.Lab4.domain.state.WithdrawnState;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

// The status is only a label now (used for storage, filters and user input).
// The transition rules moved out of this enum into the State classes; each constant just says
// which state object it stands for and which event of the current state leads to it.
public enum ApplicationStatus {
    WISHLIST(WishlistState::new, null),
    APPLIED(AppliedState::new, ApplicationState::apply),
    INTERVIEW(InterviewState::new, ApplicationState::scheduleInterview),
    OFFER(OfferState::new, ApplicationState::receiveOffer),
    REJECTED(RejectedState::new, ApplicationState::reject),
    WITHDRAWN(WithdrawnState::new, ApplicationState::withdraw);

    private final Supplier<ApplicationState> state;
    private final UnaryOperator<ApplicationState> event;

    ApplicationStatus(Supplier<ApplicationState> state, UnaryOperator<ApplicationState> event) {
        this.state = state;
        this.event = event;
    }

    // The state object for this status, used when an application is created or loaded from storage.
    public ApplicationState toState() {
        return state.get();
    }

    // Asks the current state to move to this status. The state decides whether that is allowed.
    public ApplicationState reachFrom(ApplicationState current) {
        if (event == null) throw new IllegalStateException(current.status() + " -> " + this + " not allowed");
        return event.apply(current);
    }
}
