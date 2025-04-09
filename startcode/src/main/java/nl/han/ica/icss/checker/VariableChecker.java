package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class VariableChecker {

    private final CheckerContext context;

    public VariableChecker(CheckerContext context) {
        this.context = context;
    }

    public void checkVariableAssignment(VariableAssignment child) {
        VariableReference variableReference = child.name;
        ExpressionChecker expressionChecker = new ExpressionChecker(context);
        ExpressionType expressionType = expressionChecker.checkExpression(child.expression);

        if (expressionType == ExpressionType.UNDEFINED || expressionType == null) {
            child.setError("Variable assignment " + variableReference.name + " has an undefined type");
            return;
        }

        context.defineVariable(variableReference.name, expressionType);
    }

    public ExpressionType checkVariableReference(ASTNode child) {
        VariableReference variableReference = (VariableReference) child;
        ExpressionType type = context.lookupVariable(variableReference.name);

        if (type == ExpressionType.UNDEFINED) {
            variableReference.setError("Variable " + variableReference.name + " is not defined");
        }

        return type;
    }
}