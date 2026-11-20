package org.example.Lab4;

import org.example.Lab4.domain.ApplicationStatus;
import org.example.Lab4.domain.JobApplication;
import org.example.Lab4.domain.StatusChange;
import org.example.Lab4.filters.ApplicationFilter;

import java.util.List;
import java.util.UUID;

// Command: the receiver interface. Commands call these methods; the business logic stays here.
// Proxy (Lab 3): both ApplicationService and AccessControlProxy implement it.
public interface ApplicationTracker {
    JobApplication add(JobApplication.Builder builder);

    void changeStatus(UUID id, ApplicationStatus next);

    // Undo of changeStatus: goes back to the status before the last change.
    void revertStatus(UUID id);

    // Removes the application and returns it, so the delete can be undone.
    JobApplication delete(UUID id);

    // Puts back an application that was removed (undo of delete, redo of add/reapply).
    void restore(JobApplication application);

    JobApplication reapply(UUID id);

    List<JobApplication> find(ApplicationFilter filter);

    List<JobApplication> findAll();

    JobApplication get(UUID id);

    List<StatusChange> history(UUID id);

    UUID resolveId(String prefix);
}
