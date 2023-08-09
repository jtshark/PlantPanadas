package de.plant.pandas.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TranslationResult {
    private final String text;
    private final String lang;
}
