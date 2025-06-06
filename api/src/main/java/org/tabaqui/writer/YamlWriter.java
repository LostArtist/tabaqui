package org.tabaqui.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.tabaqui.model.CodeSnippet;

/**
 * File writer class for yaml files
 */
public class YamlWriter {

    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static void write(List<CodeSnippet> snippets, Path path) throws IOException {
        Files.write(path, mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(snippets));
    }
}
