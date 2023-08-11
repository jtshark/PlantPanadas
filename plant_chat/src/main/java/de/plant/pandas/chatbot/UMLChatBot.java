package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface UMLChatBot {
    UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException;
}
