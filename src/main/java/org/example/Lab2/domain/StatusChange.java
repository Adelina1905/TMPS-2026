package org.example.Lab2.domain;

import java.time.LocalDateTime;

public record StatusChange(ApplicationStatus from, ApplicationStatus to, LocalDateTime time) {
}
