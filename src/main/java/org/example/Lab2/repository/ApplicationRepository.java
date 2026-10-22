package org.example.Lab2.repository;

import org.example.Lab2.domain.JobApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    void save(JobApplication application);
    Optional<JobApplication> findById(UUID id);
    List<JobApplication> findAll();
    void delete(UUID id);
}
