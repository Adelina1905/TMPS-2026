package org.example.Lab4.commands;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

// Command: the invoker. It runs commands and remembers them, so the last one can be undone
// and an undone one can be redone. It only knows the ApplicationCommand interface.
public class CommandHistory {
    private final Deque<ApplicationCommand> done = new ArrayDeque<>();
    private final Deque<ApplicationCommand> undone = new ArrayDeque<>();

    // If execute() throws (forbidden transition, viewer role, ...) nothing is recorded.
    public void run(ApplicationCommand command) {
        command.execute();
        done.push(command);
        undone.clear();
    }

    public ApplicationCommand undo() {
        if (done.isEmpty()) throw new IllegalStateException("Nothing to undo");
        var command = done.peek();
        command.undo();
        undone.push(done.pop());
        return command;
    }

    public ApplicationCommand redo() {
        if (undone.isEmpty()) throw new IllegalStateException("Nothing to redo");
        var command = undone.peek();
        command.execute();
        done.push(undone.pop());
        return command;
    }

    public boolean canUndo() {
        return !done.isEmpty();
    }

    public boolean canRedo() {
        return !undone.isEmpty();
    }

    // Most recent first.
    public List<String> descriptions() {
        return done.stream().map(ApplicationCommand::describe).toList();
    }
}
