package org.example.Lab2;

import org.example.Lab2.domain.ApplicationStatus;
import org.example.Lab2.domain.Company;
import org.example.Lab2.filters.CompanyFilter;
import org.example.Lab2.filters.StatusFilter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleMenu {
    private final ApplicationService service;
    private final ApplicationPrinter printer;
    private final Scanner in = new Scanner(System.in);

    public ConsoleMenu(ApplicationService service, ApplicationPrinter printer) {
        this.service = service;
        this.printer = printer;
    }

    public void run(){
        while (true) {
            System.out.println("""

                1. Add application
                2. List all
                3. Change status
                4. Filter by status
                5. Filter by company
                6. Show status history
                7. Delete
                0. Exit""");
            if (!in.hasNextLine()) return;
            try {
                switch (in.nextLine().trim()) {
                    case "1" -> add();
                    case "2" -> printer.printAll(service.findAll());
                    case "3" -> changeStatus();
                    case "4" -> printer.printAll(service.find(new StatusFilter(askStatus())));
                    case "5" -> printer.printAll(service.find(new CompanyFilter(ask("Company: "))));
                    case "6" -> printer.printHistory(service.history(askId()));
                    case "7" -> delete();
                    case "0" -> { return; }
                    default -> System.out.println("Unknown option");
                }
            } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private void add() {
        var company = new Company(askNonBlank("Company: "));
        var position = askNonBlank("Position: ");
        var app = service.add(company, position, LocalDate.now());
        System.out.println("Added:");
        printer.print(app);
    }
    private void changeStatus() {
        var id = askId();
        var next = askStatus();
        service.changeStatus(id, next);
    }
    private void delete() {
        service.delete(askId());
        System.out.println("Deleted.");
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) throw new NoSuchElementException("No input");
        return in.nextLine().trim();
    }
    private String askNonBlank(String prompt) {
        var value = ask(prompt);
        if (value.isEmpty()) throw new IllegalArgumentException("Value must not be empty");
        return value;
    }
    private UUID askId() {
        return service.resolveId(ask("Id (first few characters): "));
    }
    private ApplicationStatus askStatus() {
        var value = ask("Status " + Arrays.toString(ApplicationStatus.values()) + ": ");
        try {
            return ApplicationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown status '" + value + "'");
        }
    }
}
