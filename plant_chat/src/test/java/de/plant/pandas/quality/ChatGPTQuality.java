package de.plant.pandas.quality;

import de.plant.pandas.Keys;
import de.plant.pandas.llm.LLM;
import de.plant.pandas.llm.Message;
import de.plant.pandas.llm.MessageRole;
import de.plant.pandas.llm.OpenAILLM;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ChatGPTQuality {


    @SneakyThrows
    public static void main(String[] args) {
        LLM llm = new OpenAILLM(Keys.getProperty("OPENAI_KEY"), OpenAILLM.OpenAIType.GPT4);

        Map<String, Map<String, String>> results = new HashMap<>();
        Map<String, String> tasks = new HashMap<>();
        for (File model : new File("results").listFiles()) {
            if (model.getName().equals("points") || model.getName().equals("venv") || model.getName().equals(".idea") || model.getName().equals("plotter.py") || model.isFile()) {
                continue;
            }
            for (File subfolder : model.listFiles()) {
                String subfolderName = subfolder.getName();
                if (!results.containsKey(subfolderName)) {
                    results.put(subfolderName, new HashMap<>());
                    tasks.put(subfolderName, new String(Files.readAllBytes(Paths.get(subfolder.getPath() + "/question.txt"))));
                }

                Map<String, String> resultsPerModel = results.get(subfolderName);
                StringBuilder builder = new StringBuilder();

                for (File umlFile : subfolder.listFiles()) {
                    if (umlFile.getPath().endsWith(".puml")) {
                        builder.append(umlFile.getName() + "\n");
                        String content = new String(Files.readAllBytes(Paths.get(umlFile.getPath())));
                        builder.append(content + "\n\n");
                    }
                }

                resultsPerModel.put(model.getName(), builder.toString());
            }
        }

        for (Map.Entry<String, Map<String, String>> umls : results.entrySet()) {
            Thread.sleep(1000 * 30);
            String name = umls.getKey();
            String question = tasks.get(name);
            List<Map.Entry<String, String>> umlFromModels = new ArrayList<>(umls.getValue().entrySet());
            Collections.shuffle(umlFromModels);

            String prompt = "You are a UML teacher. You wrote an exam in which students have to answer the following question: " + question + "\n" +
                    "Your task is now to grade the students from $1$ (very bad) to $10$ (very good). You should award points based on the following aspects: naming of methods, classes, etc., simplicity of the diagram, types of attributes used, correctness to the original task, and correct use of relationships between classes. Please also explain your scoring.";

            StringBuilder answers = new StringBuilder();

            StringBuilder order = new StringBuilder();
            for (int i = 0; i < umlFromModels.size(); i++) {
                Map.Entry<String, String> uml = umlFromModels.get(i);
                answers.append("User").append(i + 1).append(":\n").append(uml.getValue()).append("\n\n");

                order.append("User").append(i + 1).append("=").append(uml.getKey()).append("\n");

            }

            List<Message> messages = new ArrayList<>();
            messages.add(new Message(prompt, MessageRole.ASSISTANT));
            messages.add(new Message(answers.toString(), MessageRole.HUMAN));

            String answer = llm.prompt(messages, Collections.emptyList(), 7000);

            String resultFolder = "results/points";
            new File(resultFolder).mkdirs();

            Files.write(Paths.get(resultFolder + "/" + name + ".txt"), (order + "\n\n" + answer).getBytes());
        }
    }
}
