package de.plant.pandas.plant_chat_intellij.ui.nodes;


import com.intellij.ide.ui.UISettings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatInputField extends TextArea {
    public ChatInputField() {
        setWrapText(true);
        setPromptText("Type your question here...");

        FontHelper.bindFont(this);

        textProperty().addListener((observable, oldValue, newValue) -> {
            int visualLinesCount = computeVisualLinesCount(newValue, getWidth());
            setPrefRowCount(visualLinesCount);
        });
        setPrefRowCount(1);
        VBox.setMargin(this, new Insets(16, 16, 16, 16));

        Platform.runLater(() -> {
            Node node = this.lookup(".scroll-bar");
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                scrollBar.setOpacity(0);
                scrollBar.setMinHeight(0);
                scrollBar.setMaxHeight(0);
                scrollBar.setPrefHeight(0);
                scrollBar.setMinWidth(0);
                scrollBar.setMaxWidth(0);
                scrollBar.setPrefWidth(0);
            }
        });

    }

    private int computeVisualLinesCount(String text, double lineWidth) {
        Text helper = new Text();
        helper.setFont(getFont());
        String[] lines = text.split("\n");
        int visualLinesCount = lines.length;  // start with number of physical lines
        for (String line : lines) {
            helper.setText(line);
            int visualLinesForThisLine = (int) Math.ceil((helper.getLayoutBounds().getWidth()) / lineWidth);
            visualLinesCount += visualLinesForThisLine - 1;  // subtract 1 because we've already counted this as one physical line
        }
        return visualLinesCount;
    }
}
