package org.example.Lab3.notification;

// Decorator: the common interface for the real notifier and every decorator.
public interface Notifier {
    void notify(String message);
}
