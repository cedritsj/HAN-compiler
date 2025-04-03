package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;

public class ConditionalChecker {

    private final Checker checker;

    public ConditionalChecker(Checker checker) {
        this.checker = checker;
    }

    public void checkIfClause(IfClause child) {
        ExpressionType expressionType = checker.getExpressionChecker().checkExpression(child.conditionalExpression);

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
    }

    private void checkElseClause(ElseClause child) {
        checkConditionalBody(child.getChildren());
    }

    public void checkConditionalBody(ArrayList<ASTNode> children) {
        checker.getVariableTypes().addFirst(new HashMap<>());

        for (ASTNode astNode : children) {
            if (astNode instanceof Stylerule) {
                checker.checkStylerule(astNode);
            } else if (astNode instanceof IfClause) {
                checkIfClause((IfClause) astNode);
            } else if (astNode instanceof VariableAssignment) {
                checker.getVariableChecker().checkVariableAssignment((VariableAssignment) astNode);
            } else if (astNode instanceof Declaration) {
                checker.checkDeclaration((Declaration) astNode);
            }
        }

        checker.getVariableTypes().removeFirst();
    }
}