package org.example.Lab4.filters;

import org.example.Lab4.domain.JobApplication;

// Composite: the common interface for single filters (leaves) and combined filters (composites).
public interface ApplicationFilter {
    boolean matches(JobApplication application);
    String describe();
}
