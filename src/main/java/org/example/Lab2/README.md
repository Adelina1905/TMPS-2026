# Job Application Tracker

This project shows three SOLID principles in a simple console application for tracking job applications. You can add the applications you sent, move them through statuses (Wishlist → Applied → Interview → Offer / Rejected / Withdrawn), filter them, view their status history, and keep them in a JSON file between runs. The design follows the Single Responsibility Principle (SRP), the Open/Closed Principle (OCP) and the Dependency Inversion Principle (DIP).

**Job Application Tracker - SOLID Principles Implementation**
Laboratory Work 1

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [SOLID Principles Applied](#solid-principles-applied)
  - [Single Responsibility Principle (SRP)](#single-responsibility-principle-srp)
  - [Open/Closed Principle (OCP)](#openclosed-principle-ocp)
  - [Dependency Inversion Principle (DIP)](#dependency-inversion-principle-dip)
- [Project Structure](#project-structure)
  - [Directory Breakdown](#directory-breakdown)
- [Usage](#usage)
  - [Running the Application](#running-the-application)
  - [Example Output](#example-output)
- [Conclusion](#conclusion)
- [Author](#author)

## Introduction
The Job Application Tracker was built for Laboratory Work 1. It shows how three SOLID principles apply in a small but realistic program: the Single Responsibility Principle (SRP), the Open/Closed Principle (OCP) and the Dependency Inversion Principle (DIP). The application has a console menu, enforces business rules on status changes, and can store data either in memory or in a JSON file.

## Features
- **Add Applications**: Record the company, the position and the date you applied.
- **Status Workflow**: Change an application's status. Only valid changes are allowed (for example, `APPLIED → INTERVIEW` works, `INTERVIEW → WISHLIST` does not).
- **Status History**: Every status change is saved with a timestamp.
- **Filtering**: Filter applications by status or by company (ignoring upper/lower case).
- **Short IDs**: Refer to an application by the first few characters of its ID (for example `9897`).
- **Notifications**: A message is shown every time a status changes.
- **Persistence**: Applications are saved to `applications.json` with Jackson and loaded again on the next run.
- **Swappable Storage**: Switch between in-memory and JSON storage by changing one line in `Main`.
- **Error Handling**: Invalid input (unknown ID, unknown status, forbidden status change) shows an error message instead of crashing the program.

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- **Definition**: A class should have only one reason to change, meaning it should have only one job or responsibility.
- **Implementation in the Project**:
  - **JobApplication / ApplicationStatus**: Hold only the business data and rules, such as which status changes are allowed.
  - **ApplicationService**: Only runs the use cases (add, change status, find, delete). It never reads input or prints output.
  - **ApplicationPrinter**: Only formats and prints applications and their history.
  - **ConsoleMenu**: Only talks to the user: it reads input and calls the service and the printer.
  - **Repositories**: Only store and load applications. The JSON file format is a private record inside `JsonFileApplicationRepository`, so the domain classes contain no serialization code.
  - **ConsoleNotifier**: Only sends notifications.

```java
// Single Responsibility: the printer only knows how to display applications.
public class ApplicationPrinter {
    public void print(JobApplication application) {
        System.out.printf("%-10s %-15s %-20s %-10s %s%n",
                application.getId().toString().substring(0, SHORT_ID_LENGTH), application.getCompany().getName(),
                application.getPosition(), application.getStatus(), application.getAppliedOn());
    }
}
```

```java
// Single Responsibility: the status enum alone decides which changes are allowed.
public boolean canMoveTo(ApplicationStatus next){
    return switch (this) {
        case WISHLIST  -> next == APPLIED || next == WITHDRAWN;
        case APPLIED   -> next == INTERVIEW || next == REJECTED || next == WITHDRAWN;
        case INTERVIEW -> next == OFFER || next == REJECTED || next == WITHDRAWN;
        default        -> false;
    };
}
```

### Open/Closed Principle (OCP)
- **Definition**: Software entities (classes, modules, functions, etc.) should be open for extension but closed for modification.
- **Implementation in the Project**:
  - **ApplicationFilter Interface**: Defines one method, `matches`. `ApplicationService.find()` accepts any filter, so you can add a new search criterion (by date, by position, etc.) by writing a new class, without changing the service.
  - **Notifier Interface**: You can add new notification channels (email, log file) without changing `ApplicationService`.
  - **ApplicationRepository Interface**: JSON storage was added without changing any existing class that uses a repository.

```java
// Open for extension: a new filter is just a new class implementing ApplicationFilter.
public class CompanyFilter implements ApplicationFilter {
    private final String name;

    public CompanyFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(JobApplication application){
        return application.getCompany().getName().equalsIgnoreCase(name);
    }
}
```

```java
// Closed for modification: find() never changes, no matter how many filters exist.
public List<JobApplication> find(ApplicationFilter filter){
    return repository.findAll().stream().filter(filter::matches).toList();
}
```

### Dependency Inversion Principle (DIP)
- **Definition**: High-level modules should not depend on low-level modules. Both should depend on abstractions.
- **Implementation in the Project**:
  - **ApplicationService**: Depends only on the `ApplicationRepository` and `Notifier` interfaces, never on `InMemoryApplicationRepository`, `JsonFileApplicationRepository` or `ConsoleNotifier`.
  - **Dependency Injection**: The concrete classes are passed in through the constructor. `Main` is the only place that decides which ones to use.

```java
// High-level module depends on abstractions, which are injected through the constructor.
public class ApplicationService {
    private final ApplicationRepository repository;
    private final Notifier notifier;

    public ApplicationService(ApplicationRepository repository, Notifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }
}
```

```java
// Main chooses the concrete classes; swapping storage is a one-line change.
ApplicationRepository repository = new JsonFileApplicationRepository(Path.of("applications.json"));
Notifier notifier = new ConsoleNotifier();
var service = new ApplicationService(repository, notifier);
new ConsoleMenu(service, new ApplicationPrinter()).run();
```

## Project Structure

### Directory Breakdown
```bash
src/main/java/org/example/
├── Main.java                                   # Entry point, creates and connects all objects (DIP)
└── Lab1/
    ├── ApplicationService.java                 # Use cases, depends only on interfaces (DIP)
    ├── ApplicationPrinter.java                 # Console output formatting (SRP)
    ├── ConsoleMenu.java                        # User interaction loop (SRP)
    ├── domain/
    │   ├── JobApplication.java                 # Application data and status change logic
    │   ├── ApplicationStatus.java              # Statuses and allowed changes
    │   ├── Company.java                        # Company value object (case-insensitive equality)
    │   └── StatusChange.java                   # One entry in the status history
    ├── filters/
    │   ├── ApplicationFilter.java              # Filter abstraction (OCP)
    │   ├── StatusFilter.java                   # Filter by status
    │   └── CompanyFilter.java                  # Filter by company
    ├── notification/
    │   ├── Notifier.java                       # Notification abstraction (DIP, OCP)
    │   └── ConsoleNotifier.java                # Prints notifications to the console
    └── repository/
        ├── ApplicationRepository.java          # Storage abstraction (DIP)
        ├── InMemoryApplicationRepository.java  # Stores applications in memory
        └── JsonFileApplicationRepository.java  # Stores applications in a JSON file (Jackson)
pom.xml                                         # Project configuration and dependencies (Jackson)
```

## Usage

### Running the Application
Requires **Java 25** and **Maven**.

```bash
mvn compile
mvn exec:java -Dexec.mainClass=org.example.Main
```

Or run `Main` directly from IntelliJ IDEA.

Menu:
```
1. Add application
2. List all
3. Change status
4. Filter by status
5. Filter by company
6. Show status history
7. Delete
0. Exit
```

### Example Output
```bash
Company: Google
Position: Backend Dev
Added:
9897adad   Google          Backend Dev          APPLIED    2026-10-15

Id (first few characters): 9897
Status [WISHLIST, APPLIED, INTERVIEW, OFFER, REJECTED, WITHDRAWN]: interview
[notification] Google: Backend Dev is now INTERVIEW

Id (first few characters): 9897
Status [WISHLIST, APPLIED, INTERVIEW, OFFER, REJECTED, WITHDRAWN]: wishlist
Error: INTERVIEW -> WISHLIST not allowed

ID         COMPANY         POSITION             STATUS     APPLIED ON
9897adad   Google          Backend Dev          INTERVIEW  2026-10-15
506aeaf5   Amazon          SDE                  APPLIED    2026-10-15

Id (first few characters): 9897
2026-10-15T09:31:01  APPLIED -> INTERVIEW
```

## Conclusion
The Job Application Tracker shows how SOLID principles lead to code that is easy to understand, extend and maintain. SRP gives each class one clear job, so a change to the output format, the storage or the business rules affects only one place. OCP lets you add new filters and notification channels without touching code that already works. DIP keeps the business logic separate from the storage and notification details, which made it possible to add JSON persistence by changing a single line in `Main`.

## Author
**Temciuc Adelina FAF-242**
