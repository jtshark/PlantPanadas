package de.plant.pandas;

import de.plant.pandas.chatbot.*;
import de.plant.pandas.llm.*;
import de.plant.pandas.translation.TranslatorService;
import de.plant.pandas.translation.TranslatorServiceDeepL;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        LLM llm = new OpenAILLM(OpenAILLM.OpenAIType.GPT4);
        Scanner scanner = new Scanner(System.in);
        TranslatorService translatorService = new TranslatorServiceDeepL(null);

        UMLChatBot umlChatBot = new UMLChatBotIoPImpl();
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message(
                "Create a class diagram for a pizza shop with customers and addresses", MessageRole.HUMAN));

        while (true) {
            UMLChatBotResults result = umlChatBot.askQuestion(messageList, Collections.emptyList(),
                    AskQuestionParameter
                            .builder()
                            .llm(llm)
                            .level(DegreeOfQuestionsFromExperts.NONE)
                            .translatorService(translatorService)
                            .build()
            );

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