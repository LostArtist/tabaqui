import org.tabaqui.extractor.CodeExtractor;
import org.tabaqui.model.CodeSnippet;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CodeExtractorTest {

    private final CodeExtractor extractor = new CodeExtractor();

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
                "    @AiSnippet(question=\"Test question?\", answer=\"Test answer\")\n" +
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

    @Test
    public void testAiSnippetOnClass() throws IOException {
        Path path = Path.of("src/test/resources/AiSnippetOnClass.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("What is this class for?"));
        assertTrue(snippets.get(0).getOutput().contains("This class is used for testing class-level annotation."));
    }

    @Test
    public void testAiSnippetOnMethod() throws IOException {
        Path path = Path.of("src/test/resources/AiSnippetOnMethod.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("What does this method do?"));
        assertTrue(snippets.get(0).getOutput().contains("It returns the number 42."));
    }

    @Test
    public void testAiSnippetOnField() throws IOException {
        Path path = Path.of("src/test/resources/AiSnippetOnField.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("What does this field store?"));
        assertTrue(snippets.get(0).getOutput().contains("It stores the name."));
    }

    @Test
    public void testAiAutoSnippetOnClass() throws IOException {
        Path path = Path.of("src/test/resources/AiAutoSnippetOnClass.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("Which class is responsible for this test?"));
        assertTrue(snippets.get(0).getOutput().contains("The AiAutoSnippetOnClass class, is responsible for this"));
    }

    @Test
    public void testAiAutoSnippetOnMethod() throws IOException {
        Path path = Path.of("src/test/resources/AiAutoSnippetOnMethod.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("What does this method handle?"));
        assertTrue(snippets.get(0).getOutput().contains("The handleRequest method, from the AiAutoSnippetOnMethod class"));
    }

    @Test
    public void testAiAutoSnippetOnField() throws IOException {
        Path path = Path.of("src/test/resources/AiAutoSnippetOnField.java");
        List<CodeSnippet> snippets = extractor.extract(path);

        assertEquals(1, snippets.size());
        assertTrue(snippets.get(0).getInstruction().contains("What does this field represent?"));
        assertTrue(snippets.get(0).getOutput().contains("The id field, from the AiAutoSnippetOnField class"));
    }
}
