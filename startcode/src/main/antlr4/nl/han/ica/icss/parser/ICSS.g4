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
id_selector         : ID_IDENT | COLOR;
class_selector      : CLASS_IDENT;
property_name       : LOWER_IDENT;

color_literal       : COLOR;
percentage_literal  : PERCENTAGE;
pixel_literal       : PIXELSIZE;
scalar_literal      : SCALAR;
lower_literal       : LOWER_IDENT;
boolean_literal     : TRUE | FALSE;

literal: boolean_literal | color_literal | percentage_literal | pixel_literal | scalar_literal | variable_identifier;

expression: literal | expression (MUL) expression | expression (PLUS | MIN) expression;

property_value      : (variable_identifier
                    | expression
                    | pixel_literal
                    | percentage_literal
                    | color_literal
                    | lower_literal
                    | boolean_literal);

declaration         : property_name COLON property_value SEMICOLON;

variable_value      : variable_identifier
                    | expression
                    | color_literal
                    | pixel_literal
                    | percentage_literal
                    | boolean_literal;

rulebody            : (declaration|if_statement|variable_declaration)*;
if_statement        : IF BOX_BRACKET_OPEN (variable_identifier | boolean_literal)  BOX_BRACKET_CLOSE OPEN_BRACE rulebody CLOSE_BRACE else_statement?;
else_statement      : ELSE OPEN_BRACE rulebody CLOSE_BRACE;

variable_identifier  :  CAPITAL_IDENT;
variable_declaration :  variable_identifier ASSIGNMENT_OPERATOR variable_value+ SEMICOLON;

selector            : id_selector #idSelector | class_selector #classSelector | property_name #tagSelector;
style_rule          : selector OPEN_BRACE rulebody CLOSE_BRACE;

stylesheet          : variable_declaration* style_rule*;

