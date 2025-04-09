package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.checker.validators.ColorValidator;
import nl.han.ica.icss.checker.validators.DimensionValidator;
import nl.han.ica.icss.checker.validators.IPropertyValidator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Checker {

    private final VariableChecker variableChecker = new VariableChecker(this);
    private final ExpressionChecker expressionChecker = new ExpressionChecker(this);
    private final ConditionalChecker conditionalChecker = new ConditionalChecker(this);
    private LinkedList<HashMap<String, ExpressionType>> variableTypes;
    private final Map<String, IPropertyValidator> propertyValidators = new HashMap<>();


    public Checker() {
        // Initialize property validators
        propertyValidators.put("width", new DimensionValidator());
        propertyValidators.put("height", new DimensionValidator());
        propertyValidators.put("background-color", new ColorValidator());
        propertyValidators.put("color", new ColorValidator());
    }

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

        // Use the property validator if available
        IPropertyValidator validator = propertyValidators.get(child.property.name);
        if (validator != null) {
            validator.validate(child, type, this);
        }
    }

    public void validateProperty(Declaration child, ExpressionType[] validTypes, ExpressionType type, String errorMessage) {
        if (child.expression instanceof VariableReference) {
            ExpressionType variableType = this.variableChecker.checkVariableReference(child.expression);
            if (variableType == ExpressionType.UNDEFINED) {
                return;
            }
        } else if (child.expression instanceof Operation) {
            expressionChecker.checkOperation((Operation) child.expression);
        }

        boolean isValid = false;
        for (ExpressionType validType : validTypes) {
            if (type == validType) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
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