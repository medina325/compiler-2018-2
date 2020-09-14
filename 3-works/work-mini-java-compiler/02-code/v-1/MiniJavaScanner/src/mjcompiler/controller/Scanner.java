package mjcompiler.controller;

import mjcompiler.model.Token;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.StringCharacterIterator;
import mjcompiler.helpers.util.EnumToken;
import mjcompiler.model.STEntry;
import mjcompiler.model.SymbolTable;

public class Scanner 
{
    private static String input;
    private StringCharacterIterator inputIt;
    private int lineNumber;
    private SymbolTable st;
    
   
    //Construtor da classe que recebe como argumento o nome do arquivo texto
    //com o código do programa a ser analisado. O arquivo é aberto e seu 
    //conteúdo é copiado para o buffer de entrada (input)
    public Scanner(SymbolTable globalST, String inputFileName)
    {
        st = globalST;
        
        try
        {
            // Correção do problema de EOF
            // inputFileName += '\n';
            if (inputFileName.isEmpty()){
                inputFileName += '\n';
            }
            if (!inputFileName.substring(inputFileName.length() - 1).equals(" ")){
                inputFileName += '\n';
            }
            //Instancia um objeto de String com vetor de caracteres preenchido 
            input = inputFileName; // Instancia um objeto de String com vetor de caracteres preenchido
            //System.out.printf("%d %s\n", size, input);
            inputIt = new StringCharacterIterator(input); // Instancia e inicializa o iterador de caracteres
                                                          // para percorrer a string input a partir de seu início
         
            lineNumber = 1; // Inicializa contagem de linhas com 1
        }
        catch(Exception e)
        {
            System.err.println("Erro na leitura do arquivo");
        }
    }
    
    //Método para reconhecer e retornar o próximo token
    public Token nextToken()
    {
        Token tok = new Token(EnumToken.UNDEF); // Instancia objeto de Token inicialmente indefinido         
        String lexeme = "";
        
        // Primeiramente ignoro espaços em branco, comentários e caracteres \n, \t, \r e \f             
        while(Character.isWhitespace(inputIt.current()) || inputIt.current() == '/' || inputIt.current() == '\\')
        {
            if(inputIt.current() == '\\') // se for '\'
            {
                inputIt.next();
                if(inputIt.current() == 'n' || inputIt.current() == 't' || inputIt.current() == 'r' ||
                   inputIt.current() == 'f')
                {
                    inputIt.next();
                }
            }
            else if(inputIt.current() == '/') //Ignorando comentários
            {
                inputIt.next();
                // Comentário do tipo //
                if(inputIt.current() == '/') // Significa que achou //
                {
                    inputIt.next();
                    while(inputIt.current() != '\n') // Ignora até pular linha
                        inputIt.next();
                    
                }
                
                // Comentário do tipo /* */
                else if(inputIt.current() == '*') // Significa que achou um /*
                {
                    boolean t = true;
                    inputIt.next();
                    while(t){ // Enquanto não aparecer "*/"
                        if(inputIt.current() == '*'){
                            inputIt.next();
                            if(inputIt.current() == '/'){ // encerra comentário
                                t = false;
                            }
                        }
                        else
                        {
                            inputIt.next(); // Dentro do comentário só ignoro
                        }   
                    }
                    inputIt.next(); // Pŕoximo símbolo
                }
                else // Div
                {
                    tok.name = EnumToken.DIV;
                    tok.attribute = EnumToken.ARITHOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next(); // Próximo símbolo
                    return tok;
                }
                
                
            }
            if(inputIt.current() == '\n') // Se caracter for \n entao incrementa número de linhas
                lineNumber++;
            
            inputIt.next();
        } // Fim do ignoramento

        //------------------------------------------------------------------------------------------------------
        
        // Se acabou, retorno token EOF
        if(inputIt.getIndex() >= inputIt.getEndIndex() - 1 || inputIt.current() == StringCharacterIterator.DONE)
        {
            tok.name = EnumToken.EOF;
            tok.lineNumber = lineNumber-1;
            return tok;
        }   
        
        // Reconhecer ID (que começa sempre com letra)
        else if(Character.isLetter(inputIt.current())) // Se caracter atual é uma letra maiúscula ou minúscula
        {
            lexeme += inputIt.current();
            
            inputIt.next();
            // Enquanto forem letras, dígitos ou underscores
            while(Character.isLetterOrDigit(inputIt.current()) || inputIt.current() == '_')
            {
                lexeme += inputIt.current();
                inputIt.next();
                
                // Tratando caso do SOPRINTLN 
                if(lexeme.equals("System"))
                { 
                    lexeme = sout(lexeme, inputIt, tok);
                }
                // Por não tratar caso do int e int[] aqui, tive que adaptar bem o Parser
            }
            
            //Ao final do reconhecimento, busca o lexema reconhecido na tabela 
            //de símbolos para ver se está lá.
            STEntry entry = st.get(lexeme);
            
            if (entry != null)
                tok.name = entry.tokName;
            else
                tok.name = EnumToken.ID;                       

            tok.value = lexeme;
            tok.lineNumber = lineNumber;
            System.out.printf("%s\n", lexeme);
            
        }
        // Reconhecendo números
        else if(Character.isDigit(inputIt.current())) // Reconhecer números
        {
            lexeme += inputIt.current();
            
            tok.attribute = EnumToken.NUMBER; // É um número
            inputIt.next();
            // Enquanto forem dígitos
            while(Character.isDigit(inputIt.current())){
                lexeme += inputIt.current();
                inputIt.next();
            }
            
            tok.value = lexeme;
            tok.lineNumber = lineNumber;
            tok.name = EnumToken.INTEGER_LITERAL; // Todos números em MiniJava são inteiros
            System.out.printf("%s\n", lexeme);
        }
        //Reconhecendo operadores (exceto DIV)
        else if(inputIt.current() == '&' || inputIt.current() == '<' || inputIt.current() == '>' ||
                inputIt.current() == '+' || inputIt.current() == '-' || inputIt.current() == '*' ||
                inputIt.current() == '=' || inputIt.current() == '!')
        {
            
            
            switch(inputIt.current())
            {
                case '&': // And
                    inputIt.next();
                    if(inputIt.current() == '&')
                    {
                        tok.name = EnumToken.AND;
                    }    
                    tok.attribute = EnumToken.LOGOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '<': // Lesser than
                    tok.name = EnumToken.LT;
                    tok.attribute = EnumToken.RELOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '>': // Greater than
                    tok.name = EnumToken.GT;
                    tok.attribute = EnumToken.RELOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '+': // Plus
                    tok.name = EnumToken.PLUS;
                    tok.attribute = EnumToken.ARITHOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '-': // Minus
                    tok.name = EnumToken.MINUS;
                    tok.attribute = EnumToken.ARITHOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '*': // Mult
                    tok.name = EnumToken.MULT;
                    tok.attribute = EnumToken.ARITHOP;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                
                case '=': 
                    inputIt.next();
                    if(inputIt.current() == '=') // Equals
                    {
                        tok.name = EnumToken.EQ;
                        tok.attribute = EnumToken.RELOP;
                        tok.lineNumber = lineNumber;
                        inputIt.next();
                    }
                    else // se próximo símbolo não for '=' então é operação de atribuição
                    {
                        tok.name = EnumToken.ATTRIB;
                        tok.attribute = EnumToken.RELOP;
                        tok.lineNumber = lineNumber;
                    }
                    break;
                
                case '!':
                    inputIt.next();
                    if(inputIt.current() == '=') // Not equals
                    {
                        tok.name = EnumToken.NE;
                        tok.attribute = EnumToken.RELOP;
                        tok.lineNumber = lineNumber;
                        inputIt.next();
                    }
                    else
                    {
                        tok.name = EnumToken.NOT;
                        tok.attribute = EnumToken.LOGOP;
                        tok.lineNumber = lineNumber;
                    } 
            }
            //lexeme += inputIt.current();
        }
        // Reconhecendo separadores
        else if(inputIt.current() == '(' || inputIt.current() == ')' || 
                inputIt.current() == '[' || inputIt.current() == ']' ||
                inputIt.current() == '{' || inputIt.current() == '}' ||
                inputIt.current() == ';' || inputIt.current() == '.' ||
                inputIt.current() == ',')
        {
            tok.attribute = EnumToken.SEP;
            //tok.name = EnumToken.
            switch(inputIt.current())
            {
                case '(':
                    tok.name = EnumToken.LPARENTHESE;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case ')':
                    tok.name = EnumToken.RPARENTHESE;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case '[':
                    tok.name = EnumToken.LBRACKET;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case ']':
                    tok.name = EnumToken.RBRACKET;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case '{':
                    tok.name = EnumToken.LBRACE;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case '}':
                    tok.name = EnumToken.RBRACE;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case ';':
                    tok.name = EnumToken.SEMICOLON;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case '.':
                    tok.name = EnumToken.PERIOD;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                case ',':
                    tok.name = EnumToken.COMMA;
                    tok.lineNumber = lineNumber;
                    inputIt.next();
                    break;
                    
                    
                    
            } // fim switch
        }
        /*else{
            throw new CompilerException("toin");
        }*/
        return tok;
    } // fim nextToken
    // --------------------------------------------------------------------------------------------------------
    // Métodos auxiliares
    public String getInput()
    {
        return this.input;
    }
    
    public String sout(String lexeme, StringCharacterIterator inputIt, Token tok)
    {
        int achou = 1;
        
        if(inputIt.current() == '.'){
            lexeme += inputIt.current();
            inputIt.next();
            while(Character.isLetter(inputIt.current())){
                lexeme += inputIt.current();
                inputIt.next();
                if(lexeme.equals("System.out")){   
                    if(inputIt.current() == '.'){
                        lexeme += inputIt.current();
                        inputIt.next();
                        while(Character.isLetter(inputIt.current()) && achou == 1){
                            lexeme += inputIt.current();
                            inputIt.next();
                            if(lexeme.equals("System.out.println"))
                            {
                                achou = 0;
                                return lexeme;
                            }
                        }
                    }
                }
            }
        }
        return lexeme;   
    }
}