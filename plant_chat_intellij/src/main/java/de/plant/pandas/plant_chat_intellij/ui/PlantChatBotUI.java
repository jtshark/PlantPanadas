package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.plant_chat_intellij.ui.nodes.ChatArea;
import de.plant.pandas.plant_chat_intellij.ui.nodes.ChatInputField;
import de.plant.pandas.plant_chat_intellij.ui.nodes.Header;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import logic.UMLChatBotProcessor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class PlantChatBotUI implements ToolWindowFactory {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        final JFXPanel fxPanel = new JFXPanel();
        JComponent component = toolWindow.getComponent();

        Platform.setImplicitExit(false);

        Platform.runLater(() -> {
            ChatArea chatArea = new ChatArea();
            Header header = new Header();

            UMLChatBotProcessor umlChatBotProcessor = new UMLChatBotProcessor(
                    message -> {
                        Platform.runLater(() -> {
                            chatArea.addChatMessage(message.getContent(), message.getMessageType() == MessageRole.HUMAN);
                        });
                    },
                    stage -> Platform.runLater(() -> header.setStage(stage))
            );

            VBox root = new VBox();
            root.getChildren().add(header);


            ChatInputField chatInputField = new ChatInputField(
                    text -> umlChatBotProcessor.startChat(project, text)
            );


            root.getChildren().add(chatArea);
            root.getChildren().add(chatInputField);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            fxPanel.setScene(scene);
        });


        component.getParent().add(fxPanel);
    }
}
