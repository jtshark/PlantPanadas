package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UMLChatBot {
    UMLChatBotResults askQuestion(Collection<String> plantUMLs, List<Message> messages, DegreeOfQuestionsFromExperts level) throws IOException;
}
