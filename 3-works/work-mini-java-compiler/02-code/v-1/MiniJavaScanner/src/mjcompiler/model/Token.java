package mjcompiler.model;

import mjcompiler.helpers.util.EnumToken;

/**
 * @author bianca
 */
public class Token 
{
    public EnumToken name;
    public EnumToken attribute;
    public String value;
    public int lineNumber;
    //public STEntry tsPtr;
    
    public Token(EnumToken name)
    {
        this.name = name;
        attribute = EnumToken.UNDEF;
        lineNumber = -1;
        //tsPtr = null;
    }
    
    public Token(EnumToken name, EnumToken attr)
    {
        this.name = name;
        attribute = attr;
        //tsPtr = null;
    }
    
    // Construtor feito especificamente para a declaração de uma variável de tipo abstrato
    public Token(EnumToken name, String lexeme) 
    {
        this.name = name; // Vai ser sempre 'ID'
        value = lexeme; // Nome de alguma classe (tipo abstrato)
        lineNumber = -2;
    }
}
