# The Scandal Syntax

## Concrete syntax

- program := (declaration | statement)\*
- block := LBRACE ( declaration | statement )\* RBRACE
- declaration := unassignedDeclaration | assignmentDeclaration
- paramDeclaration := type IDENT
- assignmentDeclaration := type IDENT ASSIGN expression
- type := KW\_INT | KW\_FLOAT | KW\_BOOL | KW\_STRING | KW\_ARRAY
- statement := assignmentStatement | ifStatement | whileStatement | frameworkStatement
- assignmentStatement := IDENT ASSIGN expression
- ifStatement := KW\_IF expression block
- whileStatement := KW\_WHILE expression block
- frameworkStatement := printStatement | plotStatement | playStatement | writeStatement
- printStatement := KW\_PRINT LPAREN expression RPAREN
- plotStatement := KW\_PLOT LPAREN expression COMMA expression COMMA expression RPAREN
- playStatement := KW\_PLAY LPAREN expression COMMA expression RPAREN
- writeStatement := KW\_WRITE LPAREN expression COMMA expression COMMA expression RPAREN
- expression := term (termOperator term)\*
- termOperator := LT | LE | GT | GE | EQUAL | NOTEQUAL
- term := summand (summandOperator summand)\*
- summandOperator := PLUS | MINUS | OR
- summand := factor (factorOperator factor)\*
- factorOperator := TIMES | DIV | MOD | AND
- factor := LPAREN expression RPAREN | identExpression | literalExpression | unaryExpression | frameworkExpression
- identExpression := IDENT
- literalExpression := intLitExpression | floatLitExpression | boolLitExpression | stringLitExpression | arrayLitExpression | funcLitExpression
- funcLitExpression := unassignedDeclaration ARROW expression
- funcApplicationExpression := identExpression LPAREN expression RPAREN
- funcCompositionExpression := identExpression DOT (identExpression DOT)\* funcApplicationExpression
- intLitExpression := INT\_LIT
- floatLitExpression := FLOAT\_LIT
- boolLitExpression := KW\_TRUE | KW\_FALSE
- stringLitExpression := STRING\_LIT
- arrayLitExpression := LBRACKET ((FLOAT\_LIT | INT\_LIT) (COMMA (FLOAT\_LIT | INT\_LIT))\*)\* RBRACKET
- unaryExpression := (KW\_MINUS | KW\_NOT) expression
- frameworkExpression := infoExpression | readExpression | recordExpression | trackExpression | mixExpression | panExpression
- infoExpression := KW\_INFO
- readExpression := KW\_READ LPAREN expression COMMA expression RPAREN
- recordExpression := KW\_RECORD PAREN expression RPAREN
- panExpression := KW\_PAN PAREN expression COMMA expression RPAREN
- trackExpression := KW\_TRACK PAREN expression COMMA expression COMMA expression COMMA expression RPAREN
- mixExpression := KW\_MIX PAREN expression (COMMA expression)\* RPAREN

## Abstract syntax

- Program := (Declaration | Statement)\*
- Block := (Declaration | Statement)\*
- Declaration := UnassignedDeclaration | AssignmentDeclaration
- UnassignedDeclaration := Type IDENT
- AssignmentDeclaration := Type IDENT Expression
- Type := INT | FLOAT | BOOL | STRING | ARRAY | FORMAT | FUNC
- Statement := AssignmentStatement | IfStatement | WhileStatement | FrameworkStatement
- AssignmentStatement := IDENT Expression
- IfStatement := Expression Block
- WhileStatement := Expression Block
- FrameworkStatement := PrintStatement | PlotStatement | PlayStatement | WriteStatement
- PrintStatement := Expression
- PlotStatement := Expression\_0 Expression\_1 Expression\_2
- PlayStatement := Expression\_0 Expression\_1
- WriteStatement := Expression\_0 Expression\_1 Expression\_2
- Expression := BinaryExpression | IdentExpression | LiteralExpression | UnaryExpression | FrameworkExpression
- FuncLitExpression := UnassignedDeclaration Expression
- FuncApplicationExpression := IdentExpression Expression
- FuncCompositionExpression := IdentExpression+ FuncApplicationExpression
- UnaryExpression := Expression
- BinaryExpression := Expression\_0 (termOperator | summandOperator | factorOperator) Expression\_1
- ReadExpression := Expression\_0 Expression\_1
- PanExpression := Expression\_0 Expression\_1
- RecordExpression := Expression
- TrackExpression := Expression\_0 Expression\_1 Expression\_2 Expression\_3
- MixExpression := Expression+

## TypeChecker rules

- UnassignedDeclaration:
	+ Variable may not be declared more than once in the same scope
- AssignmentDeclaration:
	+ Variable may not be declared more than once in the same scope
	+ Type = Expression.type
- AssignmentStatement:
	+ Variable must have been declared in some enclosing scope
	+ Declaration.Type = Expression.type
- IfStatement:
	+ Expression.type = BOOL
- WhileStatement:
	+ Expression.type = BOOL
- PrintStatement:
	+ Expression.type != ARRAY | FORMAT | FILTER | WAVEFORM
- PlotStatement:
	+ Expression\_0.type = STRING
	+ Expression\_1.type = ARRAY
	+ Expression\_2.type = INT | FLOAT
- PlayStatement:
	+ Expression\_0.type = ARRAY
	+ Expression\_1.type = FORMAT
- WriteStatement:
	+ Expression\_0.type = ARRAY
	+ Expression\_1.type = STRING
	+ Expression\_2.type = FORMAT
- UnaryExpression:
	+ Type = Expression.type
	+ Expression.type = INT | FLOAT | BOOL
- BinaryExpression:
	+ (INT | FLOAT) (MOD | PLUS | MINUS | TIMES | DIV) (FLOAT | INT) ==> FLOAT
	+ INT (summandOperator | factorOperator) INT ==> INT
	+ (INT | BOOL) (AND | OR) (BOOL | INT) ==> BOOL
	+ !(ARRAY | STRING) termOperator !(STRING | ARRAY) ==> BOOL
- IdentExpression:
	+ Variable must have been declared in some enclosing scope
	+ Type = Declaration.type
- IntLitExpression:
	+ Type = INT
- FloatLitExpression:
	+ Type = FLOAT
- BoolLitExpression:
	+ Type = BOOL
- StringLitExpression:
	+ Type = STRING
- ArrayLitExpression:
	+ Type = ARRAY
- InfoExpression:
	+ Type = STRING
- FormatExpression:
	+ Type = FORMAT
- WaveformExpression:
	+ Type = WAVEFORM
- FilterExpression:
	+ Type = FILTER
- ReadExpression:
	+ Type = ARRAY
	+ Expression\_0.type = STRING
	+ Expression\_1.type = FORMAT
- PanExpression:
	+ Type = ARRAY
	+ Expression\_0.type = ARRAY
	+ Expression\_1.type = INT | FLOAT | ARRAY
- RecordExpression:
	+ Type = ARRAY
	+ Expression.type = INT | FLOAT
- TrackExpression:
	+ Type = ARRAY
	+ Expression\_0.type = ARRAY
	+ Expression\_1.type = INT | FLOAT
	+ Expression\_2.type = INT | FLOAT | ARRAY
	+ Expression\_3.type = INT | FLOAT | ARRAY
- MixExpression:
	+ Type = ARRAY
	+ Expression.type = ARRAY
- FuncLitExpression:
	+ type = FUNC
	+ inputType = UnassignedDeclaration.type
	+ returnType = Expression.type
- FuncApplicationExpression:
	+ type = IdentExpression.returnType
	+ Expression.type = IdentExpression.inputType
- FuncCompositionExpression:
	+ Every lambda must have been declared in some enclosing scope
	+ The return type of each lambda must match the input type of the next 
	+ inputType = IdentExpression.inputType
	+ type = FuncApplicationExpression.type
