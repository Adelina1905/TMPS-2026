package org.example.Lab1.repository;

import org.example.Lab2.domain.JobApplication;
import org.example.Lab2.repository.ApplicationRepository;

import java.util.*;

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
