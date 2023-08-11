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

    private JBTextField llamaURLtextField;

    private ComboBox<String> degreeQuestionsUI = new ComboBox<>();
    private ComboBox<String> llmTypeUI = new ComboBox<>();


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

        llamaURLtextField = new JBTextField();
        llamaURLtextField.setColumns(20);

        for (DegreeOfQuestionsFromExperts questionChoice : DegreeOfQuestionsFromExperts.values()) {
            degreeQuestionsUI.addItem(getDegreeOfQuestionsString(questionChoice));
        }

        for (PlantChatSettings.LLMType llmType : PlantChatSettings.LLMType.values()) {
            llmTypeUI.addItem(getLLMString(llmType));
        }

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Enter your OpenAI Key: "), openAIAPItextField, 1, false)
                .addLabeledComponent(new JBLabel("Enter your DeepL Key: "), deepLAPItextField, 1, false)
                .addLabeledComponent(new JBLabel("Select how many questions shall be asked"), degreeQuestionsUI, 1, false)
                .addLabeledComponent(new JBLabel("Select the type of LLM you want to use"), llmTypeUI, 1, false)
                .addLabeledComponent(new JBLabel("Enter the URL for the LLaMA url:"), llamaURLtextField, 1, false)
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

    String getLLMString(PlantChatSettings.LLMType type) {
        return switch (type) {
            case CHATGPT -> "ChatGPT 3.5";
            case GPT4 -> "GPT 4";
            case LLaMA -> "Plant Panadas LLM";
        };
    }

    @Override
    public boolean isModified() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        return !openAIAPItextField.getText().equals(settings.openAIToken) ||
                !deepLAPItextField.getText().equals(settings.deepLToken) ||
                settings.questionSetting.ordinal() != degreeQuestionsUI.getSelectedIndex() ||
                settings.llmType.ordinal() != llmTypeUI.getSelectedIndex() ||
                !llamaURLtextField.getText().equals(settings.llamaURL);
    }

    @Override
    public void apply() {
        PlantChatSettings settings = PlantChatSettings.getInstance();
        settings.openAIToken = openAIAPItextField.getText();
        settings.questionSetting = DegreeOfQuestionsFromExperts.values()[degreeQuestionsUI.getSelectedIndex()];
        settings.llmType = PlantChatSettings.LLMType.values()[llmTypeUI.getSelectedIndex()];
        settings.deepLToken = deepLAPItextField.getText();
        settings.llamaURL = llamaURLtextField.getText();
    }

    @Override
    public void reset() {
        // Reset the text field to the saved setting value
        PlantChatSettings settings = PlantChatSettings.getInstance();
        openAIAPItextField.setText(settings.openAIToken);
        degreeQuestionsUI.setSelectedIndex(settings.questionSetting.ordinal());
        llmTypeUI.setSelectedIndex(settings.llmType.ordinal());
        deepLAPItextField.setText(settings.deepLToken);
        llamaURLtextField.setText(settings.llamaURL);
    }

    @Override
    public void disposeUIResources() {
        // Clean up any resources used by the UI component
        openAIAPItextField = null;
        degreeQuestionsUI = null;
        deepLAPItextField = null;
        llmTypeUI = null;
        llamaURLtextField = null;
    }

}
