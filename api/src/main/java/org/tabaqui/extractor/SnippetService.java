package org.tabaqui.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.tabaqui.model.CodeSnippet;
import org.tabaqui.writer.JsonWriter;
import org.tabaqui.writer.YamlWriter;

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
                return;
            }
        }

        if (allSnippets.isEmpty()) {
            System.out.println("ℹ️ No annotated snippets found in the provided project.");
            return;
        }

        Path output = Paths.get("snippets." + (format.equals("yaml") ? "yaml" : "json"));

        if ("yaml".equals(format)) {
            YamlWriter.write(allSnippets, output);
        } else {
            JsonWriter.write(allSnippets, output);
        }

        System.out.println("✅ Snippets successfully extracted to: " + output.toAbsolutePath());
    }
}
