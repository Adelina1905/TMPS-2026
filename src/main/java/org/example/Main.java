package org.example;

import org.example.Lab2.ApplicationPrinter;
import org.example.Lab2.ApplicationService;
import org.example.Lab2.ConsoleMenu;
import org.example.Lab2.notification.ConsoleNotifier;
import org.example.Lab2.notification.Notifier;
import org.example.Lab2.repository.ApplicationRepository;
import org.example.Lab2.repository.InMemoryRepositoryFactory;
import org.example.Lab2.repository.JsonRepositoryFactory;
import org.example.Lab2.repository.RepositoryFactory;

import java.nio.file.Path;

public class Main {
    static void main(String[] args) {
        // Factory Method: pass "memory" as an argument to use in-memory storage, otherwise JSON file storage.
        RepositoryFactory factory = args.length > 0 && args[0].equalsIgnoreCase("memory")
                ? new InMemoryRepositoryFactory()
                : new JsonRepositoryFactory(Path.of("applications.json"));
        System.out.println("Storage: " + factory.description());

        ApplicationRepository repository = factory.createRepository();
        Notifier notifier = new ConsoleNotifier();
        var service = new ApplicationService(repository, notifier);
        new ConsoleMenu(service, new ApplicationPrinter()).run();
    }
}
