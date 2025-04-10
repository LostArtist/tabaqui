import extractor.CodeExtractor;
import model.CodeSnippet;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CodeExtractorTest {

    @BeforeAll
    static void setupParser() {
        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_14);
        StaticJavaParser.setConfiguration(configuration);
    }

    @Test
    void testExtractSingleSnippet() throws Exception {
        String code = "package test;\n" +
                "import com.example.annotations.*;\n" +
                "public class TestClass {\n" +
                "    @AiSnippetQuestion(\"Test question?\")\n" +
                "    @AiSnippetAnswer(\"Test answer\")\n" +
                "    public void testMethod() { System.out.println(\"Hello\"); }\n" +
                "}";
        Path tempFile = Files.createTempFile("Test", ".java");
        Files.write(tempFile, code.getBytes());

        CodeExtractor extractor = new CodeExtractor();
        List<CodeSnippet> snippets = extractor.extract(tempFile);

        assertEquals(1, snippets.size());
        CodeSnippet snippet = snippets.get(0);
        assertEquals("Test question?", snippet.getInstruction());
        assertTrue(snippet.getOutput().contains("Test answer"));

        Files.deleteIfExists(tempFile);
    }
}
