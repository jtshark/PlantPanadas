package de.plant.pandas.plant_chat_intellij.ui.nodes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChatArea extends ScrollPane {
    private final VBox chatBubblesBox;

    public ChatArea() {
        chatBubblesBox = new VBox();
        chatBubblesBox.setSpacing(10);
        chatBubblesBox.setPadding(new Insets(10));

        setContent(chatBubblesBox);
        setFitToWidth(true);
        setVvalue(1.0); // scroll to bottom
        vvalueProperty().bind(chatBubblesBox.heightProperty()); // auto scroll to bottom when a new message is added

        VBox.setVgrow(this, Priority.ALWAYS);
    }

    public void addChatMessage(String message, boolean isUser) {
        ChatBubble chatBubble = new ChatBubble(message, isUser);
        chatBubblesBox.getChildren().add(chatBubble);
    }
}
