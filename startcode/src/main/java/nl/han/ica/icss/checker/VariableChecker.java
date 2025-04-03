package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class VariableChecker {

    private final Checker checker;

    public VariableChecker(Checker checker) {
        this.checker = checker;
    }

    public void checkVariableAssignment(VariableAssignment child) {
        VariableReference variableReference = child.name;
        ExpressionType expressionType = checker.getExpressionChecker().checkExpression(child.expression);

        if (expressionType == ExpressionType.UNDEFINED || expressionType == null) {
            child.setError("Variable assignment " + variableReference.name + " has an undefined type");
            return;
        }

        checker.getVariableTypes().getFirst().put(variableReference.name, expressionType);
    }

    public ExpressionType checkVariableReference(ASTNode child) {
        VariableReference variableReference = (VariableReference) child;
        for (HashMap<String, ExpressionType> scope : checker.getVariableTypes()) {
            if (scope.containsKey(variableReference.name)) {
                return scope.get(variableReference.name);
            }
        }
        variableReference.setError("Variable " + variableReference.name + " is not defined");
        return ExpressionType.UNDEFINED;
    }
}