package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.plant_chat_intellij.logic.UMLChatBotProcessor;
import de.plant.pandas.plant_chat_intellij.ui.nodes.ChatArea;
import de.plant.pandas.plant_chat_intellij.ui.nodes.ChatInputField;
import de.plant.pandas.plant_chat_intellij.ui.nodes.Header;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;


public class PlantChatBotUI implements ToolWindowFactory {

    private Node separator() {
        Region topSeparator = new Region();
        topSeparator.setPrefHeight(1);
        topSeparator.setBackground(new Background(new BackgroundFill(Color.web("#1e1f22"), CornerRadii.EMPTY, Insets.EMPTY)));
        return topSeparator;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        final JFXPanel fxPanel = new JFXPanel();

        Platform.setImplicitExit(false);

        Platform.runLater(() -> {
            ChatArea chatArea = new ChatArea();
            Header header = new Header();

            UMLChatBotProcessor umlChatBotProcessor = new UMLChatBotProcessor(
                    message -> Platform.runLater(() -> chatArea.addChatMessage(message.getContent(), message.getMessageRole() == MessageRole.HUMAN)),
                    stage -> Platform.runLater(() -> header.setStage(stage))
            );

            VBox root = new VBox();


            root.getChildren().add(separator());
            root.getChildren().add(header);
            root.getChildren().add(separator());

            ChatInputField chatInputField = new ChatInputField(
                    text -> umlChatBotProcessor.startChat(project, text)
            );


            root.getChildren().add(chatArea);
            root.getChildren().add(separator());
            root.getChildren().add(chatInputField);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            fxPanel.setScene(scene);

        });


        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(fxPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
