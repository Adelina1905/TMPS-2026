package org.example.Lab2.notification;

public class ConsoleNotifier implements Notifier {
    @Override
    public void notify(String message) {
        System.out.println("[notification] " + message);
    }
}
