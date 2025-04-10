import configuration.ExtractorConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTest {
    @Test
    void testDefaultConfig() {
        ExtractorConfiguration config = new ExtractorConfiguration();
        assertEquals("/code/java/camel/components/camel-kafka", config.getProjectRelativePath());
        assertTrue(config.getFileExtensions().contains(".java"));
    }
}
