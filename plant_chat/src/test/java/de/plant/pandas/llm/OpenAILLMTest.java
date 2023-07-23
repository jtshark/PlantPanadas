package de.plant.pandas.llm;


import de.plant.pandas.Keys;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenAILLMTest {
    @SneakyThrows
    @Test
    public void sendMessageToGPT3() {
        LLM llm = new OpenAILLM(Keys.getProperty("OPENAI_KEY"), OpenAILLM.OpenAIType.CHATGPT);
        String response = llm.prompt(List.of(new Message("Response with: \"Okay.\"", MessageRole.HUMAN)), Collections.emptyList(), 100);
        assertEquals("Okay.", response);
    }

    @SneakyThrows
    @Test
    public void sendMessageToGPT4() {
        LLM llm = new OpenAILLM(Keys.getProperty("OPENAI_KEY"), OpenAILLM.OpenAIType.GPT4);
        String response = llm.prompt(List.of(new Message("Response with: \"Okay.\"", MessageRole.HUMAN)), Collections.emptyList(), 100);
        assertEquals("Okay.", response);
    }
}
