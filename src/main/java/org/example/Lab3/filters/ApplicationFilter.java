package org.example.Lab3.filters;

import org.example.Lab3.domain.JobApplication;

public interface ApplicationFilter {
    boolean matches(JobApplication application);
}

