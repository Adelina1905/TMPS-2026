package org.example.Lab3.importing.jobboard;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Adapter: the adaptee. It plays the role of a third-party library that reads the CSV file
// a job board exports. We treat it as code we cannot change, and its interface
// (readPostings() returning JobPosting) does not fit our application.
//
// Expected header: Job Title,Employer,City,Salary Range,Date Saved,Stage
public class JobBoardExport {
    private static final int COLUMNS = 6;
    private final Path file;

    public JobBoardExport(Path file) {
        this.file = file;
    }

    public List<JobPosting> readPostings() {
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file, e);
        }
        var postings = new ArrayList<JobPosting>();
        for (int i = 1; i < lines.size(); i++) {   // line 0 is the header
            var line = lines.get(i);
            if (line.isBlank()) continue;
            var cells = splitCsvLine(line);
            if (cells.size() != COLUMNS)
                throw new IllegalArgumentException("Line " + (i + 1) + " of " + file + " has " + cells.size()
                        + " columns, expected " + COLUMNS);
            postings.add(new JobPosting(cells.get(0), cells.get(1), cells.get(2),
                    cells.get(3), cells.get(4), cells.get(5)));
        }
        return postings;
    }

    // Splits one CSV line; a value in double quotes may contain commas ("Acme, Inc.").
    private static List<String> splitCsvLine(String line) {
        var cells = new ArrayList<String>();
        var current = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') quoted = !quoted;
            else if (c == ',' && !quoted) {
                cells.add(current.toString().trim());
                current.setLength(0);
            } else current.append(c);
        }
        cells.add(current.toString().trim());
        return cells;
    }
}
