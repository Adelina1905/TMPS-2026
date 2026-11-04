package org.example.Lab3.filters;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.JobApplication;

// Composite leaf: matches applications with one status.
public class StatusFilter implements ApplicationFilter {
    private final ApplicationStatus status;
    public StatusFilter(ApplicationStatus status){
        this.status = status;
    }
    @Override
    public boolean matches(JobApplication a){
        return a.getStatus() == status;
    }

    @Override
    public String describe() {
        return "status = " + status;
    }
}
