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

/**
 * Functional part of extractor (parsing, formatting)
 */
public class CodeExtractor {

    // specifies parser configuration java level
    static {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
    }

    // the main extraction method
    public List<CodeSnippet> extract(Path javaFile) throws IOException {

        // reads all the .java files
        String content = Files.readString(javaFile);
        CompilationUnit cu = StaticJavaParser.parse(content);
        List<CodeSnippet> snippets = new ArrayList<>();

        cu.accept(new GenericVisitorAdapter<Void, Void>() {

            // finds the class or interface name
            private Optional<String> getClassName(Node node) {
                return node.findAncestor(ClassOrInterfaceDeclaration.class).map(ClassOrInterfaceDeclaration::getNameAsString);
            }

            // separates the code from the annotation
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
                    // searches for annotation occurrences
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        // separates question
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        // separates answers
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        // merges the answer with the code body
                        String output = answer.isEmpty() ? getElementCode(clazz) : answer + ": " + getElementCode(clazz);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }

                    // searches for @AiAutoSnippet annotation
                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");

                        // answer is generated using template
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {class} class, is responsible for this");

                        // replaces {class} with the actual class name
                        String answer = answerTemplate
                                .replace("{class}", clazz.getNameAsString());

                        // merges the generated answer and the code body
                        String output = answer.isEmpty() ? getElementCode(clazz) : answer + ": " + getElementCode(clazz);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(clazz, arg);
            }

            // methods
            @Override
            public Void visit(MethodDeclaration method, Void arg) {
                method.getAnnotations().forEach(anno -> {

                    // searches for annotation occurrences
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        // separates question
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        // separates answer
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        // merges the answer with the code body
                        String output = answer.isEmpty() ? getElementCode(method) : answer + ": " + getElementCode(method);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }

                    // searches for @AiAutoSnippet annotation
                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");

                        // answer is generated using template
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {method} method, from the {class} class, is responsible for this operation");

                        // replaces {method} and {class} with the actual method and class name
                        String answer = answerTemplate
                                .replace("{method}", method.getNameAsString())
                                .replace("{class}", getClassName(method).orElse("Unknown"));

                        // merges the generated answer and the code body
                        String output = answer.isEmpty() ? getElementCode(method) : answer + ": " + getElementCode(method);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(method, arg);
            }

            // fields
            @Override
            public Void visit(FieldDeclaration field, Void arg) {
                field.getAnnotations().forEach(anno -> {

                    // searches for annotation occurrences
                    if (anno.getNameAsString().equals("AiSnippet")) {
                        // separates question
                        String question = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("question"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");
                        // separates answer
                        String answer = anno.asNormalAnnotationExpr().getPairs().stream()
                                .filter(p -> p.getNameAsString().equals("answer"))
                                .map(p -> p.getValue().asStringLiteralExpr().getValue())
                                .findFirst().orElse("");

                        // merges the answer with the code body
                        String output = answer.isEmpty() ? getElementCode(field) : answer + ": " + getElementCode(field);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }

                    // searches for @AiAutoSnippet annotation
                    if (anno.getNameAsString().equals("AiAutoSnippet")) {
                        Map<String, String> values = parseAutoSnippetValues(anno);
                        String question = values.getOrDefault("question", "");

                        // answer is generated using template
                        String answerTemplate = values.getOrDefault("answerTemplate",
                                "The {field} field, from the {class} class, is responsible for this configuration");

                        // replaces {field} and {class} with the actual field and class name
                        String answer = answerTemplate
                                .replace("{field}", field.getVariables().get(0).getNameAsString())
                                .replace("{class}", getClassName(field).orElse("Unknown"));

                        // merges the generated answer and the code body
                        String output = answer.isEmpty() ? getElementCode(field) : answer + ": " + getElementCode(field);

                        // adds new snippet
                        snippets.add(new CodeSnippet(question, output));
                    }
                });
                return super.visit(field, arg);
            }
        }, null);

        return snippets;
    }

    // separate parser for the question in @AiAutoSnippet annotations
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
