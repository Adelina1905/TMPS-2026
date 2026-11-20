package org.example.Lab4;

import org.example.Lab4.domain.ApplicationStatus;
import org.example.Lab4.domain.JobApplication;
import org.example.Lab4.domain.StatusChange;
import org.example.Lab4.events.ApplicationEvent;
import org.example.Lab4.events.ApplicationSubject;
import org.example.Lab4.events.EventType;
import org.example.Lab4.filters.ApplicationFilter;
import org.example.Lab4.repository.ApplicationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

// Command: the receiver, it does the real work. Observer: every change is published to the subject,
// so the service does not know who is listening.
public class ApplicationService implements ApplicationTracker {
    private final ApplicationRepository repository;
    private final ApplicationSubject events;

    public ApplicationService(ApplicationRepository repository, ApplicationSubject events) {
        this.repository = repository;
        this.events = events;
    }

    @Override
    public JobApplication add(JobApplication.Builder builder) {
        var app = builder.build();
        repository.save(app);
        events.publish(ApplicationEvent.of(EventType.ADDED, app));
        return app;
    }

    @Override
    public void changeStatus(UUID id, ApplicationStatus next) {
        var app = get(id);
        var from = app.getStatus();
        app.changeStatus(next);              // State: the application's current state decides
        repository.save(app);
        events.publish(ApplicationEvent.statusChanged(app, from, app.getStatus()));
    }

    @Override
    public void revertStatus(UUID id) {
        var app = get(id);
        var from = app.getStatus();
        app.revertLastChange();
        repository.save(app);
        events.publish(ApplicationEvent.statusReverted(app, from, app.getStatus()));
    }

    @Override
    public JobApplication delete(UUID id) {
        var app = get(id);
        repository.delete(app.getId());
        events.publish(ApplicationEvent.of(EventType.DELETED, app));
        return app;
    }

    @Override
    public void restore(JobApplication application) {
        if (repository.findById(application.getId()).isPresent())
            throw new IllegalStateException("Application " + application.getId() + " already exists");
        repository.save(application);
        events.publish(ApplicationEvent.of(EventType.RESTORED, application));
    }

    @Override
    public JobApplication reapply(UUID id) {
        var copy = get(id).copy();           // Prototype (Lab 2)
        repository.save(copy);
        events.publish(ApplicationEvent.of(EventType.ADDED, copy));
        return copy;
    }

    @Override
    public List<JobApplication> find(ApplicationFilter filter) {
        return repository.findAll().stream().filter(filter::matches).toList();
    }

    @Override
    public List<JobApplication> findAll() {
        return repository.findAll();
    }

    @Override
    public JobApplication get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No application with id " + id));
    }

    @Override
    public List<StatusChange> history(UUID id) {
        return get(id).getHistory();
    }

    // Resolves a full id from its first few characters; the prefix must match exactly one application.
    @Override
    public UUID resolveId(String prefix) {
        var key = prefix.trim().toLowerCase();
        if (key.isEmpty()) throw new IllegalArgumentException("Id must not be empty");
        var matches = repository.findAll().stream()
                .map(JobApplication::getId)
                .filter(id -> id.toString().startsWith(key))
                .toList();
        if (matches.isEmpty()) throw new NoSuchElementException("No application with id starting with '" + key + "'");
        if (matches.size() > 1) throw new IllegalArgumentException("Id '" + key + "' is ambiguous, type more characters");
        return matches.getFirst();
    }
}
