package de.plant.pandas.translation;

import com.deepl.api.DeepLException;
import com.deepl.api.TextResult;
import com.deepl.api.Translator;

public class TranslatorServiceDeepL implements TranslatorService {

    private final String deepLToken;

    public TranslatorServiceDeepL(String deepLToken) {
        this.deepLToken = deepLToken;
    }

    @Override
    public TranslationResult translateToLanguage(String input, String lang, boolean forceTranslation) {
        if (deepLToken != null && !deepLToken.isBlank()) {
            if (!lang.toLowerCase().contains("en") || forceTranslation) {
                try {
                    Translator translator = new Translator(deepLToken);
                    TextResult textResult = translator.translateText(input, null, lang);
                    return new TranslationResult(textResult.getText(), textResult.getDetectedSourceLanguage());
                } catch (DeepLException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return new TranslationResult(input, lang);
    }
}
