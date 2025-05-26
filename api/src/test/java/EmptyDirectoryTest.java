import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.tabaqui.extractor.SnippetService;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmptyDirectoryTest {

    @Test
    void testEmptyProjectOutput() throws Exception {
        Path tempDir = Files.createTempDirectory("empty-project");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        SnippetService.extractSnippets(tempDir, "json");

        String output = outContent.toString().trim();
        assertTrue(output.contains("No annotated snippets found"), "Expected message about no snippets found");

        System.setOut(System.out);
        Files.deleteIfExists(tempDir);
    }
}
