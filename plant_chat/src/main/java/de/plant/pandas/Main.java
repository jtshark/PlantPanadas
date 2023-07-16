package de.plant.pandas;

import de.plant.pandas.chatbot.DegreeOfQuestionsFromExperts;
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
        LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT);
        //LLM llm = new LLaMALLM(new URL("http://127.0.0.1:5000"));
        Scanner scanner = new Scanner(System.in);

        UMLChatBot umlChatBot = new UMLChatBotImpl(llm, Keys.getProperty("DEEPL_KEY"));
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message(
                "Erstelle ein Klassendiagramm für einen Pizzashop mit einem Kunden und einer Bestellung und Pizzas, die man kaufen kann.\n" +
                        "Die Kunden sollen ein Alter haben, damit wir überprüfen können, ob sie bestimmte Artikel kaufen können.\n" +
                        "Außerdem sollen die Pizzas einen normalen Preis haben und einen Preis, wenn ein Rabatt angewendet wurde.", MessageRole.HUMAN));

        while (true) {
            UMLChatBotResults result = umlChatBot.askQuestion(Collections.emptyList(), messageList, DegreeOfQuestionsFromExperts.ALL_POSSIBLE);

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
        scanner.close();
    }
}