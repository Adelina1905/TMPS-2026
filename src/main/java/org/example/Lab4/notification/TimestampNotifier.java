package org.example.Lab4.notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Decorator: adds the current date and time in front of the message, then passes it on.
public class TimestampNotifier extends NotifierDecorator {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TimestampNotifier(Notifier wrapped) {
        super(wrapped);
    }

    @Override
    public void notify(String message) {
        super.notify("[" + LocalDateTime.now().format(FORMAT) + "] " + message);
    }
}
