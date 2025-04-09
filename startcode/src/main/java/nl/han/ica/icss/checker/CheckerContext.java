package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class CheckerContext {
    private final LinkedList<HashMap<String, ExpressionType>> variableTypes = new LinkedList<>();

    public void addVariableScope() {
        variableTypes.addFirst(new HashMap<>());
    }

    public void removeVariableScope() {
        variableTypes.removeFirst();
    }

    public void defineVariable(String name, ExpressionType type) {
        if (!variableTypes.isEmpty()) {
            variableTypes.getFirst().put(name, type);
        }
    }

    public ExpressionType lookupVariable(String name) {
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return ExpressionType.UNDEFINED;
    }
}