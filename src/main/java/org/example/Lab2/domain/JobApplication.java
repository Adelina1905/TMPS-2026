package org.example.Lab2.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobApplication {
    private final UUID id;
    private final Company company;
    private final String position;
    private final LocalDate appliedOn;
    private ApplicationStatus status;
    private final List<StatusChange> history = new ArrayList<>();

    public JobApplication(Company company, String position, LocalDate appliedOn) {
        this(UUID.randomUUID(), company, position, appliedOn, ApplicationStatus.APPLIED);
    }


    public JobApplication(UUID id, Company company, String position, LocalDate appliedOn, ApplicationStatus status) {
        this(id, company, position, appliedOn, status, List.of());
    }

    public JobApplication(UUID id, Company company, String position, LocalDate appliedOn,
                          ApplicationStatus status, List<StatusChange> history) {
        this.id = id;
        this.company = company;
        this.position = position;
        this.appliedOn = appliedOn;
        this.status = status;
        this.history.addAll(history);
    }
    public void changeStatus(ApplicationStatus next){
        if(!status.canMoveTo(next))
            throw new IllegalStateException(status + " -> " + next + " not allowed");
        history.add(new StatusChange(status, next, LocalDateTime.now()));
        status = next;
    }

    public UUID getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getAppliedOn() {
        return appliedOn;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public List<StatusChange> getHistory() {
        return List.copyOf(history);
    }
}
