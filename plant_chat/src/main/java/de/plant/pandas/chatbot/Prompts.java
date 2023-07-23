package de.plant.pandas.chatbot;

import java.util.Collection;

public interface Prompts {

    String getIOPrompt(Collection<String> plantUMLs, DegreeOfQuestionsFromExperts level);

    String getDiscussionPrompt(Collection<String> plantUMLs, DegreeOfQuestionsFromExperts level);

    String getStepByStepPrompt();

    String generatePlantUMLPrompt();


    String foundNoUMLPrompt();
}
