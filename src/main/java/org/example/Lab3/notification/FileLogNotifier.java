package org.example.Lab3.notification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// Decorator: passes the message on, then also appends it to a log file.
// A failure to write the log is reported but does not stop the program.
public class FileLogNotifier extends NotifierDecorator {
    private final Path logFile;

    public FileLogNotifier(Notifier wrapped, Path logFile) {
        super(wrapped);
        this.logFile = logFile;
    }

    @Override
    public void notify(String message) {
        super.notify(message);
        try {
            Files.writeString(logFile, message + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Warning: could not write to " + logFile + ": " + e.getMessage());
        }
    }
}
