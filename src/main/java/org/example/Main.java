package org.example;

import org.example.Lab3.ApplicationPrinter;
import org.example.Lab3.ApplicationService;
import org.example.Lab3.ConsoleMenu;
import org.example.Lab3.notification.ConsoleNotifier;
import org.example.Lab3.notification.FileLogNotifier;
import org.example.Lab3.notification.Notifier;
import org.example.Lab3.notification.TimestampNotifier;
import org.example.Lab3.repository.ApplicationRepository;
import org.example.Lab3.repository.InMemoryRepositoryFactory;
import org.example.Lab3.repository.JsonRepositoryFactory;
import org.example.Lab3.repository.RepositoryFactory;

import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        // Factory Method: pass "memory" as an argument to use in-memory storage, otherwise JSON file storage.
        RepositoryFactory factory = args.length > 0 && args[0].equalsIgnoreCase("memory")
                ? new InMemoryRepositoryFactory()
                : new JsonRepositoryFactory(Path.of("applications.json"));
        System.out.println("Storage: " + factory.description());

        ApplicationRepository repository = factory.createRepository();
        // Decorator: the console notifier is wrapped so that every message gets a timestamp
        // and is also written to notifications.log. The order matters: the timestamp is added
        // first, so both the log file and the console receive the timestamped message.
        Notifier notifier = new TimestampNotifier(
                new FileLogNotifier(new ConsoleNotifier(), Path.of("notifications.log")));
        var service = new ApplicationService(repository, notifier);
        new ConsoleMenu(service, new ApplicationPrinter()).run();
    }
}
