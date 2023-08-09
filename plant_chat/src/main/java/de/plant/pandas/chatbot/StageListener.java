package de.plant.pandas.chatbot;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class StageListener {
    @Getter
    private GenerationStage generationStage;


    private static StageListener instance;

    private StageListener() {
    }

    public static StageListener getInstance() {
        if (instance == null) {
            instance = new StageListener();
        }
        return instance;
    }

    private final List<Consumer<GenerationStage>> listeners = new LinkedList<>();

    public void registerListener(Consumer<GenerationStage> listener, boolean fireImmediately) {
        listeners.add(listener);
        if (fireImmediately) {
            listener.accept(generationStage);
        }
    }

    public void registerListener(Consumer<GenerationStage> listener) {
        registerListener(listener, false);
    }

    public void unregisterListener(Consumer<GenerationStage> listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    void setGenerationStage(GenerationStage generationStage) {
        listeners.forEach((c) -> c.accept(generationStage));
        this.generationStage = generationStage;
    }


}
