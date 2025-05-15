package org.tabaqui.extractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import org.tabaqui.model.CodeSnippet;

public class CodeExtractor {

    static {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
    }

    public List<CodeSnippet> extract(Path filePath) throws IOException {

        List<CodeSnippet> snippets = new ArrayList<>();
        CompilationUnit cu = StaticJavaParser.parse(filePath);

        // Extract methods
        cu.findAll(MethodDeclaration.class).forEach(member ->
                extractIfAnnotated(snippets, member)
        );

        // Extract fields
        cu.findAll(FieldDeclaration.class).forEach(member ->
                extractIfAnnotated(snippets, member)
        );

        return snippets;
    }

    private void extractIfAnnotated(List<CodeSnippet> snippets, BodyDeclaration<?> member) {
        Optional<AnnotationExpr> annotationOpt = member.getAnnotationByName("AiSnippet");
        if (annotationOpt.isPresent() && annotationOpt.get() instanceof NormalAnnotationExpr annotation) {
            String question = getMemberValue(annotation, "question");
            String answer = getMemberValue(annotation, "answer");

            String code = member.toString()
                    .replace(annotation.toString() + "\n", "")
                    .replace(annotation.toString(), "");

            String output = answer.isEmpty() ? code : answer + ": " + code;
            snippets.add(new CodeSnippet(question, output));
        }
    }

    private String getMemberValue(NormalAnnotationExpr annotation, String key) {
        return annotation.getPairs().stream()
                .filter(p -> p.getNameAsString().equals(key))
                .map(MemberValuePair::getValue)
                .map(val -> val.toString().replaceAll("^\"|\"$", "")) // remove quotes
                .findFirst()
                .orElse("");
    }
}
