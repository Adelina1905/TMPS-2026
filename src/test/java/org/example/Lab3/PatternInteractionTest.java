package org.example.Lab3;

import org.example.Lab3.access.AccessControlProxy;
import org.example.Lab3.access.AccessDeniedException;
import org.example.Lab3.access.Role;
import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.Company;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.filters.AndFilter;
import org.example.Lab3.filters.CompanyFilter;
import org.example.Lab3.filters.NotFilter;
import org.example.Lab3.filters.StatusFilter;
import org.example.Lab3.notification.FileLogNotifier;
import org.example.Lab3.notification.Notifier;
import org.example.Lab3.notification.TimestampNotifier;
import org.example.Lab3.repository.InMemoryRepositoryFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// The same wiring as Main: Proxy -> ApplicationService -> Decorator chain, with Composite filters on top.
class PatternInteractionTest {

    private final List<String> messages = new ArrayList<>();

    private ApplicationTracker realService(Notifier notifier) {
        return new ApplicationService(new InMemoryRepositoryFactory().createRepository(), notifier);
    }

    @Test
    void editorChangeGoesThroughProxyAndIsReportedByDecorators(@TempDir Path dir) throws IOException {
        var log = dir.resolve("notifications.log");
        Notifier notifier = new TimestampNotifier(new FileLogNotifier(messages::add, log));
        ApplicationTracker tracker = new AccessControlProxy(realService(notifier), Role.EDITOR, notifier);

        var google = tracker.add(JobApplication.builder(new Company("Google"), "Backend Dev"));
        tracker.add(JobApplication.builder(new Company("Endava"), "Intern"));
        tracker.changeStatus(google.getId(), ApplicationStatus.REJECTED);
        var active = tracker.find(new AndFilter(new NotFilter(new StatusFilter(ApplicationStatus.REJECTED))));

        assertEquals(1, active.size());
        assertEquals("Endava", active.getFirst().getCompany().getName());
        assertTrue(messages.getFirst().matches("\\[.+] Google: Backend Dev is now REJECTED"), messages.getFirst());
        assertTrue(Files.readString(log).contains("Google: Backend Dev is now REJECTED"));
    }

    @Test
    void viewerIsDeniedThroughDecoratorsButCanStillUseCompositeFilters(@TempDir Path dir) throws IOException {
        var log = dir.resolve("notifications.log");
        Notifier notifier = new TimestampNotifier(new FileLogNotifier(messages::add, log));
        var real = realService(notifier);
        var google = real.add(JobApplication.builder(new Company("Google"), "Backend Dev"));
        ApplicationTracker viewer = new AccessControlProxy(real, Role.VIEWER, notifier);

        assertThrows(AccessDeniedException.class, () -> viewer.delete(google.getId()));
        var found = viewer.find(new AndFilter(new CompanyFilter("Google"), new StatusFilter(ApplicationStatus.APPLIED)));

        assertEquals(List.of(google), found);
        assertTrue(messages.getFirst().endsWith("Access denied: VIEWER tried to delete an application"));
        assertTrue(Files.readString(log).contains("Access denied"));
    }
}
