package org.example.Lab2.filters;

import org.example.Lab2.domain.ApplicationStatus;
import org.example.Lab2.domain.JobApplication;

public class StatusFilter implements ApplicationFilter {
    private final ApplicationStatus status;
    public StatusFilter(ApplicationStatus status){
        this.status = status;
    }
    @Override
    public boolean matches(JobApplication a){
        return a.getStatus() == status;
    }
}
