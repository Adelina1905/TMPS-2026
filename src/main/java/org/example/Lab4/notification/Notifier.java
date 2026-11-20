package org.example.Lab4.notification;

// Decorator: the common interface for the real notifier and every decorator.
public interface Notifier {
    void notify(String message);
}
