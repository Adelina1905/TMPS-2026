package org.example.Lab2.repository;

import java.nio.file.Path;

public class JsonRepositoryFactory extends RepositoryFactory {
    private final Path file;

    public JsonRepositoryFactory(Path file) {
        this.file = file;
    }

    @Override
    public ApplicationRepository createRepository() {
        return new JsonFileApplicationRepository(file);
    }

    @Override
    public String description() {
        return "JSON file (" + file + ")";
    }
}
