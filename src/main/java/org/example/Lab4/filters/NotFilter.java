package org.example.Lab4.filters;

import org.example.Lab4.domain.JobApplication;

// Composite: matches when the wrapped filter does not match.
public class NotFilter implements ApplicationFilter {
    private final ApplicationFilter child;

    public NotFilter(ApplicationFilter child) {
        this.child = child;
    }

    @Override
    public boolean matches(JobApplication application) {
        return !child.matches(application);
    }

    @Override
    public String describe() {
        return "NOT " + child.describe();
    }
}
