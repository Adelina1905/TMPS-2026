package org.example;

import org.example.Lab1.ApplicationPrinter;
import org.example.Lab1.ApplicationService;
import org.example.Lab1.ConsoleMenu;
import org.example.Lab1.notification.ConsoleNotifier;
import org.example.Lab1.notification.Notifier;
import org.example.Lab1.repository.ApplicationRepository;
import org.example.Lab1.repository.JsonFileApplicationRepository;

import java.nio.file.Path;

public class Main {
    static void main() {
        // DIP: swap for `new InMemoryApplicationRepository()` and nothing else has to change.
        ApplicationRepository repository = new JsonFileApplicationRepository(Path.of("applications.json"));
        Notifier notifier = new ConsoleNotifier();
        var service = new ApplicationService(repository, notifier);
        new ConsoleMenu(service, new ApplicationPrinter()).run();
    }
}
