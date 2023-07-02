package de.plant.pandas;

import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        /*LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT);
        //LLM llm = new LLaMALLM(new URL("http://127.0.0.1:5000"));
        Scanner scanner = new Scanner(System.in);

        UMLChatBot umlChatBot = new UMLChatBotImpl(llm);
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message("Create a class diagram PizzaShop with a customer and an order and Pizzas to buy. The Customer should have an age so we can check that they are eighteen when buying certain elements. Additionally the Pizzas have a normal price and a price when a discount is applied.", MessageRole.HUMAN));

        while (true) {
            UMLChatBotResults result = umlChatBot.askQuestion(Collections.emptyList(), messageList);

            if (result instanceof UMLChatBotResults.ChatBotQuestions) {
                String question = ((UMLChatBotResults.ChatBotQuestions) result).getQuestion();
                System.out.println(question);
                String response = scanner.nextLine();
                messageList.add(new Message(response, MessageRole.HUMAN));
            } else if (result instanceof UMLChatBotResults.GeneratedUML) {
                System.out.println(((UMLChatBotResults.GeneratedUML) result).getUmlResults());
                break;

            }

        }
        scanner.close();*/
        String text = new String(Files.readAllBytes(Paths.get("/home/jt/work/uni/PlantPanadas/plant_chat/src/main/resources/examples.txt")));
        UMLChatBotImpl chatBot = new UMLChatBotImpl(null);
        System.out.println(chatBot.umlStringToMap(text));

    }
}