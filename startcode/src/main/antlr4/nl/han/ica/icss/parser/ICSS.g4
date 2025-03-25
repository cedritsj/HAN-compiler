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
id_selector         : ID_IDENT;
class_selector      : CLASS_IDENT;
tag_selector        : LOWER_IDENT;

add_operator        : PLUS;
sub_operator        : MIN;
mul_operator        : MUL;

color_literal       : COLOR;
percentage_literal  : PERCENTAGE;
pixel_literal       : PIXELSIZE;
scalar_literal      : SCALAR;
capital_literal     : CAPITAL_IDENT;
lower_literal       : LOWER_IDENT;
boolean_literal     : TRUE | FALSE;

literal             : capital_literal | lower_literal | pixel_literal | percentage_literal | scalar_literal;

arithmetic_expression : literal (( add_operator | sub_operator | mul_operator ) literal)+;

property_value      : pixel_literal
                    | percentage_literal
                    | color_literal
                    | capital_literal
                    | lower_literal
                    | arithmetic_expression;

declaration         : tag_selector COLON property_value SEMICOLON;

variable_value      : color_literal
                    | pixel_literal
                    | percentage_literal
                    | boolean_literal;

rulebody            : (declaration|if_statement|variable_declaration)*;
if_statement        : IF BOX_BRACKET_OPEN (id_selector | LOWER_IDENT | CLASS_IDENT | CAPITAL_IDENT)  BOX_BRACKET_CLOSE OPEN_BRACE rulebody CLOSE_BRACE else_statement?;
else_statement      : ELSE OPEN_BRACE rulebody CLOSE_BRACE;

variable_declaration : (capital_literal | lower_literal) ASSIGNMENT_OPERATOR variable_value+ SEMICOLON;

selector            : id_selector | class_selector | tag_selector;
style_rule          : selector OPEN_BRACE rulebody CLOSE_BRACE;

stylesheet          : variable_declaration* style_rule*;

