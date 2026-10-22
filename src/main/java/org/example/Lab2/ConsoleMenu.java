package org.example.Lab2;

import org.example.Lab2.domain.ApplicationStatus;
import org.example.Lab2.domain.Company;
import org.example.Lab2.domain.JobApplication;
import org.example.Lab2.filters.CompanyFilter;
import org.example.Lab2.filters.StatusFilter;

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
                8. Reapply
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
                    case "8" -> reapply();
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
        var builder = JobApplication.builder(company, askNonBlank("Position: "));
        // Optional fields: leaving the answer empty skips them.
        var location = ask("Location (optional): ");
        if (!location.isEmpty()) builder.location(location);
        var salary = ask("Salary (optional): ");
        if (!salary.isEmpty()) builder.salary(parseSalary(salary));
        var notes = ask("Notes (optional): ");
        if (!notes.isEmpty()) builder.notes(notes);
        var app = service.add(builder);
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
    private void reapply() {
        var app = service.reapply(askId());
        System.out.println("Created a copy:");
        printer.print(app);
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
    private int parseSalary(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Salary must be a whole number, got '" + value + "'");
        }
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
