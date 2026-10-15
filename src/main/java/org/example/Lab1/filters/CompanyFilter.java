package org.example.Lab1.filters;

import org.example.Lab1.domain.JobApplication;

public class CompanyFilter implements ApplicationFilter {
    private final String name;

    public CompanyFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(JobApplication application){
        return application.getCompany().getName().equalsIgnoreCase(name);
    }
}
