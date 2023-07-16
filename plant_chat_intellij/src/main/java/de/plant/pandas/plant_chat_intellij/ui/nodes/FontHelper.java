package de.plant.pandas.plant_chat_intellij.ui.nodes;

import com.intellij.ide.ui.UISettings;
import com.intellij.ide.ui.UISettingsListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import javafx.application.Platform;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class FontHelper {

    enum FontType {
        TITLE(4), STANDARD(0), SUBTITLE(-2);

        final int fontOffset;

        FontType(int fontOffset) {
            this.fontOffset = fontOffset;
        }
    }

    public static void bindFont(Consumer<Font> setFont, FontType type) {
        int fontSize = UISettings.getInstance().getFontSize() + type.fontOffset;


        String fontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
        setFont.accept(new Font(fontFamily, fontSize));


        ApplicationManager.getApplication().getMessageBus().connect().subscribe(UISettingsListener.TOPIC, (UISettingsListener) source -> {
            int newFontSize = source.getFontSize() + type.fontOffset;

            String newFontFamily = EditorColorsManager.getInstance().getGlobalScheme().getEditorFontName();
            int finalNewFontSize = newFontSize;
            Platform.runLater(() -> setFont.accept(new Font(newFontFamily, finalNewFontSize)));
        });
    }

    public static void bindFont(TextInputControl node, FontType isTitle) {
        bindFont(node::setFont, isTitle);
    }

    public static void bindFont(TextInputControl node) {
        bindFont(node::setFont, FontType.STANDARD);
    }

    public static void bindFont(Labeled node, FontType isTitle) {
        bindFont(node::setFont, isTitle);
    }

    public static void bindFont(Labeled node) {
        bindFont(node::setFont, FontType.STANDARD);
    }

}
