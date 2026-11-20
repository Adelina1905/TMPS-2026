package org.example.Lab4.notification;

// Decorator: the concrete component, the basic notifier that the decorators wrap.
public class ConsoleNotifier implements Notifier {
    @Override
    public void notify(String message) {
        System.out.println("[notification] " + message);
    }
}
