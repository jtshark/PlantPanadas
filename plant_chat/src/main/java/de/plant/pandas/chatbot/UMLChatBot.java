package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface UMLChatBot {
    UMLChatBotResults askQuestion(Collection<String> plantUMLs, List<Message> messages, DegreeOfQuestionsFromExperts level, Consumer<GenerationStage> onStageChange) throws IOException;

    default UMLChatBotResults askQuestion(Collection<String> plantUMLs, List<Message> messages, DegreeOfQuestionsFromExperts level) throws IOException {
        return askQuestion(plantUMLs, messages, level, null);
    }

}
