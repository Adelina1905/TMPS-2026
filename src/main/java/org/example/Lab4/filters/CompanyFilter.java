package org.example.Lab4.filters;

import org.example.Lab4.domain.JobApplication;

// Composite leaf: matches applications sent to one company (ignoring upper/lower case).
public class CompanyFilter implements ApplicationFilter {
    private final String name;

    public CompanyFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(JobApplication application){
        return application.getCompany().getName().equalsIgnoreCase(name);
    }

    @Override
    public String describe() {
        return "company = " + name;
    }
}
