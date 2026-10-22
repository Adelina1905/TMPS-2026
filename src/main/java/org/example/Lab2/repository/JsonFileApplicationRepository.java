package org.example.Lab2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Lab2.domain.ApplicationStatus;
import org.example.Lab2.domain.Company;
import org.example.Lab2.domain.JobApplication;
import org.example.Lab2.domain.StatusChange;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public class JsonFileApplicationRepository implements ApplicationRepository {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);
    private final Map<UUID, JobApplication> applications = new LinkedHashMap<>();

    public JsonFileApplicationRepository(Path file) {
        this.file = file;
        load();
    }

    @Override
    public void save(JobApplication application) {
        applications.put(application.getId(), application);
        write();
    }

    @Override
    public Optional<JobApplication> findById(UUID id) {
        return Optional.ofNullable(applications.get(id));
    }

    @Override
    public List<JobApplication> findAll() {
        return new ArrayList<>(applications.values());
    }

    @Override
    public void delete(UUID id) {
        if (applications.remove(id) != null) write();
    }

    private void load() {
        if (!Files.exists(file)) return;
        try {
            List<Record> records = mapper.readValue(file.toFile(), new TypeReference<>() {});
            records.forEach(r -> applications.put(r.id(), r.toDomain()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + file, e);
        }
    }

    private void write() {
        try {
            var records = applications.values().stream().map(Record::from).toList();
            mapper.writeValue(file.toFile(), records);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + file, e);
        }
    }

    // File format kept separate from the domain class so JobApplication stays free of Jackson annotations.
    private record Record(UUID id, String company, String position, LocalDate appliedOn,
                          ApplicationStatus status, List<StatusChange> history) {
        static Record from(JobApplication a) {
            return new Record(a.getId(), a.getCompany().getName(), a.getPosition(),
                    a.getAppliedOn(), a.getStatus(), a.getHistory());
        }

        JobApplication toDomain() {
            return new JobApplication(id, new Company(company), position, appliedOn, status,
                    history == null ? List.of() : history);
        }
    }
}
