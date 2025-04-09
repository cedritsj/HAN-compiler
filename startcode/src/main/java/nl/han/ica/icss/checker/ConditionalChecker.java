package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;

public class ConditionalChecker {

    private final CheckerContext context;

    public ConditionalChecker(CheckerContext context) {
        this.context = context;
    }

    public void checkIfClause(IfClause child) {
        ExpressionChecker expressionChecker = new ExpressionChecker(context);
        ExpressionType expressionType = expressionChecker.checkExpression(child.conditionalExpression);

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
        context.addVariableScope();

        for (ASTNode astNode : children) {
            if (astNode instanceof Stylerule) {
                new Checker(context).checkStylerule((Stylerule) astNode);
            } else if (astNode instanceof IfClause) {
                checkIfClause((IfClause) astNode);
            } else if (astNode instanceof VariableAssignment) {
                new VariableChecker(context).checkVariableAssignment((VariableAssignment) astNode);
            } else if (astNode instanceof Declaration) {
                new Checker(context).checkDeclaration((Declaration) astNode);
            }
        }

        context.removeVariableScope();
    }
}