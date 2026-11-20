package org.example.Lab4.commands;

import org.example.Lab4.ApplicationTracker;
import org.example.Lab4.domain.JobApplication;

import java.util.UUID;

// Concrete command: deletes an application and keeps it, so undo can put it back with its full history.
public class DeleteApplicationCommand implements ApplicationCommand {
    private final ApplicationTracker tracker;
    private final UUID id;
    private JobApplication deleted;

    public DeleteApplicationCommand(ApplicationTracker tracker, UUID id) {
        this.tracker = tracker;
        this.id = id;
    }

    @Override
    public void execute() {
        deleted = tracker.delete(id);
    }

    @Override
    public void undo() {
        tracker.restore(deleted);
    }

    @Override
    public String describe() {
        return deleted == null ? "Delete application" : "Delete " + deleted.getCompany() + ": " + deleted.getPosition();
    }
}
