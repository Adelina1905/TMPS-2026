package org.example.Lab2.repository;

import org.example.Lab2.domain.JobApplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryApplicationRepository implements ApplicationRepository {
    private final Map<UUID, JobApplication> applications = new LinkedHashMap<>();

    @Override
    public void save(JobApplication application) {
        applications.put(application.getId(), application);
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
        applications.remove(id);
    }
}
