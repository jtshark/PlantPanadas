package de.plant.pandas;

import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        //LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT);
        LLM llm = new LLaMALLM(new URL("http://127.0.0.1:5000"));
        Scanner scanner = new Scanner(System.in);

        UMLChatBot umlChatBot = new UMLChatBotImpl(llm);
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message("Create a class diagram PizzaShop with a customer and an order.", MessageRole.HUMAN));

        while (true) {
            UMLChatBotResults result = umlChatBot.askQuestion(Collections.emptyList(), messageList);

            if (result instanceof UMLChatBotResults.ChatBotQuestions) {
                String question = ((UMLChatBotResults.ChatBotQuestions) result).getQuestion();
                messageList.add(new Message(question, MessageRole.ASSISTANT));
                System.out.println(question);
                String response = scanner.nextLine();
                messageList.add(new Message(response, MessageRole.HUMAN));
            } else if (result instanceof UMLChatBotResults.GeneratedUML) {
                System.out.println(((UMLChatBotResults.GeneratedUML) result).getUmlResults());
                break;

            }

        }
        scanner.close();
    }
}