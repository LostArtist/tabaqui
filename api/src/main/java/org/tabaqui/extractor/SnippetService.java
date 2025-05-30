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

/**
 * File management (parsing) and exception check class
 */
public class SnippetService {

    public static void extractSnippets(Path rootPath, String format) throws IOException {
        CodeExtractor extractor = new CodeExtractor();

        // snippet list
        List<CodeSnippet> allSnippets = new ArrayList<>();

        // searches for all .java files
        List<Path> javaFiles = Files.walk(rootPath)
                .filter(p -> p.toString().endsWith(".java"))
                .collect(Collectors.toList());

        // searches for annotations present in .java files
        for (Path file : javaFiles) {
            try {
                allSnippets.addAll(extractor.extract(file));
            } catch (Exception e) {
                System.err.println("⚠️ Failed to parse: " + file + " - " + e.getMessage());
                return;
            }
        }

        // checks if the directory doesn't have any required annotations
        if (allSnippets.isEmpty()) {
            System.out.println("ℹ️ No annotated snippets found in the provided project.");
            return;
        }

        // defines name and format for the output file
        Path output = Paths.get("snippets." + (format.equals("yaml") ? "yaml" : "json"));

        // calls the appropriate constructor
        if ("yaml".equals(format)) {
            YamlWriter.write(allSnippets, output);
        } else {
            JsonWriter.write(allSnippets, output);
        }

        System.out.println("✅ Snippets successfully extracted to: " + output.toAbsolutePath());
    }
}
