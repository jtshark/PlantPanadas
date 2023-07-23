package de.plant.pandas.chatbot;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

public abstract class UMLChatBotResults {

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(callSuper = false)
    @ToString
    static public class ChatBotQuestions extends UMLChatBotResults {
        private final String question;

    }


    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(callSuper = false)
    @ToString
    static public class GeneratedUML extends UMLChatBotResults {
        private final Map<String, String> umlResults;
    }
}
