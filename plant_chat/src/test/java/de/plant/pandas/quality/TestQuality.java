package de.plant.pandas.quality;

import de.plant.pandas.chatbot.*;
import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.translation.TranslatorServiceDeepL;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestQuality {
    @SneakyThrows
    public static void main(String[] args) {
        List<String> questions = List.of(
                "Our project is focused on designing an online bookstore where users can browse and purchase books, and also leave reviews. The system should also enable administrative staff to manage the inventory, track orders, and respond to user inquiries.",
                "The aim is to design an e-commerce website where users can browse and purchase electronic products, leave reviews, and track orders, with a backend for administrators to manage inventory, handle refunds, and respond to customer queries.",
                "CREATE a pet care service platform, where pet owners can book services like dog walking or vet appointments, and where service providers manage their schedules, prices, and customer interactions.",
                "Our project involves a gaming platform, where gamers can play games, interact with other players, and purchase in-game items, while game developers manage game releases, user behavior, and community engagement.",
                "We're creating a travel booking website, where users can search for flights, hotels, and holiday packages, and book their trips, while travel agents manage bookings, pricing, and customer inquiries.",
                "Designing a charity fundraising platform, where users can discover causes, make donations, and track the impact of their contributions, while charity organizers can manage fundraising campaigns and donor relationships.",
                "We're designing a music streaming app where users can listen to songs, create playlists, and follow artists, while music labels manage their artists, albums, and royalty payments.",
                "A virtual classroom system, where teachers can schedule classes, share materials, and evaluate student work, while students can attend classes, submit assignments, and communicate with peers and teachers.",
                "We are building a sports team management app, where team members can track game schedules, view statistics, and communicate with each other, while coaches manage rosters, strategies, and player development.",
                "Create a weather forecasting app where users can check local and global weather, get alerts, and customize their viewing preferences, while meteorologists manage data input, forecast models, and user inquiries."
        );

        Map<String, List> chatBots = Map.of(
                "ChatGPT-4-CoT", List.of(new UMLChatBotCoTImpl(), new OpenAILLM(OpenAILLM.OpenAIType.GPT4)),
                "ChatGPT-4-IoP", List.of(new UMLChatBotIoPImpl(), new OpenAILLM(OpenAILLM.OpenAIType.GPT4)),
                "ChatGPT-3-CoT", List.of(new UMLChatBotCoTImpl(), new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT))
        );

        for (Map.Entry<String, List> parameter : chatBots.entrySet()) {
            String name = parameter.getKey();
            System.out.println(name);
            UMLChatBot chatBot = (UMLChatBot) parameter.getValue().get(0);

            final Map<GenerationStage, Long> timePerStage = new HashMap<>();
            final GenerationStage[] lastStage = {null};
            final long[] time = {System.currentTimeMillis()};

            AskQuestionParameter par = AskQuestionParameter.builder()
                    .level(DegreeOfQuestionsFromExperts.NONE)
                    .llm((LLM) parameter.getValue().get(1))
                    .translatorService(new TranslatorServiceDeepL(null))
                    .onStageChange((generationStage -> {
                        long currentTimeMillis = System.currentTimeMillis();
                        if (lastStage[0] != null) {
                            timePerStage.put(lastStage[0], currentTimeMillis - time[0]);
                        }
                        lastStage[0] = generationStage;
                        time[0] = currentTimeMillis;
                    }))
                    .build();


            for (int i = 0; i < questions.size(); i++) {

                String folder = "results/" + name + "/" + i + "/";
                if (new File(folder).exists()) {
                    continue;
                }
                String question = questions.get(i);
                Thread.sleep(60 * 1000);

                lastStage[0] = null;
                time[0] = System.currentTimeMillis();
                timePerStage.clear();

                UMLChatBotResults result = null;

                while (result == null) {
                    List<Message> messages = new ArrayList<>();
                    messages.add(new Message("Create a class diagram. " + question, MessageRole.HUMAN));
                    result = chatBot.askQuestion(messages, Collections.emptyList(), par);
                    if (result instanceof UMLChatBotResults.ChatBotQuestions) {
                        System.out.println("Got a question, this is weired");
                    }
                }

                assert result instanceof UMLChatBotResults.GeneratedUML;
                UMLChatBotResults.GeneratedUML umlResult = (UMLChatBotResults.GeneratedUML) result;
                new File(folder).mkdirs();
                Files.write(Paths.get(folder + "question.txt"), question.getBytes());
                Files.write(Paths.get(folder + "timings.txt"), timePerStage.toString().getBytes());
                for (Map.Entry<String, String> umlFile : umlResult.getUmlResults().entrySet()) {
                    Files.write(Paths.get(folder + umlFile.getKey()), umlFile.getValue().getBytes());
                }
            }

        }
    }
}
