package mjcompiler.model;

import mjcompiler.helpers.util.EnumToken;

/**
 * @author Bianca de Almeida Dantas
 */
//Classe que especifica uma entrada da tabela de símbolos. Ela contém uma 
//string que representa o lexema reconhecido no processo de análise sintática (ou
//as palavras chaves inseridas no início da compilação.
public class STEntry 
{
    public EnumToken tokName;
    public String lexeme;//Lexema.
    public String tokNameAbst; // Tipo abstrato 
    public boolean reserved;//Indica se é palavra reservada.
    public boolean abstrato; // Indica se possui tipo abstrato ou básico
    
    public STEntry() // Só para instanciar uma nova ST vazia
    {}
    
    public STEntry(EnumToken tipo) // Para tipo array
    {
        tokName = tipo;
        reserved = false;
    }
    public STEntry(String lex) // Para classes 
    {
        lexeme = lex;
        reserved = false;
    }
    public STEntry(String tipo, String name) // Quando o tipo for abstrato
    {
        tokNameAbst = tipo;
        lexeme = name;
        reserved = false;
        abstrato = true;
    }
    
    public STEntry(EnumToken name, String lex)
    {
        tokName = name;
        lexeme = lex;
        reserved = false;
        abstrato = false;
    }
    
    public STEntry(EnumToken name, String lex, boolean res)
    {
        tokName =  name;
        lexeme = lex;
        reserved = res;
    }
}
