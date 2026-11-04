package org.example.Lab3.importing;

import org.example.Lab3.domain.JobApplication;

import java.util.List;

// Adapter: the target interface. This is what our application expects from any source of
// applications to import. ApplicationService only knows this interface.
public interface ApplicationSource {
    List<JobApplication> fetch();
    String description();
}
