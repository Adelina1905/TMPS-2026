package org.example.Lab4.events;

import java.util.ArrayList;
import java.util.List;

// Observer: the concrete subject. ApplicationService publishes every change here,
// and the publisher passes the event to everyone who is subscribed at that moment.
public class ApplicationEventPublisher implements ApplicationSubject {
    private final List<ApplicationObserver> observers = new ArrayList<>();

    @Override
    public void subscribe(ApplicationObserver observer) {
        if (observer == null) throw new IllegalArgumentException("Observer must not be null");
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void unsubscribe(ApplicationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public boolean isSubscribed(ApplicationObserver observer) {
        return observers.contains(observer);
    }

    @Override
    public void publish(ApplicationEvent event) {
        // A copy is used, so an observer may unsubscribe itself (or others) while being notified.
        for (var observer : List.copyOf(observers)) {
            observer.onEvent(event);
        }
    }
}
