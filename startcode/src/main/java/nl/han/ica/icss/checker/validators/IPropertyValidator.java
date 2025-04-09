package nl.han.ica.icss.checker.validators;

import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.checker.Checker;

public interface IPropertyValidator {
    void validate(Declaration declaration, ExpressionType type, Checker checker);
}
