package org.example.Lab3.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotifierDecoratorTest {

    // A stub component that remembers every message instead of printing it.
    private static class RecordingNotifier implements Notifier {
        final List<String> messages = new ArrayList<>();
        @Override
        public void notify(String message) { messages.add(message); }
    }

    private static final String TIMESTAMPED = "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}] Offer from Google";

    @Test
    void timestampDecoratorAddsTimeAndPassesMessageOn() {
        var recorder = new RecordingNotifier();
        Notifier notifier = new TimestampNotifier(recorder);

        notifier.notify("Offer from Google");

        assertEquals(1, recorder.messages.size());
        assertTrue(recorder.messages.getFirst().matches(TIMESTAMPED), recorder.messages.getFirst());
    }

    @Test
    void decoratorsStackedInAnyOrderStillDeliverTimestampedMessageAndLogIt(@TempDir Path dir) throws IOException {
        var recorderA = new RecordingNotifier();
        var logA = dir.resolve("a.log");
        Notifier timestampOutside = new TimestampNotifier(new FileLogNotifier(recorderA, logA));

        var recorderB = new RecordingNotifier();
        var logB = dir.resolve("b.log");
        Notifier logOutside = new FileLogNotifier(new TimestampNotifier(recorderB), logB);

        timestampOutside.notify("Offer from Google");
        logOutside.notify("Offer from Google");

        // Both chains deliver the same kind of message to the real component...
        assertTrue(recorderA.messages.getFirst().matches(TIMESTAMPED));
        assertTrue(recorderB.messages.getFirst().matches(TIMESTAMPED));
        // ...and both write the log. The order only decides whether the log line has the timestamp.
        assertTrue(Files.readString(logA).strip().matches(TIMESTAMPED));
        assertEquals("Offer from Google", Files.readString(logB).strip());
    }

    @Test
    void sameDecoratorCanBeUsedTwice(@TempDir Path dir) throws IOException {
        var recorder = new RecordingNotifier();
        var log = dir.resolve("n.log");
        Notifier notifier = new FileLogNotifier(new FileLogNotifier(recorder, log), log);

        notifier.notify("hello");

        assertEquals(List.of("hello"), recorder.messages);
        assertEquals(List.of("hello", "hello"), Files.readAllLines(log));
    }

    @Test
    void decoratorRejectsMissingWrappedNotifier() {
        assertThrows(IllegalArgumentException.class, () -> new TimestampNotifier(null));
    }
}
