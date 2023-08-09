package de.plant.pandas.translation;

import de.plant.pandas.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslatorServiceDeepLTest {
    private TranslatorServiceDeepL translatorService;

    @BeforeEach
    public void setUp() {
        translatorService = new TranslatorServiceDeepL(Keys.getProperty("DEEPL_KEY"));
    }

    @Test
    public void testTranslationToEnglish() {
        TranslationResult result = translatorService.translateToLanguage("Hallo wie geht es dir?", "EN-US", true);
        assertEquals("Hello how are you?", result.getText());
        assertEquals("de", result.getLang());
    }

    @Test
    public void testTranslationToGerman() {
        TranslationResult result = translatorService.translateToLanguage("Hello how are you?", "de", true);
        assertEquals("Hallo, wie geht es Ihnen?", result.getText());
        result = translatorService.translateToLanguage("Hello how are you?", "de", false);
        assertEquals("Hallo, wie geht es Ihnen?", result.getText());
    }

    @Test
    public void testNoTranslationOnForce() {
        TranslatorService translatorService = new TranslatorServiceDeepL("ada");
        TranslationResult result = translatorService.translateToLanguage("Hello how are you?", "EN-US", false);
        assertEquals("Hello how are you?", result.getText());
        assertEquals("EN-US", result.getLang());
    }

    @Test
    public void testBlankOrEmptyToken() {
        TranslatorService translatorService = new TranslatorServiceDeepL(" ");
        TranslationResult result = translatorService.translateToLanguage("Hallo wie geht es dir?", "EN-US", true);
        assertEquals("Hallo wie geht es dir?", result.getText());
        assertEquals("EN-US", result.getLang());
        translatorService = new TranslatorServiceDeepL(null);
        result = translatorService.translateToLanguage("Hallo wie geht es dir?", "EN-US", true);
        assertEquals("Hallo wie geht es dir?", result.getText());
        assertEquals("EN-US", result.getLang());
    }
}
