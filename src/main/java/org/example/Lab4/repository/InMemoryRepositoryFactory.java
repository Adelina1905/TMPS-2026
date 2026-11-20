package org.example.Lab4.repository;

public class InMemoryRepositoryFactory extends RepositoryFactory {
    @Override
    public ApplicationRepository createRepository() {
        return new InMemoryApplicationRepository();
    }

    @Override
    public String description() {
        return "in memory (data is lost on exit)";
    }
}
