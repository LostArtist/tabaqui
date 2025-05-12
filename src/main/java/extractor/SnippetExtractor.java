package extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import configuration.ExtractorConfiguration;
import model.CodeSnippet;

public class SnippetExtractor {

    private String path;

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.err.println("Usage: java -jar extractor.jar <relative_project_path>");
            System.exit(1);
        }

        Path userHome = Paths.get(System.getProperty("user.home"));
        Path projectPath = userHome.resolve(args[0]);
        if (!Files.exists(projectPath)) {
            System.err.println("Invalid path: " + projectPath);
            System.exit(2);
        }

        ParserConfiguration configuration = new ParserConfiguration();
        configuration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(configuration); // takes a configuration file
        ExtractorConfiguration config = new ExtractorConfiguration();

        List<CodeSnippet> allSnippets = new ArrayList<>(); // snippet list
        CodeExtractor extractor = new CodeExtractor();

        Files.walk(projectPath) // going through all files mentioned in configuration
                .filter(path -> config.getFileExtensions().stream().anyMatch(ext -> path.toString().endsWith(ext)))
                .forEach(path -> {
                    try {
                        allSnippets.addAll(extractor.extract(path)); //add all appropriate snippets
                    } catch (IOException e) {
                        // log error may be implemented
                    }
                });

        String outputFileExt = ".json"; // read from config if available

        // choose the appropriate file format in config
        ObjectMapper mapper = outputFileExt.equals(".yaml") ? new ObjectMapper(new YAMLFactory()) : new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("snippets" + outputFileExt), allSnippets);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}