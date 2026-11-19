package org.example.Lab3.filters;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.Company;
import org.example.Lab3.domain.JobApplication;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFilterTest {

    private static JobApplication app(String company, ApplicationStatus status) {
        return JobApplication.builder(new Company(company), "Developer").status(status).build();
    }

    private final JobApplication googleApplied = app("Google", ApplicationStatus.APPLIED);
    private final JobApplication googleRejected = app("Google", ApplicationStatus.REJECTED);
    private final JobApplication endavaInterview = app("Endava", ApplicationStatus.INTERVIEW);
    private final List<JobApplication> all = List.of(googleApplied, googleRejected, endavaInterview);

    private static List<JobApplication> apply(ApplicationFilter filter, List<JobApplication> apps) {
        return apps.stream().filter(filter::matches).toList();
    }

    @Test
    void leafAndGroupAreUsedThroughTheSameInterface() {
        // The client code (apply) only knows ApplicationFilter, it never checks the concrete type.
        ApplicationFilter leaf = new CompanyFilter("google");
        ApplicationFilter group = new AndFilter(new CompanyFilter("google"));

        assertEquals(List.of(googleApplied, googleRejected), apply(leaf, all));
        assertEquals(apply(leaf, all), apply(group, all));
        assertEquals("company = google", group.describe());
    }

    @Test
    void nestedGroupsCombineTheirChildren() {
        // (status = APPLIED OR status = INTERVIEW) AND NOT company = Endava
        ApplicationFilter filter = new AndFilter(
                new OrFilter(List.of(new StatusFilter(ApplicationStatus.APPLIED),
                                     new StatusFilter(ApplicationStatus.INTERVIEW))),
                new NotFilter(new CompanyFilter("Endava")));

        assertEquals(List.of(googleApplied), apply(filter, all));
        assertEquals("((status = APPLIED OR status = INTERVIEW) AND NOT company = Endava)", filter.describe());
    }

    @Test
    void emptyAndGroupMatchesEverything() {
        assertEquals(all, apply(new AndFilter(List.of()), all));
    }

    @Test
    void newLeafCanBeAddedWithoutChangingExistingClasses() {
        // Open/closed: a filter written only here, as an anonymous class, plugs into the existing groups.
        ApplicationFilter rejected = new ApplicationFilter() {
            public boolean matches(JobApplication a) { return a.getStatus() == ApplicationStatus.REJECTED; }
            public String describe() { return "rejected"; }
        };
        assertEquals(List.of(googleApplied, endavaInterview), apply(new NotFilter(rejected), all));
    }
}
