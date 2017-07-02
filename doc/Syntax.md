### Concrete syntax

- program := (declaration | statement)*
- block := LBRACE ( declaration | statement )* RBRACE
- declaration := unassignedDeclaration | assignmentDeclaration
- unassignedDeclaration := type IDENT
- assignmentDeclaration := type IDENT ASSIGN expression
- type := KW\_INT | KW\_FLOAT | KW\_BOOL | KW\_STRING | KW\_ARRAY | KW\_FORMAT
- statement := assignmentStatement | ifStatement | whileStatement | frameworkStatement
- assignmentStatement := IDENT ASSIGN expression
- ifStatement := KW\_IF expression block
- whileStatement := KW\_WHILE expression block
- frameworkStatement := printStatement | plotStatement | playStatement
- printStatement := KW\_PRINT LPAREN expression RPAREN
- plotStatement := KW\_PLOT LPAREN expression COMMA expression COMMA expression RPAREN
- playStatement := KW\_PLAY LPAREN expression COMMA expression RPAREN
- expression := term (termOperator term)*
- termOperator := LT | LE | GT | GE | EQUAL | NOTEQUAL
- term := summand (summandOperator summand)*
- summandOperator := PLUS | MINUS | OR
- summand := factor (factorOperator factor)*
- factorOperator := TIMES | DIV | MOD | AND
- factor := LPAREN expression RPAREN | identExpression | literalExpression | frameworkExpression
- identExpression := IDENT
- literalExpression := intLitExpression | floatLitExpression | boolLitExpression | stringLitExpression
- intLitExpression := INT\_LIT
- floatLitExpression := FLOAT\_LIT
- boolLitExpression := KW\_TRUE | KW\_FALSE
- stringLitExpression := STRING\_LIT
- frameworkExpression := infoExpression | readExpression | formatExpression
- infoExpression := KW\_INFO
- readExpression := KW\_READ LPAREN expression COMMA expression RPAREN
- formatExpression := KW\_MONO | KW\_STEREO

### Abstract syntax

- Program := (Declaration | Statement)*
- Block := (Declaration | Statement)*
- Declaration := UnassignedDeclaration | AssignmentDeclaration
- UnassignedDeclaration := Type IDENT
- AssignmentDeclaration := Type IDENT Expression
- Type := INT | FLOAT | BOOL | STRING | ARRAY | FORMAT
- Statement := AssignmentStatement | IfStatement | WhileStatement | FrameworkStatement
- AssignmentStatement := IDENT Expression
- IfStatement := Expression Block
- WhileStatement := Expression Block
- FrameworkStatement := PrintStatement | PlotStatement | PlayStatement
- PrintStatement := Expression
- PlotStatement := Expression\_0 Expression\_1 Expression\_2
- PlayStatement := Expression\_0 Expression\_1
- Expression := BinaryExpression | IdentExpression | LiteralExpression | FrameworkExpression
- BinaryExpression := Expression\_0 (termOperator | summandOperator | factorOperator) Expression\_1
- ReadExpression := Expression\_0 Expression\_1

### TypeChecker rules

- UnassignedDeclaration:
	+ Variable may not be declared more than once in the same scope
- AssignmentDeclaration:
	+ Variable may not be declared more than once in the same scope
	+ Declaration.type == Expression.type
- AssignmentStatement:
	+ Variable must have been declared in some enclosing scope
	+ Declaration.type == Expression.type
- IfStatement:
	+ Expression.type == BOOL
- WhileStatement:
	+ Expression.type == BOOL
- PrintStatement:
	+ Expression.type != ARRAY | FORMAT
- PlotStatement:
	+ Expression\_0.type = STRING
	+ Expression\_1.type = ARRAY
	+ Expression\_2.type = INT
- PlayStatement:
	+ Expression\_0.type = ARRAY
	+ Expression\_1.type = FORMAT
- BinaryExpression:
	+ (INT | FLOAT) (MOD | PLUS | MINUS | TIMES | DIV) (FLOAT | INT) ==> FLOAT
	+ INT (summandOperator | factorOperator) INT ==> INT
	+ (INT | BOOL) (AND | OR) (BOOL | INT) ==> BOOL
	+ !(ARRAY | STRING) (termOperator) !(STRING | ARRAY) ==> BOOL
- IdentExpression:
	+ Variable must have been declared in some enclosing scope
	+ IdentExpression.type = Declaration.type
- IntLitExpression:
	+ IntLitExpression.type = INT
- FloatLitExpression:
	+ FloatLitExpression.type = FLOAT
- BoolLitExpression:
	+ BoolLitExpression.type = BOOL
- StringLitExpression:
	+ StringLitExpression.type = STRING
- InfoExpression:
	+ InfoExpression.type = STRING
- FormatExpression:
	+ FormatExpression.type = FORMAT
- ReadExpression:
	+ ReadExpression.type = ARRAY
	+ Expression\_0.type = STRING
	+ Expression\_1.type = FORMAT