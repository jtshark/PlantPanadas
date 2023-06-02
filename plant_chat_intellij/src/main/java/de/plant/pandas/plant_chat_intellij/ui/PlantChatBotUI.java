package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.plant_chat_intellij.ui.settings.PlantChatSettings;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.util.Collections;

public class PlantChatBotUI implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Create a panel with a text field
        JPanel panel = new JPanel();
        JTextField textField = new JTextField(20);
        textField.addActionListener((e) -> {
            String text = textField.getText();
            UMLChatBot umlChatBot = new UMLChatBotImpl(new OpenAILLM(PlantChatSettings.getInstance().openAIToken));
            textField.setText("");
            String answer = umlChatBot.askQuestion(Collections.emptyList(),text);

            VirtualFile baseDir =
                    ProjectUtil.guessProjectDir(project);
            if(baseDir != null)
            {
                PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(baseDir);
                String fileName = "newFile.puml";

                ApplicationManager.getApplication().runWriteAction(()->{
                    PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(fileName, FileTypeManager.getInstance().getFileTypeByExtension("puml"), answer);

                    psiFile = (PsiFile) psiDirectory.add(psiFile);

                    VirtualFile virtualFile = psiFile.getVirtualFile();
                    if(virtualFile != null)
                    {
                        FileEditorManager.getInstance(project).openFile(virtualFile, true);
                    }
                });


            }

        });
        panel.add(textField);

        // Create a content with the panel and add it to the tool window
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
