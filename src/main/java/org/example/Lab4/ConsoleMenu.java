package org.example.Lab4;

import org.example.Lab4.commands.AddApplicationCommand;
import org.example.Lab4.commands.ApplicationCommand;
import org.example.Lab4.commands.ChangeStatusCommand;
import org.example.Lab4.commands.CommandHistory;
import org.example.Lab4.commands.DeleteApplicationCommand;
import org.example.Lab4.commands.ReapplyCommand;
import org.example.Lab4.domain.ApplicationStatus;
import org.example.Lab4.domain.Company;
import org.example.Lab4.domain.JobApplication;
import org.example.Lab4.events.ApplicationObserver;
import org.example.Lab4.events.ApplicationSubject;
import org.example.Lab4.filters.AndFilter;
import org.example.Lab4.filters.ApplicationFilter;
import org.example.Lab4.filters.CompanyFilter;
import org.example.Lab4.filters.NotFilter;
import org.example.Lab4.filters.OrFilter;
import org.example.Lab4.filters.StatusFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

// The client. Reading goes straight to the tracker; every change is wrapped in a command and run
// through the CommandHistory (invoker), so it can be undone. The menu only knows interfaces:
// ApplicationTracker (maybe the Proxy), ApplicationCommand, ApplicationSubject and ApplicationObserver.
public class ConsoleMenu {
    private final ApplicationTracker service;
    private final CommandHistory commands;
    private final ApplicationSubject events;
    private final List<ApplicationObserver> observers;
    private final ApplicationPrinter printer;
    private final Scanner in = new Scanner(System.in);

    public ConsoleMenu(ApplicationTracker service, CommandHistory commands, ApplicationSubject events,
                       List<ApplicationObserver> observers, ApplicationPrinter printer) {
        this.service = service;
        this.commands = commands;
        this.events = events;
        this.observers = List.copyOf(observers);
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
                10. Undo
                11. Redo
                12. Show command history
                13. Manage observers
                0. Exit""");
            if (!in.hasNextLine()) return;
            try {
                switch (in.nextLine().trim()) {
                    case "1" -> add();
                    case "2" -> printer.printAll(service.findAll());
                    case "3" -> changeStatus();
                    case "4" -> printer.printAll(service.find(new StatusFilter(askStatus())));
                    case "5" -> printer.printAll(service.find(new CompanyFilter(ask("Company: "))));
                    case "6" -> showHistory();
                    case "7" -> delete();
                    case "8" -> reapply();
                    case "9" -> advancedFilter();
                    case "10" -> System.out.println("Undone: " + commands.undo().describe());
                    case "11" -> System.out.println("Redone: " + commands.redo().describe());
                    case "12" -> showCommandHistory();
                    case "13" -> manageObservers();
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
        var command = new AddApplicationCommand(service, builder);
        run(command);
        System.out.println("Added:");
        printer.print(command.added());
    }
    private void changeStatus() {
        var id = askId();
        var next = askStatus();
        run(new ChangeStatusCommand(service, id, next));
    }
    private void delete() {
        run(new DeleteApplicationCommand(service, askId()));
        System.out.println("Deleted (option 10 undoes it).");
    }
    private void reapply() {
        var command = new ReapplyCommand(service, askId());
        run(command);
        System.out.println("Created a copy:");
        printer.print(command.copy());
    }
    // Command: every change goes through the invoker, so it is recorded and can be undone.
    private void run(ApplicationCommand command) {
        commands.run(command);
    }
    private void showHistory() {
        var app = service.get(askId());
        printer.printHistory(app.getHistory());
        System.out.println("Current status: " + app.getStatus() + (app.isClosed() ? " (closed)" : ""));
        System.out.println("Next step: " + app.nextStep());
    }
    private void showCommandHistory() {
        var done = commands.descriptions();
        if (done.isEmpty()) {
            System.out.println("No commands yet.");
            return;
        }
        System.out.println("Most recent first:");
        done.forEach(d -> System.out.println("  - " + d));
    }
    // Observer: subscribe and unsubscribe observers while the program is running.
    private void manageObservers() {
        for (int i = 0; i < observers.size(); i++) {
            var o = observers.get(i);
            System.out.printf("%d. [%s] %-18s %s%n", i + 1, events.isSubscribed(o) ? "on " : "off",
                    o.name(), o.summary());
        }
        var answer = ask("Number to turn on/off (empty to go back): ");
        if (answer.isEmpty()) return;
        int index;
        try {
            index = Integer.parseInt(answer) - 1;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number: '" + answer + "'");
        }
        if (index < 0 || index >= observers.size()) throw new IllegalArgumentException("No observer " + answer);
        var observer = observers.get(index);
        if (events.isSubscribed(observer)) {
            events.unsubscribe(observer);
            System.out.println(observer.name() + " unsubscribed.");
        } else {
            events.subscribe(observer);
            System.out.println(observer.name() + " subscribed.");
        }
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
