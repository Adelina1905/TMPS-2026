package org.example.Lab3.repository;

// Factory Method creator: subclasses decide which ApplicationRepository gets created.
public abstract class RepositoryFactory {
    public abstract ApplicationRepository createRepository();

    public abstract String description();
}
