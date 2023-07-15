package de.plant.pandas.plant_chat_intellij.ui.nodes;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatArea extends VBox {
    public ChatArea() {
        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public void addChatMessage(String message, boolean isUser) {
        ChatBubble chatBubble = new ChatBubble(message, isUser);
        getChildren().add(chatBubble);
    }
}
