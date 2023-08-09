package de.plant.pandas.chatbot;

import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.translation.TranslatorServiceDeepL;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UMLChatBotIoPImplIntegrationTest {

    UMLChatBot chatBot;
    AskQuestionParameter.AskQuestionParameterBuilder parameter;

    @SneakyThrows
    @BeforeEach
    public void setup() {
        chatBot = new UMLChatBotIoPImpl();

        parameter = AskQuestionParameter.builder()
                .llm(new OpenAILLM(OpenAILLM.OpenAIType.CHATGPT))
                .onStageChange(null)
                .level(DegreeOfQuestionsFromExperts.NONE)
                .translatorService(new TranslatorServiceDeepL(null));
    }

    @SneakyThrows
    @Test
    public void testResponse() {
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message(
                "Create a class diagram for a pizza shop with customers and addresses", MessageRole.HUMAN));

        UMLChatBotResults results = chatBot.askQuestion(messageList, Collections.emptyList(), parameter.build());

        assertTrue(results instanceof UMLChatBotResults.GeneratedUML);
        assertFalse(((UMLChatBotResults.GeneratedUML) results).getUmlResults().isEmpty());
    }

    @SneakyThrows
    @Test
    public void testQuestionResponse() {
        List<Message> messageList = new LinkedList<>();
        messageList.add(new Message(
                "Create a class diagram for a pizza shop with customers and addresses. And so many features!", MessageRole.HUMAN));


        UMLChatBotResults results = chatBot.askQuestion(messageList, Collections.emptyList(),
                parameter
                        .level(DegreeOfQuestionsFromExperts.ALL_POSSIBLE)
                        .llm(new OpenAILLM(OpenAILLM.OpenAIType.GPT4))
                        .build());

        assertTrue(results instanceof UMLChatBotResults.ChatBotQuestions);
    }

}
