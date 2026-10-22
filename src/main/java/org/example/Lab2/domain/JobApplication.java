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
    private final String location;      // optional
    private final Integer salary;       // optional
    private final String notes;
    private ApplicationStatus status;
    private final List<StatusChange> history = new ArrayList<>();

//    public JobApplication(Company company, String position, LocalDate appliedOn) {
//        this(UUID.randomUUID(), company, position, appliedOn, ApplicationStatus.APPLIED);
//    }
//
//
//    public JobApplication(UUID id, Company company, String position, LocalDate appliedOn, ApplicationStatus status) {
//        this(id, company, position, appliedOn, status, List.of());
//    }
//
//    public JobApplication(UUID id, Company company, String position, LocalDate appliedOn,
//                          ApplicationStatus status, List<StatusChange> history) {
//        this.id = id;
//        this.company = company;
//        this.position = position;
//        this.appliedOn = appliedOn;
//        this.status = status;
//        this.history.addAll(history);
//    }

    private JobApplication(Builder builder) {
        this.id = builder.id;
        this.company = builder.company;
        this.position = builder.position;
        this.appliedOn = builder.appliedOn;
        this.location = builder.location;
        this.salary = builder.salary;
        this.notes = builder.notes;
        this.status = builder.status;
        this.history.addAll(builder.history);
    }
    public static Builder builder(Company company, String position) {
        return new Builder(company, position);
    }

    public static class Builder {
        private final Company company;
        private final String position;
        private UUID id = UUID.randomUUID();
        private LocalDate appliedOn = LocalDate.now();
        private ApplicationStatus status = ApplicationStatus.APPLIED;
        private List<StatusChange> history = List.of();
        private String location;
        private Integer salary;
        private String notes;

        private Builder(Company company, String position) {
            this.company = company;
            this.position = position;
        }

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder appliedOn(LocalDate appliedOn) { this.appliedOn = appliedOn; return this; }
        public Builder status(ApplicationStatus status) { this.status = status; return this; }
        public Builder history(List<StatusChange> history) { this.history = history; return this;}
            public Builder location(String location) { this.location = location; return this; }
            public Builder salary(Integer salary) { this.salary = salary; return this; }
            public Builder notes(String notes) { this.notes = notes; return this; }

            public JobApplication build() {
                if (company == null) throw new IllegalArgumentException("Company is required");
                if (position == null || position.isBlank()) throw new IllegalArgumentException("Position is required");
                if (salary != null && salary < 0) throw new IllegalArgumentException("Salary must not be negative");
                return new JobApplication(this);
            }

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

    public String getLocation() {
        return location;
    }

    public Integer getSalary() {
        return salary;
    }

    public String getNotes() {
        return notes;
    }

    public List<StatusChange> getHistory() {
        return List.copyOf(history);
    }
}
