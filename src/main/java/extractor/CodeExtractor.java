package extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import annotations.Annotations;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import model.CodeSnippet;

public class CodeExtractor {
    public List<CodeSnippet> extract(Path filePath) throws IOException {
        List<CodeSnippet> snippets = new ArrayList<>(); // list for saved snippets
        CompilationUnit cu = StaticJavaParser.parse(filePath);
        cu.findAll(MethodDeclaration.class).forEach(method -> { //method finding func.
            Optional<String> question = method.getAnnotationByClass(Annotations.AiSnippetQuestion.class) // question annotation
                    .flatMap(a -> a.asSingleMemberAnnotationExpr().getMemberValue().toStringLiteralExpr().map(expr -> expr.getValue()));
            Optional<String> answer = method.getAnnotationByClass(Annotations.AiSnippetAnswer.class) // answer annotation
                    .flatMap(a -> a.asSingleMemberAnnotationExpr().getMemberValue().toStringLiteralExpr().map(expr -> expr.getValue()));
            if (question.isPresent() || answer.isPresent()) {
                String instruction = question.orElse(""); // if question is present we save it
                String code = method.getDeclarationAsString(false, false, false) +
                        " " + method.getBody().map(Object::toString).orElse(";"); // code that we marked as a snippet
                String output = answer.map(ans -> ans + ": " + code).orElse(code); // if answer is present we save it, else only a code part
                snippets.add(new CodeSnippet(instruction, output));
            }
        });
        return snippets;
    }
}
