package de.plant.pandas.llm;

public class Message {
    private final String content;
    private final MessageRole messageRole;

    public Message(String content, MessageRole messageRole) {
        this.content = content;
        this.messageRole = messageRole;
    }

    public String getContent() {
        return content;
    }

    public MessageRole getMessageType() {
        return messageRole;
    }
}
