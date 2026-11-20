package org.example.Lab4.commands;

import org.example.Lab4.ApplicationTracker;
import org.example.Lab4.domain.ApplicationStatus;

import java.util.UUID;

// Concrete command: changes the status. Undo asks the receiver to go back to the previous status.
public class ChangeStatusCommand implements ApplicationCommand {
    private final ApplicationTracker tracker;
    private final UUID id;
    private final ApplicationStatus next;
    private ApplicationStatus previous;
    private String title = "application";

    public ChangeStatusCommand(ApplicationTracker tracker, UUID id, ApplicationStatus next) {
        this.tracker = tracker;
        this.id = id;
        this.next = next;
    }

    @Override
    public void execute() {
        var app = tracker.get(id);
        previous = app.getStatus();
        title = app.getCompany() + ": " + app.getPosition();
        tracker.changeStatus(id, next);
    }

    @Override
    public void undo() {
        tracker.revertStatus(id);
    }

    @Override
    public String describe() {
        return "Change " + title + " " + previous + " -> " + next;
    }
}
