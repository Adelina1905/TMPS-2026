package org.example.Lab3;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.Company;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.filters.AndFilter;
import org.example.Lab3.filters.ApplicationFilter;
import org.example.Lab3.filters.CompanyFilter;
import org.example.Lab3.filters.NotFilter;
import org.example.Lab3.filters.OrFilter;
import org.example.Lab3.filters.StatusFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                9. Advanced filter
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
                    case "9" -> advancedFilter();
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
    // Composite: builds a tree of filters, e.g. (status = APPLIED OR status = INTERVIEW) AND company = Google AND NOT ...
    private void advancedFilter() {
        System.out.println("Leave a question empty to skip it. Separate several values with commas.");
        var parts = new ArrayList<ApplicationFilter>();

        var statuses = askList("Statuses (any of): ").stream()
                .map(s -> (ApplicationFilter) new StatusFilter(parseStatus(s))).toList();
        if (!statuses.isEmpty()) parts.add(new OrFilter(statuses));

        var companies = askList("Companies (any of): ").stream()
                .map(c -> (ApplicationFilter) new CompanyFilter(c)).toList();
        if (!companies.isEmpty()) parts.add(new OrFilter(companies));

        var excluded = askList("Exclude statuses: ").stream()
                .map(s -> (ApplicationFilter) new StatusFilter(parseStatus(s))).toList();
        if (!excluded.isEmpty()) parts.add(new NotFilter(new OrFilter(excluded)));

        var filter = new AndFilter(parts);
        System.out.println("Filter: " + filter.describe());
        printer.printAll(service.find(filter));
    }
    private List<String> askList(String prompt) {
        return Arrays.stream(ask(prompt).split(","))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .toList();
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
        return parseStatus(ask("Status " + Arrays.toString(ApplicationStatus.values()) + ": "));
    }
    private ApplicationStatus parseStatus(String value) {
        try {
            return ApplicationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown status '" + value + "'");
        }
    }
}
