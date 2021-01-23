grammar Little;
KEYWORD: 'PROGRAM'|'BEGIN'|'END'|'FUNCTION'|'READ'|'WRITE'|'IF'|'ELSE'|'ENDIF'|'WHILE'|'ENDWHILE'|'CONTINUE'|'BREAK'|
'RETURN'|'INT'|'VOID'|'STRING'|'FLOAT';
OPEATORS: ':=' | '+' | '-' | '*' | '/' | '=' | '!=' | '<' | '>' | '(' | ')' | ';' | ',' | '<=' | '>='; 
COMMENT : '--'[ -~]*[\r\n] -> skip;
WS: [ \t\r\n]+ -> skip;
IDENTIFIER: [a-z][a-z0-9]*;
INTLITERAL: [0-9]+;
FLOATLITERAL: [0-9]*'.'[0-9]+;
STRINGLITERAL: '"'[ -~]*'"';
