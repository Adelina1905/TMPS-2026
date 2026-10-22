package org.example.Lab2.filters;

import org.example.Lab2.domain.JobApplication;

public interface ApplicationFilter {
    boolean matches(JobApplication application);
}

