package org.example.Lab3;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.domain.StatusChange;
import org.example.Lab3.filters.ApplicationFilter;

import java.util.List;
import java.util.UUID;

// Proxy: the subject interface. Both the real ApplicationService and AccessControlProxy implement it,
// so ConsoleMenu works with either one without knowing which it has.
public interface ApplicationTracker {
    JobApplication add(JobApplication.Builder builder);
    void changeStatus(UUID id, ApplicationStatus next);
    List<JobApplication> find(ApplicationFilter filter);
    List<JobApplication> findAll();
    List<StatusChange> history(UUID id);
    void delete(UUID id);
    JobApplication reapply(UUID id);
    UUID resolveId(String prefix);
}
