package de.plant.pandas.llm;

import de.plant.pandas.chatbot.LLaMAChatBot;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LLaMATest {
    public static void main(String[] args) throws IOException {
        LLaMAChatBot lLaMAChatBot = new LLaMAChatBot();

        String question = "Imagen a zoo filled with magical creatures. We have unicorns, dragons, and mermaids. Each of these creatures has a name, age, and a magic power. They also perform magic tricks.";

        List<Message> messages = new ArrayList<>();
        messages.add(new Message(question, MessageRole.SYSTEM));

        UMLChatBotResults results = lLaMAChatBot.askQuestion(messages, null, null);

        System.out.println(((UMLChatBotResults.GeneratedUML) results).getUmlResults());
    }
}
