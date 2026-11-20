package org.example.Lab4.events;

import java.util.function.Consumer;

// Concrete observer: after every status change it asks the application's new State what to do next
// and shows that advice.
public class NextStepAdvisor implements ApplicationObserver {
    private final Consumer<String> output;
    private int tips;

    public NextStepAdvisor(Consumer<String> output) {
        this.output = output;
    }

    @Override
    public void onEvent(ApplicationEvent event) {
        if (event.type() != EventType.STATUS_CHANGED && event.type() != EventType.STATUS_REVERTED) return;
        output.accept("[next step] " + event.title() + " -> " + event.application().nextStep());
        tips++;
    }

    @Override
    public String name() {
        return "Next-step advisor";
    }

    @Override
    public String summary() {
        return tips + " tip(s) given";
    }
}
