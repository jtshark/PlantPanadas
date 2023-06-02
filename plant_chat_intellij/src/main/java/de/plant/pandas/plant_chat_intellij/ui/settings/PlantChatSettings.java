package de.plant.pandas.plant_chat_intellij.ui.settings;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
@State(name = "PlantChatSettings", storages = {@Storage("PlantChat.xml")})
public class PlantChatSettings implements PersistentStateComponent<PlantChatSettings> {
    public String token;

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

}
