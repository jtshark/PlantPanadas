package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PlantChatBotUI implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Create a panel with a text field
        JPanel panel = new JPanel();
        JTextField textField = new JTextField(20);
        textField.addActionListener((e) -> {
            String text = textField.getText();
            textField.setText("");
        });
        panel.add(textField);

        // Create a content with the panel and add it to the tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
