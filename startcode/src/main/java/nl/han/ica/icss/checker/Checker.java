package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
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

            if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
                continue;
            }

            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            }
        }
    }

    private void checkIfClause(IfClause child) {
        ExpressionType expressionType = checkExpression(child.conditionalExpression);

        if (expressionType == ExpressionType.UNDEFINED || expressionType == null) {
            child.setError("If clause condition has an undefined type");
            return;
        }

        if (expressionType != ExpressionType.BOOL) {
            child.setError("If clause condition must be a boolean literal");
            return;
        }

        checkConditionalBody(child.getChildren());

        if (child.elseClause != null) {
            checkElseClause(child.elseClause);
        }

        variableTypes.pop();
    }

    private void checkElseClause(ElseClause child) {
        checkConditionalBody(child.getChildren());

        variableTypes.pop();
    }

    private void checkConditionalBody(ArrayList<ASTNode> children) {
        variableTypes.addFirst(new HashMap<>());

        for (ASTNode astNode : children) {
            if (astNode instanceof Stylerule) {
                checkStylerule(astNode);
            } else if (astNode instanceof IfClause) {
                checkIfClause((IfClause) astNode);
            } else if (astNode instanceof VariableReference) {
                checkVariableReference(astNode);
            } else if (astNode instanceof Declaration) {
                checkDeclaration((Declaration) astNode);
            }
        }
    }

    private void checkDeclaration(Declaration child) {
        ExpressionType type = checkExpression(child.expression);

        switch (child.property.name) {
            case "width":
            case "height":
                if (type != ExpressionType.PIXEL && type != ExpressionType.PERCENTAGE) {
                    child.setError(child.property.name + " must be a pixel or percentage literal or a variable reference to a pixel or percentage");
                } else if (child.expression instanceof VariableReference) {
                    checkVariableReference(child.expression);
                } else if (child.expression instanceof Operation) {
                    checkOperation((Operation) child.expression);
                }
                break;
            case "background-color":
            case "color":
                if (type != ExpressionType.COLOR) {
                    child.setError(child.property.name + " must be a color literal or a variable reference to a color");
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

        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            operation.setError("Color literals cannot be used in operations");
            return ExpressionType.UNDEFINED;
        }

        if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            if (leftType != rightType) {
                operation.setError("Invalid operation types: " + leftType + " and " + rightType);
                return ExpressionType.UNDEFINED;
            }
            return leftType;
        }

        if (operation instanceof MultiplyOperation) {
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
        } else if (expression instanceof ColorLiteral) {
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