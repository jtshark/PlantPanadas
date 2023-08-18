package de.plant.pandas.plant_chat_intellij.ui.settings;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import de.plant.pandas.chatbot.DegreeOfQuestionsFromExperts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
@State(name = "PlantChatSettings", storages = {@Storage("PlantChat.xml")})
public class PlantChatSettings implements PersistentStateComponent<PlantChatSettings> {
    public String openAIToken;
    public String deepLToken;

    public DegreeOfQuestionsFromExperts questionSetting = DegreeOfQuestionsFromExperts.NONE;
    public LLMType llmType = LLMType.GPT4;

    public String llamaURL = "http://localhost:5632";

    public static PlantChatSettings getInstance() {
        Application app = ApplicationManager.getApplication();
        return app.getService(PlantChatSettings.class);
    }


    @Nullable
    @Override
    public PlantChatSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PlantChatSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public enum LLMType {
        CHATGPT, GPT4, LLaMA
    }
}
