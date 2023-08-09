package de.plant.pandas.translation;

public interface TranslatorService {

    TranslationResult translateToLanguage(String input, String lang, boolean forceTranslation);

}
