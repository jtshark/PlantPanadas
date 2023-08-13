package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.plantuml.ResponseParserImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
public class UMLChatBotIoPImpl implements UMLChatBot {
    private final ResponseParser responseParser;
    private final Prompts prompts;

    public UMLChatBotIoPImpl() {
        this(new ResponseParserImpl(), new PromptsImpl());
    }


    public UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException {
        try {
            Message lastMessage = messages.get(messages.size() - 1);
            String lang = askQuestionParameter.getTranslatorService().translateToLanguage(lastMessage.getContent(), "EN-US", true).getLang();

            messages.add(0, new Message(prompts.getIOPrompt(plantUMLs, askQuestionParameter.getLevel()), MessageRole.SYSTEM));


            StageListener.getInstance().setGenerationStage(GenerationStage.GENERATE_PLANT_UML);
 
            String output = askQuestionParameter.getLlm().prompt(messages, List.of("END", "User:"), 5000);
            messages.add(new Message(output, MessageRole.ASSISTANT));
            if (output.contains("QUESTION:")) {
                messages.remove(0);
                String question = responseParser.getQuestion(output);
                question = askQuestionParameter.getTranslatorService().translateToLanguage(question, lang, false).getText();

                StageListener.getInstance().setGenerationStage(null);
                return new UMLChatBotResults.ChatBotQuestions(question);
            } else {
                Map<String, String> result = responseParser.umlStringToMap(output);

                if (result.isEmpty()) {
                    messages.add(new Message(prompts.foundNoUMLPrompt(), MessageRole.SYSTEM));

                    StageListener.getInstance().setGenerationStage(GenerationStage.FIX_ERRORS);


                    output = askQuestionParameter.getLlm().prompt(messages, Collections.EMPTY_LIST, 2000);
                    result = responseParser.umlStringToMap(output);
                }

                StageListener.getInstance().setGenerationStage(null);
                return new UMLChatBotResults.GeneratedUML(result);
            }
        } catch (Exception e) {
            StageListener.getInstance().setGenerationStage(null);
            throw e;
        }


    }
}
