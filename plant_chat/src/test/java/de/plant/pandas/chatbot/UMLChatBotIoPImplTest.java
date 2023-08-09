package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.translation.TranslationResult;
import de.plant.pandas.translation.TranslatorService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UMLChatBotIoPImplTest {
    private Prompts prompts;
    private ResponseParser responseParser;
    private TranslatorService translatorService;
    private LLM llm;
    private Consumer<GenerationStage> onStageChange;

    private AskQuestionParameter askQuestionParameter;

    private UMLChatBot chatBot;

    @BeforeEach
    public void setup() {
        prompts = mock(Prompts.class);
        responseParser = mock(ResponseParser.class);
        translatorService = mock(TranslatorService.class);
        llm = mock(LLM.class);
        onStageChange = mock(Consumer.class);
        askQuestionParameter = AskQuestionParameter.builder()
                .level(DegreeOfQuestionsFromExperts.NONE)
                .translatorService(translatorService)
                .llm(llm)
                .build();

        StageListener.getInstance().registerListener(onStageChange);


        chatBot = new UMLChatBotIoPImpl(responseParser, prompts);
    }

    @AfterEach
    public void tearDown() {
        StageListener.getInstance().unregisterListener(onStageChange);
    }

    @SneakyThrows
    @Test
    public void testQuestionResponse() {
        Message userQuestion = new Message("User", MessageRole.HUMAN);
        List<Message> messages = new ArrayList<>();
        messages.add(userQuestion);

        when(translatorService.translateToLanguage(eq("User"), any(), eq(true))).thenReturn(new TranslationResult("User-de", "de"));

        Message dicussionMessage = new Message("Discuss", MessageRole.SYSTEM);
        when(prompts.getIOPrompt(Collections.emptyList(), DegreeOfQuestionsFromExperts.NONE)).thenReturn(dicussionMessage.getContent());
        when(llm.prompt(eq(List.of(dicussionMessage, userQuestion)), any(), anyInt())).thenReturn("discussion... QUESTION: question1?");
        when(responseParser.getQuestion("discussion... QUESTION: question1?")).thenReturn("question1?");

        when(translatorService.translateToLanguage("question1?", "de", false)).thenReturn(new TranslationResult("question1-de?", "EN-US"));


        UMLChatBotResults results = chatBot.askQuestion(messages, Collections.emptyList(), askQuestionParameter);
        assertEquals(new UMLChatBotResults.ChatBotQuestions("question1-de?"), results);

        InOrder inOrder = inOrder(onStageChange);
        inOrder.verify(onStageChange).accept(GenerationStage.GENERATE_PLANT_UML);
        inOrder.verify(onStageChange).accept(null);
    }


    @SneakyThrows
    private void setUpCodeForUMLResponse(boolean firstTry) {
        Message userQuestion = new Message("User", MessageRole.HUMAN);
        List<Message> messages = new ArrayList<>();
        messages.add(userQuestion);
        when(translatorService.translateToLanguage(eq("User"), any(), eq(true))).thenReturn(new TranslationResult("User-de", "de"));

        Message ioMessage = new Message("Generate", MessageRole.SYSTEM);
        when(prompts.getIOPrompt(Collections.emptyList(), DegreeOfQuestionsFromExperts.NONE)).thenReturn(ioMessage.getContent());
        when(llm.prompt(eq(List.of(ioMessage, userQuestion)), any(), anyInt())).thenReturn("plant uml");


        Map<String, String> result = Map.of("file1", "@startuml\n@enduml");

        if (firstTry) {
            when(responseParser.umlStringToMap("plant uml")).thenReturn(result);
        } else {
            when(responseParser.umlStringToMap("plant uml")).thenReturn(Map.of());

            Message fixMessage = new Message("fix PlantUML", MessageRole.SYSTEM);
            when(prompts.foundNoUMLPrompt()).thenReturn(fixMessage.getContent());
            when(llm.prompt(eq(List.of(ioMessage, userQuestion, new Message("plant uml", MessageRole.ASSISTANT), fixMessage)), any(), anyInt())).thenReturn("plant uml2");
            when(responseParser.umlStringToMap("plant uml2")).thenReturn(result);
        }


        UMLChatBotResults results = chatBot.askQuestion(messages, Collections.emptyList(), askQuestionParameter);
        assertEquals(new UMLChatBotResults.GeneratedUML(result), results);

        InOrder inOrder = inOrder(onStageChange);
        inOrder.verify(onStageChange).accept(GenerationStage.GENERATE_PLANT_UML);
        if (!firstTry) {
            inOrder.verify(onStageChange).accept(GenerationStage.FIX_ERRORS);
        }
        inOrder.verify(onStageChange).accept(null);
    }

    @Test
    @SneakyThrows
    public void testUMLResponse() {
        setUpCodeForUMLResponse(true);
    }

    @Test
    public void testUMLResponseWithFirstlyWrongSyntax() {
        setUpCodeForUMLResponse(false);
    }
}
