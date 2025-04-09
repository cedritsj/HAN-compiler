package nl.han.ica.icss.checker.validators;

import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.checker.Checker;

public class DimensionValidator implements IPropertyValidator {
    @Override
    public void validate(Declaration declaration, ExpressionType type, Checker checker) {
        checker.validateProperty(
                declaration,
                new ExpressionType[]{ExpressionType.PIXEL, ExpressionType.PERCENTAGE},
                type,
                declaration.property.name + " must be a pixel or percentage literal or a variable reference to a pixel or percentage"
        );
    }
}
