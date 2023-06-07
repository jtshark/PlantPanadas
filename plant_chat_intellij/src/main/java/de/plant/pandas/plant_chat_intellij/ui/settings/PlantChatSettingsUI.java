package de.plant.pandas.plant_chat_intellij.ui.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;


public class PlantChatSettingsUI implements Configurable {
    private JTextField textField;
    private JPanel panel;

    @Nls
    @Override
    public String getDisplayName() {
        return "PlantPanadas";
    }

    @Override
    public JComponent createComponent() {
        panel = new JPanel(new FlowLayout());
        textField = new JTextField(20);
        PlantChatSettings settings = PlantChatSettings.getInstance();
        textField.setText(settings.openAIToken);
        panel.add(new JLabel("Enter your OpenAI Key:"));
        panel.add(textField);
        return panel;
    }

    @Override
    public boolean isModified() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        return !textField.getText().equals(settings.openAIToken);
    }

    @Override
    public void apply() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        settings.openAIToken = textField.getText();
    }

    @Override
    public void reset() {
        // Reset the text field to the saved setting value
        PlantChatSettings settings = PlantChatSettings.getInstance();
        textField.setText(settings.openAIToken);
    }

    @Override
    public void disposeUIResources() {
        // Clean up any resources used by the UI component
        panel = null;
        textField = null;
    }

}
