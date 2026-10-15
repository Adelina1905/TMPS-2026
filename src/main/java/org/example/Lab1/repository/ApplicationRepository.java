package org.example.Lab1.repository;

import org.example.Lab1.domain.JobApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    void save(JobApplication application);
    Optional<JobApplication> findById(UUID id);
    List<JobApplication> findAll();
    void delete(UUID id);
}
