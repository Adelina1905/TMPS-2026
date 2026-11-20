package org.example.Lab4.domain;

import org.example.Lab4.domain.state.ApplicationState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// State: the context. It keeps a reference to its current ApplicationState and lets that object
// decide which status changes are allowed. Builder and Prototype are reused from Lab 2.
public class JobApplication implements Prototype<JobApplication>{
    private final UUID id;
    private final Company company;
    private final String position;
    private final LocalDate appliedOn;
    private final String location;      // optional
    private final Integer salary;       // optional
    private final String notes;         // optional
    private ApplicationState state;
    private final List<StatusChange> history = new ArrayList<>();

    // Builder: the only way to create an application is JobApplication.builder(...).build().
    private JobApplication(Builder builder) {
        this.id = builder.id;
        this.company = builder.company;
        this.position = builder.position;
        this.appliedOn = builder.appliedOn;
        this.location = builder.location;
        this.salary = builder.salary;
        this.notes = builder.notes;
        this.state = builder.status.toState();
        this.history.addAll(builder.history);
    }

    // Required fields are taken here, so a builder can never be missing them.
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
        public Builder history(List<StatusChange> history) { this.history = history; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder salary(Integer salary) { this.salary = salary; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public JobApplication build() {
            if (company == null) throw new IllegalArgumentException("Company is required");
            if (position == null || position.isBlank()) throw new IllegalArgumentException("Position is required");
            if (id == null || appliedOn == null || status == null || history == null)
                throw new IllegalArgumentException("Id, date, status and history must not be null");
            if (salary != null && salary < 0) throw new IllegalArgumentException("Salary must not be negative");
            return new JobApplication(this);
        }
    }

    // State: the context delegates to its current state. If the state forbids the event it throws,
    // and nothing in the application changes.
    public void changeStatus(ApplicationStatus next){
        var from = state.status();
        state = next.reachFrom(state);
        history.add(new StatusChange(from, state.status(), LocalDateTime.now()));
    }

    // Used to undo a status change (Command): goes back to the state before the last change
    // and forgets that change. This is not a normal transition, so it does not ask the state.
    public void revertLastChange() {
        if (history.isEmpty()) throw new IllegalStateException("No status change to undo");
        var last = history.removeLast();
        state = last.from().toState();
    }

    public String nextStep() {
        return state.nextStep();
    }

    public boolean isClosed() {
        return state.isFinal();
    }

    @Override
    public JobApplication copy() {
        return builder(company, position)
                .location(location)
                .salary(salary)
                .notes(notes)
                .build();
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
        return state.status();
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
