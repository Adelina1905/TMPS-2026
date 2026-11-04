package org.example.Lab3.domain;

import java.time.LocalDateTime;

public record StatusChange(ApplicationStatus from, ApplicationStatus to, LocalDateTime time) {
}
