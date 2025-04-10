package configuration;

import java.util.Arrays;
import java.util.List;

public class ExtractorConfiguration {

    private final String projectRelativePath;
    private final List<String> fileExtensions;

    public ExtractorConfiguration() {
        this.projectRelativePath = "/code/java/camel/components/camel-kafka"; //path to the project
        this.fileExtensions = Arrays.asList(".java"); // file extensions that should be searched
    }

    public String getProjectRelativePath() {
        return projectRelativePath;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
