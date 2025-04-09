package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {
    private final CheckerContext context;
    private final VariableChecker variableChecker;
    private final ExpressionChecker expressionChecker;
    private final ConditionalChecker conditionalChecker;

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
        expressionChecker.checkExpression(declaration.expression);
    }

    public void validateProperty(Declaration declaration, ExpressionType[] expectedTypes, ExpressionType actualType, String errorMessage) {
        boolean isValid = false;
        for (ExpressionType expectedType : expectedTypes) {
            if (expectedType == actualType) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            declaration.setError(errorMessage);
        }
    }
}