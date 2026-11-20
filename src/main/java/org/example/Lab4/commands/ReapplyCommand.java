package org.example.Lab4.commands;

import org.example.Lab4.ApplicationTracker;
import org.example.Lab4.domain.JobApplication;

import java.util.UUID;

// Concrete command: creates a fresh copy of an application (Prototype). Undo removes the copy.
public class ReapplyCommand implements ApplicationCommand {
    private final ApplicationTracker tracker;
    private final UUID originalId;
    private JobApplication copy;

    public ReapplyCommand(ApplicationTracker tracker, UUID originalId) {
        this.tracker = tracker;
        this.originalId = originalId;
    }

    @Override
    public void execute() {
        if (copy == null) copy = tracker.reapply(originalId);
        else tracker.restore(copy);
    }

    @Override
    public void undo() {
        tracker.delete(copy.getId());
    }

    @Override
    public String describe() {
        return copy == null ? "Reapply" : "Reapply to " + copy.getCompany() + ": " + copy.getPosition();
    }

    public JobApplication copy() {
        return copy;
    }
}
