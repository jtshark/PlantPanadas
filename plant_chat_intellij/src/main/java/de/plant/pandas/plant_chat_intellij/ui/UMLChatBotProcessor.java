package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import de.plant.pandas.chatbot.DegreeOfQuestionsFromExperts;
import de.plant.pandas.chatbot.UMLChatBot;
import de.plant.pandas.chatbot.UMLChatBotImpl;
import de.plant.pandas.chatbot.UMLChatBotResults;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.plant_chat_intellij.ui.settings.PlantChatSettings;
import de.plant.pandas.plant_chat_intellij.ui.settings.PlantChatSettingsUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UMLChatBotProcessor {
    private final List<Message> _currentMessages = new ArrayList<>();
    private final UMLChatBot umlChatBot = new UMLChatBotImpl(new OpenAILLM(PlantChatSettings.getInstance().openAIToken, OpenAILLM.OpenAIType.CHATGPT));
    private Consumer<Message> addChatMessage;


    public UMLChatBotProcessor(Consumer<Message> addChatMessage) {
        this.addChatMessage = addChatMessage;
    }

    private void addChatMessage(Message message, boolean addToHistory) {
        if (addChatMessage != null) {
            addChatMessage.accept(message);
        }
        if (addToHistory)
            _currentMessages.add(message);
    }


    private void addUML(Project project, String filename, String uml) {
        VirtualFile baseDir =
                ProjectUtil.guessProjectDir(project);
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

    private void processUMLResult(Project project, UMLChatBotResults.GeneratedUML result) {
        Map<String, String> answer = result.getUmlResults();
        _currentMessages.clear();
        addChatMessage.accept(new Message("Here is the generated UML. Enjoy", MessageRole.ASSISTANT));
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                for (Map.Entry<String, String> filenameUmlDiagramPair : answer.entrySet()) {

                    String filename = filenameUmlDiagramPair.getKey();
                    String uml = filenameUmlDiagramPair.getValue();
                    addUML(project, filename, uml);
                }
            });
        });
    }

    private void processQuestion(UMLChatBotResults.ChatBotQuestions result) {
        Message question = new Message(result.getQuestion(), MessageRole.ASSISTANT);
        addChatMessage(question, false);
    }

    private Collection<String> getPumlFiles(Project project) {
        Collection<VirtualFile> currentDiagramsFiles = PlantChatHelper.findPumlFiles(project);

        Collection<String> currentDiagramStrings = currentDiagramsFiles.stream().map((file) -> {
            try {
                return file.getName() + "\n" + VfsUtil.loadText(file);
            } catch (IOException ex) {
                return "";
            }
        }).collect(Collectors.toList());
        return currentDiagramStrings;
    }

    public void startChat(Project project, String input) {
        Collection<String> currentDiagramStrings = getPumlFiles(project);
        ApplicationManager.getApplication().executeOnPooledThread(
                () -> {
                    Message ourMessage = new Message(input, MessageRole.HUMAN);
                    addChatMessage(ourMessage, true);

                    UMLChatBotResults result = null;
                    try {
                        result = umlChatBot.askQuestion(currentDiagramStrings, _currentMessages, PlantChatSettings.getInstance().questionSetting);
                    } catch (IOException e) {
                        _currentMessages.clear();
                        addChatMessage.accept(new Message("Ohhhh it seems like pandas do not like you.", MessageRole.ASSISTANT));
                        addChatMessage.accept(new Message("Ohh no what have I done?", MessageRole.HUMAN));
                        addChatMessage.accept(new Message(e.toString(), MessageRole.ASSISTANT));

                    }
                    if (result instanceof UMLChatBotResults.ChatBotQuestions) {
                        processQuestion((UMLChatBotResults.ChatBotQuestions) result);
                    } else if (result instanceof UMLChatBotResults.GeneratedUML) {
                        processUMLResult(project, (UMLChatBotResults.GeneratedUML) result);
                    }
                }
        );
    }

}
