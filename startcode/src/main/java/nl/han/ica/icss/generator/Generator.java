package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

import java.util.stream.Collectors;

public class Generator {

    private StringBuilder css;

    public Generator() {
        this.css = new StringBuilder();
    }

    public String generate(AST ast) {
        generateCSS(ast.root);
        return css.toString();
    }

    private void generateCSS(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                generateSelector(child);

                generateDeclaration(child);

                this.css.append("}\n");
            }
        }
    }

    private void generateSelector(ASTNode node) {
        Stylerule stylerule = (Stylerule) node;
        String selectors = stylerule.selectors.stream()
                .map(ASTNode::toString)
                .collect(Collectors.joining(", "));
        this.css.append(selectors).append(" {\n");
    }

    private void generateDeclaration(ASTNode node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                Declaration declaration = (Declaration) child;
                this.css.append("  ")
                        .append(declaration.property.name)
                        .append(": ")
                        .append(generateExpression(declaration.expression))
                        .append(";\n");
            }
        }
    }

    private String generateExpression(Expression expression) {
        if (expression instanceof PixelLiteral) {
            PixelLiteral pixelLiteral = (PixelLiteral) expression;
            return pixelLiteral.value + "px";
        } else if (expression instanceof ColorLiteral) {
            ColorLiteral colorLiteral = (ColorLiteral) expression;
            return colorLiteral.value;
        } else if (expression instanceof PercentageLiteral) {
            PercentageLiteral percentageLiteral = (PercentageLiteral) expression;
            return percentageLiteral.value + "%";
        }
        return "";
    }
}
