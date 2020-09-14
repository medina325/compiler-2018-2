package mjcompiler.helpers.util;

public class CompilerException extends RuntimeException // filho da classe RuntimeExecption
{
    private String msg;

    public CompilerException() 
    {       
        msg = "Unexpected";
    }
    
    public CompilerException(String str)
    {
        super(str);
        msg = str;
    }
    
    public String toString()
    {
        return msg;
    }
}