package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;

import java.io.IOException;
import java.util.*;

import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.plantuml.ResponseParserImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class UMLChatBotCoTImpl implements UMLChatBot {
    private final ResponseParser responseParser;
    private final Prompts prompts;

    public UMLChatBotCoTImpl() {
        this(new ResponseParserImpl(), new PromptsImpl());
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time); // wait for one second (not have that often to many requests error)
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public UMLChatBotResults.GeneratedUML generateUML(List<Message> messages, AskQuestionParameter askQuestionParameter) throws IOException {
        messages.remove(0);
        messages.add(new Message(prompts.getStepByStepPrompt(), MessageRole.SYSTEM));
        StageListener.getInstance().setGenerationStage(GenerationStage.CREATE_PLAN);

        sleep(askQuestionParameter.getSleepTime());
        String steps = askQuestionParameter.getLlm().prompt(messages, Collections.EMPTY_LIST, 4000);


        messages.add(new Message(steps, MessageRole.ASSISTANT));
        messages.add(new Message(prompts.generatePlantUMLPrompt(), MessageRole.SYSTEM));
        StageListener.getInstance().setGenerationStage(GenerationStage.GENERATE_PLANT_UML);

        sleep(askQuestionParameter.getSleepTime());
        String plantUML = askQuestionParameter.getLlm().prompt(messages, Collections.EMPTY_LIST, 6000);
        System.out.println(plantUML);
        Map<String, String> result = responseParser.umlStringToMap(plantUML);
        if (result.isEmpty()) {
            messages.add(new Message(plantUML, MessageRole.ASSISTANT));
            messages.add(new Message(prompts.foundNoUMLPrompt(), MessageRole.SYSTEM));

            StageListener.getInstance().setGenerationStage(GenerationStage.FIX_ERRORS);

            sleep(askQuestionParameter.getSleepTime());
            plantUML = askQuestionParameter.getLlm().prompt(messages, Collections.EMPTY_LIST, 7000);
            result = responseParser.umlStringToMap(plantUML);
        }


        return new UMLChatBotResults.GeneratedUML(result);
    }

    public UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException {

        try {
            Message lastMessage = messages.get(messages.size() - 1);
            String language = askQuestionParameter.getTranslatorService().translateToLanguage(lastMessage.getContent(), "EN-US", true).getLang();

            messages.add(0, new Message(prompts.getDiscussionPrompt(plantUMLs, askQuestionParameter.getLevel()), MessageRole.SYSTEM));
            StageListener.getInstance().setGenerationStage(GenerationStage.THINKING);

            String output = askQuestionParameter.getLlm().prompt(messages, List.of("END", "User:"), 2000);
            System.out.println(output);
            messages.add(new Message(output, MessageRole.ASSISTANT));
            if (output.contains("QUESTION:")) {
                messages.remove(0);
                String question = responseParser.getQuestion(output);

                question = askQuestionParameter.getTranslatorService().translateToLanguage(question, language, false).getText();

                StageListener.getInstance().setGenerationStage(null);
                return new UMLChatBotResults.ChatBotQuestions(question);
            } else {
                UMLChatBotResults.GeneratedUML result = generateUML(messages, askQuestionParameter);
                StageListener.getInstance().setGenerationStage(null);
                return result;
            }
        } catch (Exception e) {
            StageListener.getInstance().setGenerationStage(null);
            throw e;
        }


    }
}
