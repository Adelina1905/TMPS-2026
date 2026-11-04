package org.example.Lab3.filters;

import org.example.Lab3.domain.JobApplication;

// Composite: the common interface for single filters (leaves) and combined filters (composites).
public interface ApplicationFilter {
    boolean matches(JobApplication application);
    String describe();
}
