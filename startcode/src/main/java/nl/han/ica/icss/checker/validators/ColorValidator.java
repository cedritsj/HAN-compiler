package nl.han.ica.icss.checker.validators;

import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.checker.Checker;

public class ColorValidator implements IPropertyValidator {

    @Override
    public void validate(Declaration declaration, ExpressionType type, Checker checker) {
        checker.validateProperty(
                declaration,
                new ExpressionType[]{ExpressionType.COLOR},
                type,
                declaration.property.name + " must be a color literal or a variable reference to a color"
        );
    }
}
