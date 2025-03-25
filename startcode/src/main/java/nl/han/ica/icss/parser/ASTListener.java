package nl.han.ica.icss.parser;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.parser.ICSSParser;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends nl.han.ica.icss.parser.ICSSBaseListener {
	
	//Accumulator attributes:
	private final AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private final IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack();
	}
    public AST getAST() {
        return ast;
    }

	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		Stylesheet stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.root = (Stylesheet) currentContainer.pop();
	}

	public void enterStyle_rule(ICSSParser.Style_ruleContext ctx) {
		Stylerule stylerule = new Stylerule();
		currentContainer.push(stylerule);
	}

	public void exitStyle_rule(ICSSParser.Style_ruleContext ctx) {
		Stylerule stylerule = (Stylerule) currentContainer.pop();
		currentContainer.peek().addChild(stylerule);
	}

	public void enterId_selector(ICSSParser.Id_selectorContext ctx) {
		IdSelector idSelector = new IdSelector(ctx.getText());
		currentContainer.push(idSelector);
	}

	public void exitId_selector(ICSSParser.Id_selectorContext ctx) {
		IdSelector idSelector = (IdSelector) currentContainer.pop();
		currentContainer.peek().addChild(idSelector);
	}

	public void enterClass_selector(ICSSParser.Class_selectorContext ctx) {
		ClassSelector classSelector = new ClassSelector(ctx.getText());
		currentContainer.push(classSelector);
	}

	public void exitClass_selector(ICSSParser.Class_selectorContext ctx) {
		ClassSelector classSelector = (ClassSelector) currentContainer.pop();
		currentContainer.peek().addChild(classSelector);
	}

	public void enterTag_selector(ICSSParser.Tag_selectorContext ctx) {
		TagSelector tagSelector = new TagSelector((ctx.getText()));
		currentContainer.push(tagSelector);
	}

	public void exitTag_selector(ICSSParser.Tag_selectorContext ctx) {
		TagSelector tagSelector = (TagSelector) currentContainer.pop();
		currentContainer.peek().addChild(tagSelector);
	}

	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = new Declaration();
		currentContainer.push(declaration);
	}

	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		Declaration declaration = (Declaration) currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	public void enterAdd_operator(ICSSParser.Add_operatorContext ctx) {
		AddOperation operator = new AddOperation();
		currentContainer.push(operator);
	}

	public void exitAdd_operator(ICSSParser.Add_operatorContext ctx) {
		AddOperation operator = (AddOperation) currentContainer.pop();
		currentContainer.peek().addChild(operator);
	}

	public void enterSub_operator(ICSSParser.Sub_operatorContext ctx) {
		SubtractOperation operator = new SubtractOperation();
		currentContainer.push(operator);
	}

	public void exitSub_operator(ICSSParser.Sub_operatorContext ctx) {
		SubtractOperation operator = (SubtractOperation) currentContainer.pop();
		currentContainer.peek().addChild(operator);
	}

	public void enterMul_operator(ICSSParser.Mul_operatorContext ctx) {
		MultiplyOperation operator = new MultiplyOperation();
		currentContainer.push(operator);
	}

	public void exitMul_operator(ICSSParser.Mul_operatorContext ctx) {
		MultiplyOperation operator = (MultiplyOperation) currentContainer.pop();
		currentContainer.peek().addChild(operator);
	}

	public void enterVariable_declaration(ICSSParser.Variable_declarationContext ctx) {
		VariableAssignment variableAssignment = new VariableAssignment();
		currentContainer.push(variableAssignment);
	}

	public void exitVariable_declaration(ICSSParser.Variable_declarationContext ctx) {
		VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
		currentContainer.peek().addChild(variableAssignment);
	}

	public void enterVariable_value(ICSSParser.Variable_valueContext ctx) {
		VariableReference variableReference = new VariableReference(ctx.getText());
		currentContainer.push(variableReference);
	}

	public void exitVariable_value(ICSSParser.Variable_valueContext ctx) {
		VariableReference variableReference = (VariableReference) currentContainer.pop();
		currentContainer.peek().addChild(variableReference);
	}

	public void enterColor_literal(ICSSParser.Color_literalContext ctx) {
		ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
		currentContainer.push(colorLiteral);
	}

	public void exitColor_literal(ICSSParser.Color_literalContext ctx) {
		ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
		currentContainer.peek().addChild(colorLiteral);
	}

	public void enterPercentage_literal(ICSSParser.Percentage_literalContext ctx) {
		PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
		currentContainer.push(percentageLiteral);
	}

	public void exitPercentage_literal(ICSSParser.Percentage_literalContext ctx) {
		PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
		currentContainer.peek().addChild(percentageLiteral);
	}

	public void enterPixel_literal(ICSSParser.Pixel_literalContext ctx) {
		PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
		currentContainer.push(pixelLiteral);
	}

	public void exitPixel_literal(ICSSParser.Pixel_literalContext ctx) {
		PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
		currentContainer.peek().addChild(pixelLiteral);
	}

	public void enterBoolean_literal(ICSSParser.Boolean_literalContext ctx) {
		BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
		currentContainer.push(boolLiteral);
	}

	public void exitBoolean_literal(ICSSParser.Boolean_literalContext ctx) {
		BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
		currentContainer.peek().addChild(boolLiteral);
	}

	public void enterScalar_literal(ICSSParser.Scalar_literalContext ctx) {
		ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
		currentContainer.push(scalarLiteral);
	}

	public void exitScalar_literal(ICSSParser.Scalar_literalContext ctx) {
		ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
		currentContainer.peek().addChild(scalarLiteral);
	}

	public void enterArithmetic_expression(ICSSParser.Arithmetic_expressionContext ctx) {

	}

	public void exitArithmetic_expression(ICSSParser.Arithmetic_expressionContext ctx) {

	}
}