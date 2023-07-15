package de.plant.pandas.plant_chat_intellij.ui.nodes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ChatBubble extends HBox {

    public ChatBubble(String message, boolean isUser) {
        Label label = new Label(message);
        FontHelper.bindFont(label);
        label.setWrapText(true);
        label.setPadding(new Insets(10));
        label.setMaxWidth(400);
        label.setBackground(new Background(new BackgroundFill(Color.web(isUser ? "#005c4b" : "#202c33"), new CornerRadii(15), Insets.EMPTY)));
        label.setTextFill(Color.WHITE);

        HBox container = new HBox(label);
        container.setPadding(new Insets(5));

        if (isUser) {
            setAlignment(Pos.CENTER_RIGHT);
        } else {
            setAlignment(Pos.CENTER_LEFT);
        }

        getChildren().add(container);
    }
}