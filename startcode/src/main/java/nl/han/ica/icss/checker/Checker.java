package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
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
        checkStylerule((Stylerule)root.getChildren().get(0));
    }

    private void checkStylerule(ASTNode astNode) {
        for(ASTNode child : astNode.getChildren()) {
            if(child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration child) {
        if (child.property.name.equals("width")) {
            if ((child.expression instanceof ColorLiteral)) {
                child.setError("Width may not be a color literal");
            }
        }
    }
}
