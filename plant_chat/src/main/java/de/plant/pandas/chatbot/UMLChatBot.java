package de.plant.pandas.chatbot;

import java.util.Collection;
import java.util.Map;

public interface UMLChatBot {
    Map<String, String> askQuestion(Collection<String> plantUMLs, String task);
}
