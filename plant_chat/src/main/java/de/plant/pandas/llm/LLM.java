package de.plant.pandas.llm;

/**
 * The LLM (Large Language Model) interface represents a language model capable of completing text prompts.
 * It provides a method to interact with the language model and generate text based on the given input.
 */
public interface LLM {

    /**
     * Generates a completion for the given input prompt.
     *
     * @param input the input prompt to be completed by the language model.
     * @return the generated completion as a string.
     */
    String prompt(String input);
}
