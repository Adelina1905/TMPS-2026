package org.example.Lab4.events;

import org.example.Lab4.domain.ApplicationStatus;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

// Concrete observer: counts how many applications reached each status during this session.
public class StatisticsObserver implements ApplicationObserver {
    private final Map<ApplicationStatus, Integer> reached = new EnumMap<>(ApplicationStatus.class);
    private int added;

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.type()) {
            case ADDED, RESTORED -> added++;
            case STATUS_CHANGED -> reached.merge(event.to(), 1, Integer::sum);
            case STATUS_REVERTED -> reached.computeIfPresent(event.from(), (s, n) -> n > 1 ? n - 1 : null);
            case DELETED -> { }
        }
    }

    public int count(ApplicationStatus status) {
        return reached.getOrDefault(status, 0);
    }

    @Override
    public String name() {
        return "Statistics";
    }

    @Override
    public String summary() {
        if (reached.isEmpty()) return added + " added, no status changes yet";
        return added + " added, reached: " + reached.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
    }
}
