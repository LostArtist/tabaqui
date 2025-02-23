package extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SnippetExtractor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ArrayNode snippetsArray = objectMapper.createArrayNode();
    private static final String OUTPUT_FILE = "snippets_dataset.json";

    public static void main(String[] args) throws IOException {
        String sourceCodeDir = System.getProperty("user.home") + "/code/java/camel/components/camel-kafka"; //source directory with camel project

        Files.walk(Paths.get(sourceCodeDir)) //look for java classes in source directory
                .filter(Files::isRegularFile) // filter regular files
                .filter(path -> path.toString().endsWith(".java")) // filter java files
                .forEach(SnippetExtractor::processFile); // parse the file for snippet annotation in java file

        // write snippet array to snippets dataset
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(OUTPUT_FILE), snippetsArray);
    }

    private static void processFile(Path path) {
        try {
            String content = Files.readString(path); //read path to the camel project

            //regex takes annotation text as an instruction and code snippet with explanation as an output
            String regex = "@AiSnippet\\(\"([^\"]*)\"\\)\\s*([\\s\\S]*?)(?=\\bendOfSnippet\\b)";
            Pattern pattern = Pattern.compile(regex); // represent regex as a pattern
            Matcher matcher = pattern.matcher(content); // find matches

            while (matcher.find()) { //append instruction and code snippet to corresponding category
                String instruction = matcher.group(1).trim(); // question
                String input = "";
                String output = matcher.group(2).trim(); // answer

                saveSnippet(instruction, input, output); //save the obtained snippet
                saveSnippet("Come up with the questions that can be asked about this code: " + output, input, "");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveSnippet(String description, String methodName, String methodBody) {
        ObjectNode snippetNode = objectMapper.createObjectNode(); //map obtained snippets
        snippetNode.put("instruction", description);
        snippetNode.put("input", "");

        //placeholder for future data
        snippetNode.put("output", methodName + "() {\n" + methodBody + "\n}");

        snippetsArray.add(snippetNode); //add to the JSON array

    }
}