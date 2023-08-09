package de.plant.pandas.chatbot;

import de.plant.pandas.llm.LLM;
import de.plant.pandas.translation.TranslatorService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

@AllArgsConstructor
@Builder
@Getter
public class AskQuestionParameter {
    private final LLM llm;
    private final DegreeOfQuestionsFromExperts level;
    private final TranslatorService translatorService;

    private final Consumer<GenerationStage> onStageChange;

}
