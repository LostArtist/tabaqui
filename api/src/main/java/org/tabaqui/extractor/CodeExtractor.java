package org.tabaqui.extractor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import org.tabaqui.model.CodeSnippet;

public class CodeExtractor {

    static {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
    }

    public List<CodeSnippet> extract(Path javaFile) throws IOException {
        String content = Files.readString(javaFile);
        CompilationUnit cu = StaticJavaParser.parse(content);
        List<CodeSnippet> snippets = new ArrayList<>();

        cu.accept(new GenericVisitorAdapter<Void, Void>() {

            private Optional<String> getClassName(Node node) {
                return node.findAncestor(ClassOrInterfaceDeclaration.class).map(ClassOrInterfaceDeclaration::getNameAsString);
            }

            private String getElementCode(BodyDeclaration<?> node) {
                return node.toString()
                        .lines()
                        .filter(line -> !line.trim().startsWith("@AiSnippet") && !line.trim().startsWith("@AiAutoSnippet"))
                        .reduce("", (a, b) -> a + b + "\n").trim();
            }

            // classes and interfaces
            @Override
            public Void visit(ClassOrInterfaceDeclaration clazz, Void arg) {
                clazz.getAnnotations().forEach(anno -> {
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        String output = answer.isEmpty() ? getElementCode(clazz) : answer + ": " + getElementCode(clazz);
                        snippets.add(new CodeSnippet(question, output));
                    }

                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {class} class, is responsible for this");

                        String answer = answerTemplate
                                .replace("{class}", clazz.getNameAsString());

                        String output = answer.isEmpty() ? getElementCode(clazz) : answer + ": " + getElementCode(clazz);
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(clazz, arg);
            }

            // methods
            @Override
            public Void visit(MethodDeclaration method, Void arg) {
                method.getAnnotations().forEach(anno -> {
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        String output = answer.isEmpty() ? getElementCode(method) : answer + ": " + getElementCode(method);
                        snippets.add(new CodeSnippet(question, output));
                    }

                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {method} method, from the {class} class, is responsible for this operation");

                        String answer = answerTemplate
                                .replace("{method}", method.getNameAsString())
                                .replace("{class}", getClassName(method).orElse("Unknown"));

                        String output = answer.isEmpty() ? getElementCode(method) : answer + ": " + getElementCode(method);
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(method, arg);
            }

            // fields
            @Override
            public Void visit(FieldDeclaration field, Void arg) {
                field.getAnnotations().forEach(anno -> {
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        String output = answer.isEmpty() ? getElementCode(field) : answer + ": " + getElementCode(field);
                        snippets.add(new CodeSnippet(question, output));
                    }

                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {field} field, from the {class} class, is responsible for this configuration");

                        String answer = answerTemplate
                                .replace("{field}", field.getVariables().get(0).getNameAsString())
                                .replace("{class}", getClassName(field).orElse("Unknown"));

                        String output = answer.isEmpty() ? getElementCode(field) : answer + ": " + getElementCode(field);
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(field, arg);
            }
        }, null);

        return snippets;
    }

    private Map<String, String> parseAutoSnippetValues(AnnotationExpr anno) {
        Map<String, String> values = new HashMap<>();
        if (anno.isNormalAnnotationExpr()) {
            for (MemberValuePair pair : anno.asNormalAnnotationExpr().getPairs()) {
                values.put(pair.getNameAsString(), pair.getValue().asStringLiteralExpr().getValue());
            }
        }
        return values;
    }
}
