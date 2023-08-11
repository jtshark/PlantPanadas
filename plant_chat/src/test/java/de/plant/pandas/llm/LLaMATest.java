package de.plant.pandas.llm;

import de.plant.pandas.chatbot.AskQuestionParameter;
import de.plant.pandas.chatbot.LLaMAChatBot;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class LLaMATest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        LLaMAChatBot lLaMAChatBot = new LLaMAChatBot();

        String question = "Imagen a zoo filled with magical creatures. We have unicorns, dragons, and mermaids. Each of these creatures has a name, age, and a magic power. They also perform magic tricks.";

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(question, MessageRole.SYSTEM));

        UMLChatBotResults results = lLaMAChatBot.askQuestion(messages, null, AskQuestionParameter.builder()
                .llm(new LLaMALLM(new URI("http://localhost:5632").toURL()))
                .build());

        System.out.println(((UMLChatBotResults.GeneratedUML) results).getUmlResults());
    }
}
