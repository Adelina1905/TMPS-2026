package org.example.Lab3.repository;

import org.example.Lab3.domain.JobApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    void save(JobApplication application);
    Optional<JobApplication> findById(UUID id);
    List<JobApplication> findAll();
    void delete(UUID id);
}
