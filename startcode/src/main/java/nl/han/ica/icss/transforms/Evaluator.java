package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.*;

public class Evaluator implements Transform {

    private final LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        Stylesheet stylesheet = ast.root;
        applyStylesheet(stylesheet);
    }

    private void applyStylesheet(ASTNode astNode) {
        List<ASTNode> toRemove = new ArrayList<>();
        variableValues.addFirst(new HashMap<>());

        for (ASTNode child : astNode.getChildren()) {
            if (child instanceof VariableAssignment) {
                applyVariableAssignment((VariableAssignment) child);
                toRemove.add(child);
                continue;
            }

            if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
        variableValues.removeFirst();

        toRemove.forEach(astNode::removeChild);
    }

    private void applyStylerule(Stylerule stylerule) {
        ArrayList<ASTNode> toAdd = new ArrayList<>();

        variableValues.addFirst(new HashMap<>());

        for (ASTNode child : stylerule.body) {
            applyRuleBody(child, toAdd);
        }
        variableValues.removeFirst();

        stylerule.body = toAdd;
    }

    private void applyRuleBody(ASTNode astNode, ArrayList<ASTNode> parentBody) {
        if (astNode instanceof VariableAssignment) {
            applyVariableAssignment((VariableAssignment) astNode);
        } else if (astNode instanceof Declaration) {
            applyDeclaration((Declaration) astNode);
            parentBody.add(astNode);
        } else if (astNode instanceof IfClause) {
            applyIfClause((IfClause) astNode, parentBody);
        }
    }

    private void applyIfClause(IfClause ifClause, ArrayList<ASTNode> parentBody) {
        Expression transformedCondition = applyExpression(ifClause.conditionalExpression);
        ifClause.conditionalExpression = transformedCondition;

        if (transformedCondition instanceof BoolLiteral) {
            BoolLiteral boolLiteral = (BoolLiteral) transformedCondition;
            applyIfClauseBody(ifClause, boolLiteral);
            applyIfClauseBody(ifClause, parentBody);
        }
    }

    private void applyIfClauseBody(IfClause ifClause, BoolLiteral boolLiteral) {
        if (boolLiteral.value) {
            if (ifClause.elseClause != null) {
                ifClause.elseClause.body = new ArrayList<>();
            }
        } else {
            if (ifClause.elseClause == null) {
                ifClause.body = new ArrayList<>();
            } else {
                ifClause.body = ifClause.elseClause.body;
                ifClause.elseClause.body = new ArrayList<>();
            }
        }
    }

    private void applyIfClauseBody(IfClause ifClause, ArrayList<ASTNode> parentBody) {
        for (ASTNode child : ifClause.getChildren()) {
            applyRuleBody(child, parentBody);
        }
    }

    private void applyDeclaration(Declaration declaration) {
        declaration.expression = applyExpression(declaration.expression);
    }

    private void applyVariableAssignment(VariableAssignment variableAssignment) {
        Expression expression = variableAssignment.expression;
        Literal evaluatedValue = applyExpression(expression);

        variableValues.getFirst().put(variableAssignment.name.name, evaluatedValue);
    }

    private Literal applyExpression(Expression expression) {
        if (expression instanceof VariableReference) {
            return getVariableValue(((VariableReference) expression).name);
        }

        if (expression instanceof Operation) {
            return applyOperation((Operation) expression);
        }

        if (expression instanceof Literal) {
            return (Literal) expression;
        }

        return null;
    }

    private Literal applyOperation(Operation operation) {
        Literal left = evaluateOperand(operation.lhs);
        Literal right = evaluateOperand(operation.rhs);

        int leftValue = getLiteralValue(left);
        int rightValue = getLiteralValue(right);

        if (operation instanceof AddOperation) {
            return createLiteral(left, leftValue + rightValue);
        } else if (operation instanceof SubtractOperation) {
            return createLiteral(left, leftValue - rightValue);
        } else if (operation instanceof MultiplyOperation) {
            return createLiteral(right instanceof ScalarLiteral ? left : right, leftValue * rightValue);
        }
        return null;
    }

    private Literal evaluateOperand(Expression operand) {
        if (operand instanceof Operation) {
            return applyOperation((Operation) operand);
        } else if (operand instanceof VariableReference) {
            return getVariableValue(((VariableReference) operand).name);
        } else {
            return (Literal) operand;
        }
    }

    private int getLiteralValue(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if (literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        } else {
            return ((PercentageLiteral) literal).value;
        }
    }

    private Literal createLiteral(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        } else {
            return new PercentageLiteral(value);
        }
    }

    private Literal getVariableValue(String varName) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(varName)) {
                return scope.get(varName);
            }
        }
        return null;
    }
}
