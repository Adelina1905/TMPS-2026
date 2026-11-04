package org.example.Lab3.importing.jobboard;

// Adapter: part of the adaptee. This is how the job board describes a saved job.
// Its field names, types and formats do not match JobApplication:
//   - savedOn is text in the board's own format "dd/MM/yyyy"
//   - salaryRange is text such as "3000-4000"
//   - stage uses the board's own words ("Saved", "Interviewing", "Not selected", ...)
public record JobPosting(String jobTitle, String employer, String city,
                         String salaryRange, String savedOn, String stage) {
}
