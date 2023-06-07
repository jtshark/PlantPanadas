package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.plant_chat_intellij.ui.settings.PlantChatSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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

            Collection<VirtualFile> currentDiagramsFiles = PlantChatHelper.findPumlFiles(project);

            Collection<String> currentDiagramStrings = currentDiagramsFiles.stream().map((file) -> {
                try {
                    return file.getName() + "\n" + VfsUtil.loadText(file);
                } catch (IOException ex) {
                    return "";
                }
            }).collect(Collectors.toList());


            Map<String, String> answer = umlChatBot.askQuestion(currentDiagramStrings, text);

            VirtualFile baseDir =
                    ProjectUtil.guessProjectDir(project);
            if (baseDir != null) {


                ApplicationManager.getApplication().runWriteAction(() -> {
                    for (Map.Entry<String, String> filenameUmlDiagramPair : answer.entrySet()) {

                        String filename = filenameUmlDiagramPair.getKey();
                        String uml = filenameUmlDiagramPair.getValue();

                        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(baseDir);

                        PsiFile psiFile = psiDirectory.findFile(filename);
                        if (psiFile != null) {
                            Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                            if (document != null) {
                                document.setText(uml);
                            }
                        } else {
                            psiFile = PsiFileFactory.getInstance(project).createFileFromText(filename, FileTypeManager.getInstance().getFileTypeByExtension("puml"), uml);
                            psiFile = (PsiFile) psiDirectory.add(psiFile);
                        }


                        VirtualFile virtualFile = psiFile.getVirtualFile();
                        if (virtualFile != null) {
                            FileEditorManager.getInstance(project).openFile(virtualFile, true);
                        }
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
