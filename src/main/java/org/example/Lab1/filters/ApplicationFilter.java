package org.example.Lab1.filters;

import org.example.Lab1.domain.JobApplication;

public interface ApplicationFilter {
    boolean matches(JobApplication application);
}

