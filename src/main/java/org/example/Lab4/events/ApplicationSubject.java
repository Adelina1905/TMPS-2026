package org.example.Lab4.events;

// Observer: the subject interface. Observers can subscribe and unsubscribe at any time.
public interface ApplicationSubject {
    void subscribe(ApplicationObserver observer);

    void unsubscribe(ApplicationObserver observer);

    boolean isSubscribed(ApplicationObserver observer);

    void publish(ApplicationEvent event);
}
