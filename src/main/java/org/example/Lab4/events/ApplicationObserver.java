package org.example.Lab4.events;

// Observer: the observer interface. The subject only knows this interface, never a concrete observer.
public interface ApplicationObserver {
    void onEvent(ApplicationEvent event);

    // Short name shown in the "Manage observers" menu.
    String name();

    // What this observer has done so far in this session.
    String summary();
}
