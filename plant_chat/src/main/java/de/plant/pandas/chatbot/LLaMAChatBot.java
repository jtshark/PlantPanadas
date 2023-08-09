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

public class LLaMAChatBot implements UMLChatBot {

    final LLaMALLM lLaMALLM;
    final OpenAILLM openAILLM;
    final Prompts prompts;
    final ResponseParser responseParser;

    @SneakyThrows
    public LLaMAChatBot() {
        lLaMALLM = new LLaMALLM(new URL("http://127.0.0.1:5632"));
        openAILLM = new OpenAILLM(OpenAILLM.OpenAIType.GPT4);
        prompts = new PromptsImpl();
        responseParser = new ResponseParserImpl();
    }

    @Override
    public UMLChatBotResults askQuestion(List<Message> messages, Collection<String> plantUMLs, AskQuestionParameter askQuestionParameter) throws IOException {

        String answer = lLaMALLM.prompt(messages, Collections.emptyList(), 2048);

        System.out.println(answer);
        messages.add(new Message(answer, MessageRole.ASSISTANT));
        messages.add(new Message(prompts.generatePlantUMLPrompt(), MessageRole.SYSTEM));
        String result = openAILLM.prompt(messages, Collections.emptyList(), 4000);

        return new UMLChatBotResults.GeneratedUML(responseParser.umlStringToMap(result));
    }
}
