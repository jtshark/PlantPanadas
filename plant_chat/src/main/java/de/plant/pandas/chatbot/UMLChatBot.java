package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.translation.TranslatorService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface UMLChatBot {
    UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException;
}
