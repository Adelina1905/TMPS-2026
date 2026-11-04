package org.example.Lab3.importing;

import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.Company;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.importing.jobboard.JobBoardExport;
import org.example.Lab3.importing.jobboard.JobPosting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

// Adapter: implements our ApplicationSource (target) and wraps a JobBoardExport (adaptee).
// It translates every JobPosting into a JobApplication, using the Builder from Lab 2.
public class JobBoardAdapter implements ApplicationSource {
    private static final DateTimeFormatter BOARD_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final JobBoardExport export;

    public JobBoardAdapter(JobBoardExport export) {
        this.export = export;
    }

    @Override
    public List<JobApplication> fetch() {
        return export.readPostings().stream().map(this::toApplication).toList();
    }

    @Override
    public String description() {
        return "job board CSV export";
    }

    private JobApplication toApplication(JobPosting posting) {
        var builder = JobApplication.builder(new Company(posting.employer()), posting.jobTitle())
                .appliedOn(parseDate(posting.savedOn()))
                .status(toStatus(posting.stage()))
                .notes("Imported from job board");
        if (!posting.city().isBlank()) builder.location(posting.city());
        if (!posting.salaryRange().isBlank()) {
            builder.salary(lowerBound(posting.salaryRange()));
            builder.notes("Imported from job board, salary range " + posting.salaryRange());
        }
        return builder.build();
    }

    // "Saved", "Interviewing", ... -> our ApplicationStatus values.
    private static ApplicationStatus toStatus(String stage) {
        return switch (stage.trim().toLowerCase()) {
            case "saved" -> ApplicationStatus.WISHLIST;
            case "applied" -> ApplicationStatus.APPLIED;
            case "interviewing" -> ApplicationStatus.INTERVIEW;
            case "offer" -> ApplicationStatus.OFFER;
            case "not selected" -> ApplicationStatus.REJECTED;
            case "withdrawn" -> ApplicationStatus.WITHDRAWN;
            default -> throw new IllegalArgumentException("Unknown job board stage '" + stage + "'");
        };
    }

    // "dd/MM/yyyy" -> LocalDate.
    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), BOARD_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date '" + value + "' is not in the format dd/MM/yyyy");
        }
    }

    // "3000-4000" -> 3000. A single number such as "3500" is also accepted.
    private static int lowerBound(String range) {
        var first = range.split("-")[0].trim();
        try {
            return Integer.parseInt(first);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Salary range '" + range + "' is not a number range");
        }
    }
}
