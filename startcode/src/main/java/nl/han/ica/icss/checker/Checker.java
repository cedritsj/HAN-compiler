package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.Arrays;
import java.util.Map;

public class Checker {
    private final CheckerContext context;
    private final VariableChecker variableChecker;
    private final ExpressionChecker expressionChecker;
    private final ConditionalChecker conditionalChecker;

    private static final Map<String, ExpressionType[]> PROPERTY_RULES = Map.of(
            "background-color", new ExpressionType[]{ExpressionType.COLOR},
            "color", new ExpressionType[]{ExpressionType.COLOR},
            "width", new ExpressionType[]{ExpressionType.PIXEL, ExpressionType.PERCENTAGE},
            "height", new ExpressionType[]{ExpressionType.PIXEL, ExpressionType.PERCENTAGE}
    );

    public Checker(CheckerContext context) {
        this.context = context != null ? context : new CheckerContext();
        this.variableChecker = new VariableChecker(this.context);
        this.expressionChecker = new ExpressionChecker(this.context);
        this.conditionalChecker = new ConditionalChecker(this.context);
    }

    public void check(AST ast) {
        context.addVariableScope();
        checkStylesheet(ast.root);
        context.removeVariableScope();
    }

    public void checkStylesheet(Stylesheet root) {
        for (ASTNode child : root.getChildren()) {
            if (child instanceof VariableAssignment) {
                variableChecker.checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                context.addVariableScope();
                checkStylerule((Stylerule) child);
                context.removeVariableScope();
            }
        }
    }

    public void checkStylerule(Stylerule stylerule) {
        for (ASTNode child : stylerule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                conditionalChecker.checkIfClause((IfClause) child);
            } else if (child instanceof VariableAssignment) {
                variableChecker.checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    public void checkDeclaration(Declaration declaration) {
        ExpressionType actualType = expressionChecker.checkExpression(declaration.expression);

        ExpressionType[] expectedTypes = PROPERTY_RULES.get(declaration.property.name);
        if (expectedTypes != null && !isValidType(actualType, expectedTypes)) {
            declaration.setError("Invalid type for property '" + declaration.property.name + "'. Expected: " +
                    String.join(", ", Arrays.stream(expectedTypes).map(Enum::name).toArray(String[]::new)) +
                    ", but found: " + actualType);
        }
    }

    private boolean isValidType(ExpressionType actualType, ExpressionType[] expectedTypes) {
        for (ExpressionType expectedType : expectedTypes) {
            if (expectedType == actualType) {
                return true;
            }
        }
        return false;
    }
}