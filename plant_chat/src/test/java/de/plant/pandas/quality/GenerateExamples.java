package de.plant.pandas.quality;

import de.plant.pandas.chatbot.*;
import de.plant.pandas.llm.*;
import de.plant.pandas.translation.TranslatorServiceDeepL;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GenerateExamples {
    public static void main(String[] args) throws IOException, URISyntaxException {

        String question = "Create a class diagramm for a zoo filled with magical creatures. We have unicorns, dragons, and mermaids. Each of these creatures has a name and a magic power. Furthermore they have an age that can be queried. They also perform magic tricks. The magical creatures don't perform their tricks alone. They have magic staffs that assist them. Every magic staff has a staffName and a gemstone embedded in it. Aside from that, a magical creature has multiple staffs to help them, but a magic staff works for only one magical creature.";
        Map<String, List> chatBots = Map.of(
                "ChatGPT-4-CoT", List.of(new UMLChatBotCoTImpl(), new OpenAILLM(OpenAILLM.OpenAIType.GPT4)),
                "ChatGPT-4-IoP", List.of(new UMLChatBotIoPImpl(), new OpenAILLM(OpenAILLM.OpenAIType.GPT4)),
                "ChatGPT-3-CoT", List.of(new UMLChatBotCoTImpl(), new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT)),
                "Plant-Panadas-LLM", List.of(new LLaMAChatBot(), new LLaMALLM(new URI("http://localhost:5632").toURL()))

        );

        for (Map.Entry<String, List> parameter : chatBots.entrySet()) {
            UMLChatBot chatBot = (UMLChatBot) parameter.getValue().get(0);
            AskQuestionParameter par = AskQuestionParameter.builder()
                    .level(DegreeOfQuestionsFromExperts.NONE)
                    .llm((LLM) parameter.getValue().get(1))
                    .translatorService(new TranslatorServiceDeepL(null))
                    .build();

            List<Message> messages = new ArrayList<>();
            messages.add(new Message(question, MessageRole.HUMAN));
            UMLChatBotResults result = chatBot.askQuestion(messages, Collections.emptyList(), par);
            UMLChatBotResults.GeneratedUML umlResult = (UMLChatBotResults.GeneratedUML) result;
            System.out.println(parameter.getKey());
            System.out.println(umlResult.getUmlResults());
        }
    }
}
