package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Checker {

    private final VariableChecker variableChecker = new VariableChecker(this);
    private final ExpressionChecker expressionChecker = new ExpressionChecker(this);
    private final ConditionalChecker conditionalChecker = new ConditionalChecker(this);
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet root) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode child : root.getChildren()) {
            if (child instanceof VariableAssignment) {
                this.variableChecker.checkVariableAssignment((VariableAssignment) child);
            }

            if (child instanceof Stylerule) {
                variableTypes.addFirst(new HashMap<>());
                checkStylerule(child);
                variableTypes.removeFirst();
            }
        }

        variableTypes.removeFirst();
    }

    void checkStylerule(ASTNode astNode) {
        for (ASTNode child : astNode.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
                continue;
            }

            if (child instanceof IfClause) {
                this.conditionalChecker.checkIfClause((IfClause) child);
                continue;
            }

            if (child instanceof VariableAssignment) {
                this.variableChecker.checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    void checkDeclaration(Declaration child) {
        ExpressionType type = this.expressionChecker.checkExpression(child.expression);

        switch (child.property.name) {
            case "width":
            case "height":
                validateDimensionProperty(child, type);
                break;
            case "background-color":
            case "color":
                validateColorProperty(child, type);
                break;
        }
    }

    private void validateDimensionProperty(Declaration child, ExpressionType type) {
        validateProperty(child, ExpressionType.PIXEL, type, child.property.name + " must be a pixel or percentage literal or a variable reference to a pixel or percentage");
    }

    private void validateColorProperty(Declaration child, ExpressionType type) {
        validateProperty(child, ExpressionType.COLOR, type, child.property.name + " must be a color literal or a variable reference to a color");
    }

    private void validateProperty(Declaration child, ExpressionType validType, ExpressionType type, String errorMessage) {
        if (child.expression instanceof VariableReference) {
            ExpressionType variableType = this.variableChecker.checkVariableReference(child.expression);
            if (variableType == ExpressionType.UNDEFINED) {
                return;
            }
        } else if (child.expression instanceof Operation) {
            expressionChecker.checkOperation((Operation) child.expression);
        }

        if (type != validType) {
            child.setError(errorMessage);
        }
    }

    public LinkedList<HashMap<String, ExpressionType>> getVariableTypes() {
        return variableTypes;
    }

    public VariableChecker getVariableChecker() {
        return variableChecker;
    }

    public ExpressionChecker getExpressionChecker() {
        return expressionChecker;
    }
}