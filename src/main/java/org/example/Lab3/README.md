# Job Application Tracker — Laboratory Work 3 (Structural Design Patterns)

This lab continues the Job Application Tracker from Laboratory Works 1 and 2. It adds three structural patterns: **Composite** (advanced filters), **Decorator** (notifications) and **Proxy** (read-only viewer mode).

The full report, with class diagrams, code snippets and test results, is in [REPORT.md](REPORT.md).

## Requirements
- **Java 25**
- **Maven 3.9+**

## Build
```bash
mvn compile
```

## Run
```bash
mvn exec:java -Dexec.mainClass=org.example.Main                             # JSON storage, editor (default)
mvn exec:java -Dexec.mainClass=org.example.Main -Dexec.args="memory"        # in-memory storage
mvn exec:java -Dexec.mainClass=org.example.Main -Dexec.args="viewer"        # read-only (Proxy)
mvn exec:java -Dexec.mainClass=org.example.Main -Dexec.args="memory viewer" # both
```

You can also run `org.example.Main` directly from IntelliJ IDEA. To pass the options, write `memory` and/or `viewer` in *Run → Edit Configurations → Program arguments*.

In `viewer` mode you can list and filter applications, but adding, changing the status, deleting and reapplying are blocked.

## Tests
All unit tests run with one command:
```bash
mvn test
```

Tests are in `src/test/java/org/example/Lab3/`.
