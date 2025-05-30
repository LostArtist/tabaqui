package org.tabaqui.extractor;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import picocli.CommandLine;


/**
 * Main extractor class with the call() method
 */
@CommandLine.Command(name = "tabaqui-extractor", mixinStandardHelpOptions = true, version = "1.0",
        description = "Extracts annotated code snippets into JSON or YAML.")
public class SnippetExtractor implements Callable<Integer> {

    @CommandLine.Option(names = {"-p", "--project"}, description = "Project path to scan", required = true)
    private Path projectPath; // path to the project you want to extract snippets from

    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format: json or yaml", defaultValue = "json")
    private String format; // format of the output file

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SnippetExtractor()).execute(args);

        // the program will close after finishing
        System.exit(exitCode);
    }

    // first method to be called after triggering .jar file
    @Override
    public Integer call() {
        try {
            SnippetService.extractSnippets(projectPath.toAbsolutePath(), format.toLowerCase());
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return 1;
        }
        return 0;
    }
}