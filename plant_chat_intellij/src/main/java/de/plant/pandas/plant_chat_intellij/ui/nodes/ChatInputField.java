package de.plant.pandas.plant_chat_intellij.ui.nodes;


import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class ChatInputField extends HBox {
    private final TextArea textArea;

    public ChatInputField(Consumer<String> sendText) {
        super();

        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPromptText("Type your question here...");
        FontHelper.bindFont(textArea);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int visualLinesCount = computeVisualLinesCount(newValue, textArea.getWidth());
            textArea.setPrefRowCount(visualLinesCount);
        });

        textArea.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            int visualLinesCount = computeVisualLinesCount(textArea.getText(), newBounds.getWidth());
            textArea.setPrefRowCount(visualLinesCount);
        });

        textArea.setPrefRowCount(1);

        // Create Button with icon
        FontIcon icon = new FontIcon(FluentUiFilledMZ.SEND_24);
        icon.setIconColor(Color.web("7c8b95"));
        FontHelper.bindFont(font -> icon.setIconSize((int) font.getSize() + 8), FontHelper.FontType.STANDARD);

        JFXButton sendButton = new JFXButton();
        sendButton.setCursor(Cursor.HAND);
        sendButton.setPrefSize(50, 50);
        sendButton.setGraphic(icon);
        sendButton.setRipplerFill(Color.WHITE);
        sendButton.setStyle("-fx-background-radius: 50%; -jfx-button-type: RAISED;");

        sendButton.setOnAction(event -> {
            sendText.accept(textArea.getText());
            textArea.clear();
        });

        HBox.setMargin(sendButton, new Insets(0, 16, 28, 0));

        getChildren().addAll(textArea, sendButton);
        setAlignment(Pos.BOTTOM_RIGHT);
        HBox.setMargin(textArea, new Insets(16, 16, 16, 16));

        Platform.runLater(() -> {
            Node node = textArea.lookup(".scroll-bar");
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                scrollBar.setOpacity(0);
                scrollBar.setMinHeight(0);
                scrollBar.setMaxHeight(0);
                scrollBar.setPrefHeight(0);
                scrollBar.setMinWidth(0);
                scrollBar.setMaxWidth(0);
                scrollBar.setPrefWidth(0);


            }

            ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
            scrollPane.setPadding(new Insets(12, 0, -8, 0));
        });

        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    textArea.insertText(textArea.getCaretPosition(), "\n");
                } else {
                    sendText.accept(textArea.getText());
                    textArea.clear();
                }
                event.consume();
            }
        });

        setHgrow(textArea, Priority.ALWAYS);

        setBackground(new Background(new BackgroundFill(Color.web("#2b2d30"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private int computeVisualLinesCount(String text, double lineWidth) {
        Text helper = new Text();
        helper.setFont(textArea.getFont());
        String[] lines = text.split("\n");
        int visualLinesCount = 0;
        for (String line : lines) {
            helper.setText(line);

            int visualLinesForThisLine = (int) Math.ceil((helper.getLayoutBounds().getWidth()) / (lineWidth - 32));
            visualLinesCount += visualLinesForThisLine;
        }
        return visualLinesCount;
    }
}
