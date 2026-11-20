package org.example.Lab4.events;

import org.example.Lab4.domain.ApplicationStatus;
import org.example.Lab4.domain.JobApplication;

// Observer: the data passed to every observer. from/to are only set for status events.
public record ApplicationEvent(EventType type, JobApplication application,
                               ApplicationStatus from, ApplicationStatus to) {

    public static ApplicationEvent of(EventType type, JobApplication application) {
        return new ApplicationEvent(type, application, null, null);
    }

    public static ApplicationEvent statusChanged(JobApplication application, ApplicationStatus from, ApplicationStatus to) {
        return new ApplicationEvent(EventType.STATUS_CHANGED, application, from, to);
    }

    public static ApplicationEvent statusReverted(JobApplication application, ApplicationStatus from, ApplicationStatus to) {
        return new ApplicationEvent(EventType.STATUS_REVERTED, application, from, to);
    }

    public String title() {
        return application.getCompany() + ": " + application.getPosition();
    }
}
