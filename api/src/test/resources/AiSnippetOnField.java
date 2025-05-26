import org.tabaqui.annotation.AiSnippet;

public class AiSnippetOnField {
    @AiSnippet(question = "What does this field store?", answer = "It stores the name.")
    private String name;
}