package de.plant.pandas.plantuml;

import de.plant.pandas.plantuml.ResponseParser;
import de.plant.pandas.plantuml.ResponseParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseParserImplTest {
    private ResponseParser responseParser;

    @BeforeEach
    public void setUp() {
        responseParser = new ResponseParserImpl();
    }

    @Test
    public void simpleUMLParse() {
        Map<String, String> umls = responseParser.umlStringToMap("test.puml\n@startuml\nTest1\n@enduml");
        assertEquals(Map.of("test.puml", "@startuml\nTest1\n@enduml"), umls);
    }

    @Test
    public void simpleQuestionParse() {
        String question = responseParser.getQuestion("QUESTION: What is 1+1?");
        assertEquals("What is 1+1?", question);
    }

    @Test
    public void multipleUMLsParse() {
        Map<String, String> umls = responseParser.umlStringToMap("test.puml\n@startuml\nTest1\n@enduml\n3. abc.puml:\n\n```plantuml\n@startuml\nTest2\n@enduml");
        assertEquals(Map.of("test.puml", "@startuml\nTest1\n@enduml", "abc.puml", "@startuml\nTest2\n@enduml"), umls);
    }

    @Test
    public void someExtraText() {
        Map<String, String> umls = responseParser.umlStringToMap("Here is the generate UML:\ntest.puml\n@startuml\nTest1\n@enduml\nEnjoy!!!");
        assertEquals(Map.of("test.puml", "@startuml\nTest1\n@enduml"), umls);
    }
}
