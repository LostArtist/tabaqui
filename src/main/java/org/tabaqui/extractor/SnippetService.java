package org.tabaqui.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.tabaqui.model.CodeSnippet;

public class SnippetService {
    public static void extractSnippets(Path rootPath, String format) throws IOException {
        CodeExtractor extractor = new CodeExtractor();
        List<CodeSnippet> allSnippets = new ArrayList<>();

        List<Path> javaFiles = Files.walk(rootPath)
                .filter(p -> p.toString().endsWith(".java"))
                .collect(Collectors.toList());

        for (Path file : javaFiles) {
            try {
                allSnippets.addAll(extractor.extract(file));
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse: " + file + " - " + e.getMessage());
            }
        }

        Path output = rootPath.resolve("snippets." + (format.equals("yaml") ? "yaml" : "json"));

        if ("yaml".equals(format)) {
            YamlWriter.write(allSnippets, output);
        } else {
            JsonWriter.write(allSnippets, output);
        }
    }
}
