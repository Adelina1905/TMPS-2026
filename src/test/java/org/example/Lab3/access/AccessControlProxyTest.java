package org.example.Lab3.access;

import org.example.Lab3.ApplicationService;
import org.example.Lab3.ApplicationTracker;
import org.example.Lab3.domain.ApplicationStatus;
import org.example.Lab3.domain.Company;
import org.example.Lab3.domain.JobApplication;
import org.example.Lab3.filters.CompanyFilter;
import org.example.Lab3.repository.InMemoryRepositoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessControlProxyTest {

    private ApplicationTracker real;
    private JobApplication google;
    private final List<String> messages = new ArrayList<>();

    @BeforeEach
    void setUp() {
        real = new ApplicationService(new InMemoryRepositoryFactory().createRepository(), m -> { });
        google = real.add(JobApplication.builder(new Company("Google"), "Backend Dev"));
    }

    @Test
    void viewerIsBlockedFromEveryChangeAndRealObjectStaysUntouched() {
        ApplicationTracker viewer = new AccessControlProxy(real, Role.VIEWER, messages::add);

        assertThrows(AccessDeniedException.class,
                () -> viewer.add(JobApplication.builder(new Company("Endava"), "Intern")));
        assertThrows(AccessDeniedException.class,
                () -> viewer.changeStatus(google.getId(), ApplicationStatus.INTERVIEW));
        assertThrows(AccessDeniedException.class, () -> viewer.delete(google.getId()));
        assertThrows(AccessDeniedException.class, () -> viewer.reapply(google.getId()));

        // The calls never reached the real service.
        assertEquals(List.of(google), real.findAll());
        assertEquals(ApplicationStatus.APPLIED, google.getStatus());
        assertEquals(4, messages.size());
        assertEquals("Access denied: VIEWER tried to change a status", messages.get(1));
    }

    @Test
    void viewerCanStillReadAndGetsSameResultsAsRealService() {
        ApplicationTracker viewer = new AccessControlProxy(real, Role.VIEWER, messages::add);

        assertEquals(real.findAll(), viewer.findAll());
        assertEquals(real.find(new CompanyFilter("google")), viewer.find(new CompanyFilter("google")));
        assertEquals(google.getId(), viewer.resolveId(google.getId().toString().substring(0, 4)));
        assertEquals(real.history(google.getId()), viewer.history(google.getId()));
        assertTrue(messages.isEmpty());
    }

    @Test
    void editorCallsArePassedThroughUnchanged() {
        ApplicationTracker editor = new AccessControlProxy(real, Role.EDITOR, messages::add);

        editor.changeStatus(google.getId(), ApplicationStatus.INTERVIEW);
        var copy = editor.reapply(google.getId());

        assertEquals(ApplicationStatus.INTERVIEW, google.getStatus());
        assertEquals(2, real.findAll().size());
        assertTrue(real.findAll().contains(copy));
        assertTrue(messages.isEmpty());
    }
}
