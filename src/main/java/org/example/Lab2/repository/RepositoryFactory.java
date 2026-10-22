package org.example.Lab2.repository;

// Factory Method creator: subclasses decide which ApplicationRepository gets created.
public abstract class RepositoryFactory {
    public abstract ApplicationRepository createRepository();

    public abstract String description();
}
