package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.plantuml.ResponseParserImpl;
import de.plant.pandas.translation.TranslationResult;
import de.plant.pandas.translation.TranslatorService;
import de.plant.pandas.translation.TranslatorServiceDeepL;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UMLChatBotCoTImplIntegrationTest {

    UMLChatBot chatBot;
    AskQuestionParameter parameter;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        chatBot = new UMLChatBotCoTImpl();

        parameter = AskQuestionParameter.builder()
                .llm(new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT))
                .level(DegreeOfQuestionsFromExperts.NONE)
                .translatorService(new TranslatorServiceDeepL(null))
                .build();

    }

    @SneakyThrows
    @Test
    public void testResponse() {
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message(
                "Create a class diagram for a pizza shop with customers and addresses", MessageRole.HUMAN));

        UMLChatBotResults results = chatBot.askQuestion(messageList, Collections.emptyList(), parameter);

        assertTrue(results instanceof UMLChatBotResults.GeneratedUML);
        assertFalse(((UMLChatBotResults.GeneratedUML) results).getUmlResults().isEmpty());
    }

}
