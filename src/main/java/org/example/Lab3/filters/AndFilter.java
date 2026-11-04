package org.example.Lab3.filters;

import org.example.Lab3.domain.JobApplication;

import java.util.List;
import java.util.stream.Collectors;

// Composite: matches when every child filter matches. With no children it matches everything.
public class AndFilter implements ApplicationFilter {
    private final List<ApplicationFilter> children;

    public AndFilter(List<ApplicationFilter> children) {
        this.children = List.copyOf(children);
    }

    public AndFilter(ApplicationFilter... children) {
        this(List.of(children));
    }

    @Override
    public boolean matches(JobApplication application) {
        return children.stream().allMatch(f -> f.matches(application));
    }

    @Override
    public String describe() {
        if (children.isEmpty()) return "everything";
        if (children.size() == 1) return children.getFirst().describe();
        return children.stream().map(ApplicationFilter::describe)
                .collect(Collectors.joining(" AND ", "(", ")"));
    }
}
