package org.example.Lab2;

import org.example.Lab2.domain.JobApplication;
import org.example.Lab2.domain.StatusChange;

import java.util.ArrayList;
import java.util.List;

public class ApplicationPrinter {
    private static final int SHORT_ID_LENGTH = 8;

    public void print(JobApplication application) {
        System.out.printf("%-10s %-15s %-20s %-10s %s%n",
                application.getId().toString().substring(0, SHORT_ID_LENGTH), application.getCompany().getName(),
                application.getPosition(), application.getStatus(), application.getAppliedOn());
        printOptionalDetails(application);
    }
    private void printOptionalDetails(JobApplication application) {
        var details = new ArrayList<String>();
        if (application.getLocation() != null) details.add("Location: " + application.getLocation());
        if (application.getSalary() != null) details.add("Salary: " + application.getSalary());
        if (application.getNotes() != null) details.add("Notes: " + application.getNotes());
        if (!details.isEmpty()) System.out.println("           " + String.join(" | ", details));
    }
    public void printAll(List<JobApplication> apps) {
        if (apps.isEmpty()) {
            System.out.println("No applications found.");
            return;
        }
        System.out.printf("%-10s %-15s %-20s %-10s %s%n", "ID", "COMPANY", "POSITION", "STATUS", "APPLIED ON");
        apps.forEach(this::print);
    }
    public void printHistory(List<StatusChange> history) {
        if (history.isEmpty()) {
            System.out.println("No status changes yet.");
            return;
        }
        history.forEach(c -> System.out.printf("%s  %s -> %s%n", c.time().withNano(0), c.from(), c.to()));
    }
}
