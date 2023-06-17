package de.plant.pandas.chatbot;

import java.util.Map;

public abstract class UMLChatBotResults {

    static public class ChatBotQuestions extends UMLChatBotResults {
        private final String question;

        public ChatBotQuestions(String question) {
            this.question = question;
        }

        public String getQuestion() {
            return question;
        }
    }


    static public class GeneratedUML extends UMLChatBotResults {
        private final Map<String, String> umlResults;

        public GeneratedUML(Map<String, String> umlResults) {
            this.umlResults = umlResults;
        }

        public Map<String, String> getUmlResults() {
            return umlResults;
        }
    }
}
