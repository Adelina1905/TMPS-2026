package org.example.Lab3;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.domain.StatusChange;
import org.example.Lab3.filters.ApplicationFilter;
import org.example.Lab3.notification.Notifier;
import org.example.Lab3.repository.ApplicationRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

// Proxy: the real subject. It does the actual work and knows nothing about roles.
public class ApplicationService implements ApplicationTracker {
    private final ApplicationRepository repository;
    private final Notifier notifier;

    public ApplicationService(ApplicationRepository repository, Notifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }
    @Override
    public JobApplication add(JobApplication.Builder builder){
        var app = builder.build();
        repository.save(app);
        return app;
    }
    @Override
    public void changeStatus(UUID id, ApplicationStatus next){
        var app = get(id);
        app.changeStatus(next);
        repository.save(app);
        notifier.notify(app.getCompany() + ": " + app.getPosition() + " is now " + next);
    }
    @Override
    public List<JobApplication> find(ApplicationFilter filter){
        return repository.findAll().stream().filter(filter::matches).toList();
    }
    @Override
    public List<JobApplication> findAll(){
        return repository.findAll();
    }
    @Override
    public List<StatusChange> history(UUID id){
        return get(id).getHistory();
    }
    @Override
    public void delete(UUID id){
        repository.delete(get(id).getId());
    }

    // Resolves a full id from its first few characters; the prefix must match exactly one application.
    @Override
    public UUID resolveId(String prefix){
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

    private JobApplication get(UUID id){
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No application with id " + id));
    }

    @Override
    public JobApplication reapply(UUID id) {
        var copy = get(id).copy();
        repository.save(copy);
        return copy;
    }
}
