package de.plant.pandas.plant_chat_intellij.logic;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import de.plant.pandas.chatbot.*;
import de.plant.pandas.llm.*;
import de.plant.pandas.plant_chat_intellij.ui.settings.PlantChatSettings;
import de.plant.pandas.translation.TranslatorServiceDeepL;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UMLChatBotProcessor {
    private final List<Message> _currentMessages = new ArrayList<>();
    private final Consumer<Message> addChatMessage;

    private final SimpleObjectProperty<GenerationStage> generationStageProperty = new SimpleObjectProperty<>(null);


    public UMLChatBotProcessor(Consumer<Message> addChatMessage) {
        this.addChatMessage = addChatMessage;

        StageListener.getInstance().registerListener(newValue -> Platform.runLater(() -> generationStageProperty.set(newValue)), true);
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

                CommandProcessor.getInstance().executeCommand(project, () -> {
                    for (Map.Entry<String, String> filenameUmlDiagramPair : answer.entrySet()) {

                        String filename = filenameUmlDiagramPair.getKey();
                        String uml = filenameUmlDiagramPair.getValue();
                        addUML(project, filename, uml);
                    }
                }, "Plant Panadas Generation", null);

            });
        });
    }

    private void processQuestion(UMLChatBotResults.ChatBotQuestions result) {
        Message question = new Message(result.getQuestion(), MessageRole.ASSISTANT);
        addChatMessage(question, false);
    }

    private Collection<String> getPumlFiles(Project project) {
        Collection<VirtualFile> currentDiagramsFiles = FilenameIndex.getAllFilesByExt(project, "puml");

        Collection<String> currentDiagramStrings = currentDiagramsFiles.stream().map((file) -> {
            try {
                return file.getName() + "\n" + VfsUtil.loadText(file);
            } catch (IOException ex) {
                return "";
            }
        }).collect(Collectors.toList());
        return currentDiagramStrings;
    }

    private LLM getLLM() throws MalformedURLException, URISyntaxException {
        return switch (PlantChatSettings.getInstance().llmType) {
            case CHATGPT -> new OpenAILLM(PlantChatSettings.getInstance().openAIToken, OpenAILLM.OpenAIType.CHATGPT);
            case GPT4 -> new OpenAILLM(PlantChatSettings.getInstance().openAIToken, OpenAILLM.OpenAIType.GPT4);
            case LLaMA -> new LLaMALLM(new URI(PlantChatSettings.getInstance().llamaURL).toURL());
        };
    }

    public void startChat(Project project, String input) {
        ApplicationManager.getApplication().runReadAction(() -> {
            Collection<String> currentDiagramStrings = getPumlFiles(project);
            ApplicationManager.getApplication().executeOnPooledThread(
                    () -> {
                        Message ourMessage = new Message(input, MessageRole.HUMAN);
                        addChatMessage(ourMessage, true);
                        UMLChatBot umlChatBot;
                        if (PlantChatSettings.getInstance().llmType == PlantChatSettings.LLMType.LLaMA) {
                            umlChatBot = new LLaMAChatBot();
                        } else {
                            umlChatBot = new UMLChatBotIoPImpl();
                        }

                        UMLChatBotResults result = null;
                        try {
                            result = umlChatBot.askQuestion(_currentMessages, currentDiagramStrings, AskQuestionParameter.builder()
                                    .translatorService(new TranslatorServiceDeepL(PlantChatSettings.getInstance().deepLToken))
                                    .level(PlantChatSettings.getInstance().questionSetting)
                                    .llm(getLLM())
                                    .build());
                        } catch (IOException | URISyntaxException e) {
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
        });
    }


    public GenerationStage getGenerationStage() {
        return generationStageProperty.get();
    }

    public ReadOnlyObjectProperty<GenerationStage> generationStageProperty() {
        return generationStageProperty;
    }
}
