package org.example.Lab4.commands;

import org.example.Lab4.ApplicationTracker;
import org.example.Lab4.domain.JobApplication;

// Concrete command: adds an application. Undo removes it; redo puts back the same object (same id).
public class AddApplicationCommand implements ApplicationCommand {
    private final ApplicationTracker tracker;
    private final JobApplication.Builder builder;
    private JobApplication added;

    public AddApplicationCommand(ApplicationTracker tracker, JobApplication.Builder builder) {
        this.tracker = tracker;
        this.builder = builder;
    }

    @Override
    public void execute() {
        if (added == null) added = tracker.add(builder);
        else tracker.restore(added);
    }

    @Override
    public void undo() {
        tracker.delete(added.getId());
    }

    @Override
    public String describe() {
        return added == null ? "Add application" : "Add " + added.getCompany() + ": " + added.getPosition();
    }

    public JobApplication added() {
        return added;
    }
}
