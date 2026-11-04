package org.example.Lab3.filters;

import org.example.Lab3.domain.JobApplication;

import java.util.List;
import java.util.stream.Collectors;

// Composite: matches when at least one child filter matches.
public class OrFilter implements ApplicationFilter {
    private final List<ApplicationFilter> children;

    public OrFilter(List<ApplicationFilter> children) {
        if (children.isEmpty()) throw new IllegalArgumentException("OR filter needs at least one filter");
        this.children = List.copyOf(children);
    }


    @Override
    public boolean matches(JobApplication application) {
        return children.stream().anyMatch(f -> f.matches(application));
    }

    @Override
    public String describe() {
        if (children.size() == 1) return children.getFirst().describe();
        return children.stream().map(ApplicationFilter::describe)
                .collect(Collectors.joining(" OR ", "(", ")"));
    }
}
