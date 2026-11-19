package org.example.Lab3.access;

import org.example.Lab3.ApplicationTracker;
import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.domain.StatusChange;
import org.example.Lab3.filters.ApplicationFilter;
import org.example.Lab3.notification.Notifier;

import java.util.List;
import java.util.UUID;

// Proxy: a protection proxy. It has the same interface as the real tracker and holds a reference to it.
// Reading methods are passed on unchanged. Changing methods are passed on only if the role may edit;
// otherwise the call is stopped before it reaches the real object, and the attempt is reported
// through the Notifier (the Decorator chain).
public class AccessControlProxy implements ApplicationTracker {
    private final ApplicationTracker real;
    private final Role role;
    private final Notifier notifier;

    public AccessControlProxy(ApplicationTracker real, Role role, Notifier notifier) {
        this.real = real;
        this.role = role;
        this.notifier = notifier;
    }

    private void checkCanEdit(String action) {
        if (!role.canEdit()) {
            notifier.notify("Access denied: " + role + " tried to " + action);
            throw new AccessDeniedException("A " + role + " cannot " + action);
        }
    }

    // ----- changing methods: checked first -----
    @Override
    public JobApplication add(JobApplication.Builder builder) {
        checkCanEdit("add an application");
        return real.add(builder);
    }

    @Override
    public void changeStatus(UUID id, ApplicationStatus next) {
        checkCanEdit("change a status");
        real.changeStatus(id, next);
    }

    @Override
    public void delete(UUID id) {
        checkCanEdit("delete an application");
        real.delete(id);
    }

    @Override
    public JobApplication reapply(UUID id) {
        checkCanEdit("reapply");
        return real.reapply(id);
    }

    // ----- reading methods: passed on unchanged -----
    @Override
    public List<JobApplication> find(ApplicationFilter filter) {
        return real.find(filter);
    }

    @Override
    public List<JobApplication> findAll() {
        return real.findAll();
    }

    @Override
    public List<StatusChange> history(UUID id) {
        return real.history(id);
    }

    @Override
    public UUID resolveId(String prefix) {
        return real.resolveId(prefix);
    }
}
