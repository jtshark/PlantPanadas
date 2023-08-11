package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLaMALLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.plantuml.ResponseParserImpl;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LLaMAChatBot implements UMLChatBot {

    private final Prompts prompts;
    private final ResponseParser responseParser;

    @SneakyThrows
    public LLaMAChatBot() {

        prompts = new PromptsImpl();
        responseParser = new ResponseParserImpl();
    }

    @Override
    public UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException {

        try {
            if (messages.size() == 1) {
                messages.set(0, new Message(messages.get(0).getContent(), MessageRole.SYSTEM));
            }
            List<String> stopTokens = askQuestionParameter.getLevel() == DegreeOfQuestionsFromExperts.NONE ? Collections.emptyList() : List.of("### ANSWER:");

            StageListener.getInstance().setGenerationStage(GenerationStage.GENERATE_PLANT_UML);

            String answer = askQuestionParameter.getLlm().prompt(messages, stopTokens, 4096);
            messages.add(new Message(answer, MessageRole.ASSISTANT));
            if (answer.contains("### QUESTION:") && askQuestionParameter.getLevel() != DegreeOfQuestionsFromExperts.NONE) {
                String question = responseParser.getQuestion(answer);
                StageListener.getInstance().setGenerationStage(null);
                return new UMLChatBotResults.ChatBotQuestions(question);
            }

            Map<String, String> uml = responseParser.umlStringToMap(answer);
            StageListener.getInstance().setGenerationStage(null);
            return new UMLChatBotResults.GeneratedUML(uml);
        } catch (Exception e) {
            StageListener.getInstance().setGenerationStage(null);
            throw e;
        }

    }
}
