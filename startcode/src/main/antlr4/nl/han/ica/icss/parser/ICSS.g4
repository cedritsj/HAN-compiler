grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
selector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;
operator: PLUS | MIN | MUL;
arithmetic_expression: (CAPITAL_IDENT | LOWER_IDENT | PIXELSIZE | PERCENTAGE | SCALAR) ( operator  (CAPITAL_IDENT | LOWER_IDENT | PIXELSIZE | PERCENTAGE | SCALAR) )+;
expression: LOWER_IDENT COLON (PIXELSIZE | PERCENTAGE | COLOR | CAPITAL_IDENT | LOWER_IDENT | arithmetic_expression) SEMICOLON;
variable_declaration: (CAPITAL_IDENT | LOWER_IDENT) ASSIGNMENT_OPERATOR (COLOR | PIXELSIZE | PERCENTAGE | TRUE | FALSE) SEMICOLON;
style_rule: selector OPEN_BRACE expression+ CLOSE_BRACE;

stylesheet: variable_declaration* style_rule+;