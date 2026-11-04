package org.example.Lab3.notification;

// Decorator: base class for decorators. It is a Notifier and also holds the Notifier it wraps,
// so decorators can be stacked in any order. By default it just passes the message on.
public abstract class NotifierDecorator implements Notifier {
    protected final Notifier wrapped;

    protected NotifierDecorator(Notifier wrapped) {
        if (wrapped == null) throw new IllegalArgumentException("Wrapped notifier must not be null");
        this.wrapped = wrapped;
    }

    @Override
    public void notify(String message) {
        wrapped.notify(message);
    }
}
