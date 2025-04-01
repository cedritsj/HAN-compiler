package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();

        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet root) {

        variableTypes.addFirst(new HashMap<>());

        for (ASTNode child : root.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }

            if (child instanceof Stylerule) {
                variableTypes.addFirst(new HashMap<>());
                checkStylerule(child);
                variableTypes.pop();
            }
        }

        variableTypes.pop();
    }

    private void checkStylerule(ASTNode astNode) {
        for (ASTNode child : astNode.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
                continue;
            }

            // IF clause

            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    private void checkDeclaration(Declaration child) {
        switch (child.property.name) {
            case "width":
            case "height":
                if (!(child.expression instanceof PixelLiteral || child.expression instanceof PercentageLiteral || child.expression instanceof VariableReference || child.expression instanceof Operation)) {
                    child.setError(child.property.name + " must be a pixel or percentage literal OR a variable reference");
                } else if (child.expression instanceof VariableReference) {
                    checkVariableReference(child.expression);
                }
                break;
            case "background-color":
                if (!(child.expression instanceof ColorLiteral || child.expression instanceof VariableReference)) {
                    child.setError("Background color must be a color literal");
                } else if (child.expression instanceof VariableReference) {
                    checkVariableReference(child.expression);
                }
                break;
            case "color":
                if (!(child.expression instanceof ColorLiteral || child.expression instanceof VariableReference)) {
                    child.setError("Color must be a color literal");
                } else if (child.expression instanceof VariableReference) {
                    checkVariableReference(child.expression);
                }
                break;
        }
    }

    private void checkVariableAssignment(VariableAssignment child) {
        VariableReference variableReference = child.name;
        ExpressionType expressionType = checkExpression(child.expression);

        if (expressionType == ExpressionType.UNDEFINED || expressionType == null) {
            child.setError("Variable assignment " + variableReference.name + " has an undefined type");
            return;
        }

        variableTypes.getFirst().put(variableReference.name, expressionType);
    }

    private ExpressionType checkVariableReference(ASTNode child) {
        VariableReference variableReference = (VariableReference) child;
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(variableReference.name)) {
                return scope.get(variableReference.name);
            }
        }
        variableReference.setError("Variable " + variableReference.name + " is not defined");
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkOperation(Operation operation) {
        ExpressionType leftType = checkExpression(operation.lhs);
        ExpressionType rightType = checkExpression(operation.rhs);

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (leftType != rightType) {
                operation.setError("Invalid operation types: " + leftType + " and " + rightType);
                return ExpressionType.UNDEFINED;
            }
            return leftType;
        } else if (operation instanceof MultiplyOperation) {
            if (leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR) {
                operation.setError("Invalid operation types: " + leftType + " and " + rightType);
                return ExpressionType.UNDEFINED;
            }
            return leftType == ExpressionType.SCALAR ? rightType : leftType;
        }
        return leftType;
    }

    public ExpressionType checkExpression(ASTNode child) {
        Expression expression = (Expression) child;

        if (expression instanceof Operation) {
            return checkOperation((Operation) expression);
        }

        return determineExpressionType(expression);
    }

    private ExpressionType determineExpressionType(Expression expression) {
        if (expression instanceof VariableReference) {
            return checkVariableReference(expression);
        } else
        if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else {
            return ExpressionType.UNDEFINED;
        }
    }
}