import org.tabaqui.annotation.AiSnippet;

public class AiSnippetOnMethod {
    @AiSnippet(question = "What does this method do?", answer = "It returns the number 42.")
    public int getNumber() {
        return 42;
    }
}