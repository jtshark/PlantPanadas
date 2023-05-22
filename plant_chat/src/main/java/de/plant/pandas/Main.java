package de.plant.pandas;

import de.plant.pandas.llm.OpenAILLM;
import de.plant.pandas.llm.LLM;

public class Main {
    public static void main(String[] args) {
        LLM llm = new OpenAILLM();
        String umlDiagram = llm.prompt("create a plantuml class diagram for a pizza shop including a customer");
        System.out.println(umlDiagram);
    }
}