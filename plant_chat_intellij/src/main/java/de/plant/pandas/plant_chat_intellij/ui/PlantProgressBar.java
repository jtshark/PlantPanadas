package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class PlantProgressBar extends JProgressBar {
    @Override
    public void setUI(ProgressBarUI ui) {
        super.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return JBColor.namedColor("ProgressBar.selectionBackground",
                        new JBColor(Color.BLACK, Color.WHITE));
            }

            @Override
            protected Color getSelectionForeground() {
                return JBColor.namedColor("ProgressBar.selectionForeground",
                        new JBColor(Color.BLACK, Color.WHITE));
            }

            @Override
            protected void startAnimationTimer() {
                super.startAnimationTimer();
                UIManager.put("ProgressBar.foreground", JBColor.namedColor("ProgressBar.hightlightForeground",
                        new JBColor(Color.BLUE, Color.YELLOW)));
                UIManager.put("ProgressBar.selectionBackground", JBColor.namedColor("ProgressBar.selectionBackground",
                        new JBColor(Color.BLUE, Color.YELLOW)));
                UIManager.put("ProgressBar.selectionForeground", JBColor.namedColor("ProgressBar.selectionForeground",
                        new JBColor(Color.BLUE, Color.YELLOW)));
            }

            @Override
            protected void stopAnimationTimer() {
                super.stopAnimationTimer();
                UIManager.put("ProgressBar.foreground", JBColor.namedColor("ProgressBar.foreground",
                        new JBColor(Color.GRAY, Color.DARK_GRAY)));
                UIManager.put("ProgressBar.selectionBackground", JBColor.namedColor("ProgressBar.selectionBackground",
                        new JBColor(Color.GRAY, Color.DARK_GRAY)));
                UIManager.put("ProgressBar.selectionForeground", JBColor.namedColor("ProgressBar.selectionForeground",
                        new JBColor(Color.GRAY, Color.DARK_GRAY)));
            }
        });
    }
}
