package org.example.Lab2.filters;

import org.example.Lab2.domain.JobApplication;

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
