package de.plant.pandas.plant_chat_intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;

import java.util.Collection;

public class PlantChatHelper {
    public static Collection<VirtualFile> findPumlFiles(Project project) {
        return FilenameIndex.getAllFilesByExt(project, "puml");
    }
}
