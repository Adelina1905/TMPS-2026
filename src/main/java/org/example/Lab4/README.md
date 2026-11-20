# Job Application Tracker — Laboratory Work 4 (Behavioral Design Patterns)

This lab continues the Job Application Tracker from Laboratory Works 1–3. It adds three behavioral patterns: **State** (the status workflow), **Command** (undo/redo for every change) and **Observer** (event subscribers that can be turned on and off at runtime). The Decorator and Proxy from Lab 3 and the Builder, Factory Method and Prototype from Lab 2 are still used.

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

New menu options in this lab:
- **10. Undo** / **11. Redo**: undo or redo the last add, status change, delete or reapply (Command).
- **12. Show command history**: list the commands that can be undone, newest first.
- **13. Manage observers**: turn the notification, next-step advisor and statistics observers on or off while the program runs (Observer).
- **6. Show status history** now also prints the current status and the next step suggested by the current State.

## Tests
All unit tests run with one command:
```bash
mvn test
```

The Lab 4 tests are in `src/test/java/org/example/Lab4/`.
