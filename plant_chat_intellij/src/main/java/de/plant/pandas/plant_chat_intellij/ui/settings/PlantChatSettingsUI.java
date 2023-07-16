package de.plant.pandas.plant_chat_intellij.ui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.plant.pandas.chatbot.DegreeOfQuestionsFromExperts;
import org.jetbrains.annotations.Nls;

import javax.swing.*;


public class PlantChatSettingsUI implements Configurable {
    private JBTextField openAIAPItextField;
    private JBTextField deepLAPItextField;

    private ComboBox<String> degreeQuestionsUI = new ComboBox<>();

    @Nls
    @Override
    public String getDisplayName() {
        return "PlantPanadas";
    }

    @Override
    public JComponent createComponent() {

        openAIAPItextField = new JBTextField();
        openAIAPItextField.setColumns(20);

        deepLAPItextField = new JBTextField();
        deepLAPItextField.setColumns(20);

        for (DegreeOfQuestionsFromExperts questionChoice : DegreeOfQuestionsFromExperts.values()
        ) {
            degreeQuestionsUI.addItem(getDegreeOfQuestionsString(questionChoice));
        }

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter your OpenAI Key: "), openAIAPItextField, 1, false)
                .addLabeledComponent(new JBLabel("Enter your DeepL Key: "), deepLAPItextField, 1, false)
                .addLabeledComponent(new JBLabel("Select how many questions shall be asked"), degreeQuestionsUI, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    String getDegreeOfQuestionsString(DegreeOfQuestionsFromExperts level) {
        return switch (level) {
            case NONE -> "ASK NO QUESTIONS";
            case ALL_POSSIBLE -> "ASK ALL UPCOMING QUESTIONS";
            case ONLY_NECESSARY -> "ASK ONLY NECESSARY QUESTIONS";
        };
    }

    @Override
    public boolean isModified() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        return !openAIAPItextField.getText().equals(settings.openAIToken) || !deepLAPItextField.getText().equals(settings.deepLToken) || settings.questionSetting.ordinal() != degreeQuestionsUI.getSelectedIndex();
    }

    @Override
    public void apply() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        settings.openAIToken = openAIAPItextField.getText();
        settings.questionSetting = DegreeOfQuestionsFromExperts.values()[degreeQuestionsUI.getSelectedIndex()];
        settings.deepLToken = deepLAPItextField.getText();
    }

    @Override
    public void reset() {
        // Reset the text field to the saved setting value
        PlantChatSettings settings = PlantChatSettings.getInstance();
        openAIAPItextField.setText(settings.openAIToken);
        degreeQuestionsUI.setSelectedIndex(settings.questionSetting.ordinal());
        deepLAPItextField.setText(settings.deepLToken);
    }

    @Override
    public void disposeUIResources() {
        // Clean up any resources used by the UI component
        openAIAPItextField = null;
        degreeQuestionsUI = null;
        deepLAPItextField = null;
    }

}
