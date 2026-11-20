package org.example.Lab4.access;

import org.example.Lab4.ApplicationTracker;
import org.example.Lab4.domain.ApplicationStatus;
import org.example.Lab4.domain.JobApplication;
import org.example.Lab4.domain.StatusChange;
import org.example.Lab4.filters.ApplicationFilter;
import org.example.Lab4.notification.Notifier;

import java.util.List;
import java.util.UUID;

// Proxy (Lab 3): a protection proxy. It has the same interface as the real tracker and holds a reference to it.
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
    public void revertStatus(UUID id) {
        checkCanEdit("undo a status change");
        real.revertStatus(id);
    }

    @Override
    public JobApplication delete(UUID id) {
        checkCanEdit("delete an application");
        return real.delete(id);
    }

    @Override
    public void restore(JobApplication application) {
        checkCanEdit("restore an application");
        real.restore(application);
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
    public JobApplication get(UUID id) {
        return real.get(id);
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
