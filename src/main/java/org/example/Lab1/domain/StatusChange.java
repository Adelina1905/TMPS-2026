package org.example.Lab1.domain;

import java.time.LocalDateTime;

public record StatusChange(ApplicationStatus from, ApplicationStatus to, LocalDateTime time) {
}
