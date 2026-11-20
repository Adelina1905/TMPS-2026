package org.example.Lab4.events;

import org.example.Lab4.notification.Notifier;

// Concrete observer: turns events into messages and sends them through the Notifier
// (the Decorator chain from Lab 3: timestamp + log file + console).
public class NotificationObserver implements ApplicationObserver {
    private final Notifier notifier;
    private int sent;

    public NotificationObserver(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        var message = switch (event.type()) {
            case ADDED -> event.title() + " was added";
            case STATUS_CHANGED -> event.title() + " is now " + event.to();
            case STATUS_REVERTED -> event.title() + " went back from " + event.from() + " to " + event.to();
            case DELETED -> event.title() + " was deleted";
            case RESTORED -> event.title() + " was restored";
        };
        notifier.notify(message);
        sent++;
    }

    @Override
    public String name() {
        return "Notifications";
    }

    @Override
    public String summary() {
        return sent + " message(s) sent";
    }
}
