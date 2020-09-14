package mjcompiler.helpers.util;

public enum EnumToken 
{
    UNDEF,
    ARRAY, // Vetor (sempre de inteiros)
    SEP, // separadores
    CLASS, // class
    PUBLIC, // public
    STATIC, // static
    VOID, // void
    MAIN, // main
    ID, 
    IF, // if
    WHILE, // while
    SOPRINTLN, // system.out.println
    THIS, // this
    STRING, // String
    INT, // int
    BOOLEAN, // boolean
    NEW, // new
    EXTENDS, // extends
    LBRACKET, // [ 
    RBRACKET, // ]
    LPARENTHESE, // (
    RPARENTHESE, // )
    LBRACE, // {
    RBRACE, // }
    PERIOD, // .
    COMMA, // ,
    SEMICOLON, // ;
    ELSE, // else
    ATTRIB, // =
    NOT, // !
    ARITHOP, // Operação aritmética
    PLUS, // +
    MINUS, // -
    MULT, // *
    DIV, //  /
    RELOP,
    EQ, // ==
    NE, // !=
    GT, // >
    LT, // <
    NUMBER,
    INTEGER_LITERAL,
    TRUE, // true
    FALSE, // false
    LOGOP, // Operação lógica
    AND, // &&
    RETURN, // return
    LENGTH, // length
    EOF 
}