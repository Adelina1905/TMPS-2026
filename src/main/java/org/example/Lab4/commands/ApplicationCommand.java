package org.example.Lab4.commands;

// Command: the command interface. Every user action that changes data is an object
// that can be executed, undone and described in the history.
public interface ApplicationCommand {
    void execute();

    void undo();

    String describe();
}
