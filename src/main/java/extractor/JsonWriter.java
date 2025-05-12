package extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import model.CodeSnippet;

public class JsonWriter {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void write(List<CodeSnippet> snippets, Path path) throws IOException {
        Files.write(path, mapper.writeValueAsBytes(snippets));
    }
}
