package de.plant.pandas.plant_chat_intellij.ui.nodes;

import com.intellij.ide.ui.UISettings;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

public class FontHelper {
    public static void bindFont(TextInputControl node) {

        int fontSize = UISettings.getInstance().getFontSize();
        String fontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
        node.setFont(new Font(fontFamily, fontSize));


        ApplicationManager.getApplication().getMessageBus().connect().subscribe(UISettingsListener.TOPIC, (UISettingsListener) source -> {
            int newFontSize = source.getFontSize();
            String newFontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
            Platform.runLater(() -> node.setFont(new Font(newFontFamily, newFontSize)));
        });

    }

    public static void bindFont(Labeled node) {

        int fontSize = UISettings.getInstance().getFontSize();
        String fontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
        node.setFont(new Font(fontFamily, fontSize));

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(UISettingsListener.TOPIC, (UISettingsListener) source -> {
            int newFontSize = source.getFontSize();
            String newFontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
            Platform.runLater(() -> node.setFont(new Font(newFontFamily, newFontSize)));
        });

    }
}
