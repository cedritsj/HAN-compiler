package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class ExpressionChecker {

    private final Checker checker;

    public ExpressionChecker(Checker checker) {
        this.checker = checker;
    }

    public ExpressionType checkExpression(ASTNode child) {
        if (child instanceof Operation) {
            return checkOperation((Operation) child);
        }
        return determineExpressionType((Expression) child);
    }

    public ExpressionType checkOperation(Operation operation) {
        ExpressionType leftType = checkExpression(operation.lhs);
        ExpressionType rightType = checkExpression(operation.rhs);

        if (leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR) {
            operation.setError("Color literals cannot be used in operations");
            return ExpressionType.UNDEFINED;
        }

        if (leftType == ExpressionType.BOOL || rightType == ExpressionType.BOOL) {
            operation.setError("Boolean literals cannot be used in operations");
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

    private ExpressionType determineExpressionType(Expression expression) {
        switch (expression.getClass().getSimpleName()) {
            case "VariableReference":
                return checker.getVariableChecker().checkVariableReference(expression);
            case "ColorLiteral":
                return ExpressionType.COLOR;
            case "PixelLiteral":
                return ExpressionType.PIXEL;
            case "PercentageLiteral":
                return ExpressionType.PERCENTAGE;
            case "ScalarLiteral":
                return ExpressionType.SCALAR;
            case "BoolLiteral":
                return ExpressionType.BOOL;
            default:
                return ExpressionType.UNDEFINED;
        }
    }
}