package de.plant.pandas;

import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.llm.LLM;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT);
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