package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlantChatBotUI implements ToolWindowFactory {
    JTextArea chatMessagesArea = new JTextArea();
    JProgressBar progressBar = new PlantProgressBar();

    private void addChatMessage(Message message) {

        ApplicationManager.getApplication().invokeLater(() -> {
            String type = switch (message.getMessageType()) {
                case HUMAN -> "Client";
                case ASSISTANT -> "\uD83D\uDC3C\uD83E\uDEB4PlantPanadas";
                case SYSTEM -> "";
            };
            chatMessagesArea.append(type + ":\n");
            chatMessagesArea.append(message.getContent() + "\n\n");
            chatMessagesArea.setCaretPosition(chatMessagesArea.getDocument().getLength());

            if (message.getMessageType() == MessageRole.ASSISTANT) {
                progressBar.setVisible(false);
            }
        });
    }


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        panel.add(progressBar, BorderLayout.NORTH);

        chatMessagesArea.setLineWrap(true);
        chatMessagesArea.setWrapStyleWord(true);
        chatMessagesArea.setEditable(false);

        JBScrollPane chatScrollPane = new JBScrollPane(chatMessagesArea);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTextArea textArea = new JTextArea();

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.setBorder(BorderFactory.createCompoundBorder(
                textArea.getBorder(),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));

        textArea.setMargin(JBUI.insets(10)); // 10 pixels margin all around

        textAreaPanel.add(textArea, BorderLayout.CENTER);
        panel.add(textAreaPanel, BorderLayout.SOUTH);
        UMLChatBotProcessor umlChatBotProcessor = new UMLChatBotProcessor(this::addChatMessage);

        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (e.isShiftDown()) {
                        textArea.append("\n");
                    } else {
                        progressBar.setVisible(true);
                        String text = textArea.getText();
                        umlChatBotProcessor.startChat(project, text);
                        textArea.setText("");

                        e.consume();
                    }
                }
            }
        });


        // Create a content with the panel and add it to the tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
