package mjcompiler.controller;

import mjcompiler.helpers.util.CompilerException;
import mjcompiler.helpers.util.EnumToken;
import mjcompiler.model.STEntry;
import mjcompiler.model.SymbolTable;
import mjcompiler.controller.Scanner;
import mjcompiler.model.Token;
import mjcompiler.view.main.MainScreenJF;

public class Parser 
{
    // Atributos para análise sintática
    private Scanner scan;
    private SymbolTable globalST;
    private SymbolTable currentST;
    private Token lToken;
    
    // Atributos específicos para análise semântica
    private SymbolTable classST = new SymbolTable<STEntry>(); // Guarda lexema de todas as Classes
    private SymbolTable classScopeST = new SymbolTable<STEntry>(); // Guarda variáveis do escopo de uma Classe
    private SymbolTable methodST = new SymbolTable<STEntry>(); // Guarda lexema de todos os Métodos
    private SymbolTable methodScopeST = new SymbolTable<STEntry>(classScopeST); // Guarda variáveis do escopo do Método e da Classe 
                                                                                // a que pertence
    private Token nT; // Token auxiliar para guardar tipos de variáveis e Métodos
    private STEntry rT1 = new STEntry(); // 
    private STEntry rT2 = new STEntry();
    private STEntry referenciaMetodo = new STEntry();
    private STEntry auxRef = new STEntry(); // Auxiliar para verificar tipo de um método quando atribuído à uma variável
    private STEntry array;
    
    public Parser(String inputFile) throws CompilerException
    {
        //Instancia a tabela de símbolos global e a inicializa
        globalST = new SymbolTable<STEntry>();
        initSymbolTable();
     
        //Faz o ponteiro para a tabela do escopo atual apontar para a tabela global
        currentST = globalST;
        
        //Instancia o analisador léxico
        scan = new Scanner(globalST, inputFile);
        
        // Instancia tabela com lexemas de todas Classes
        initClassTable(inputFile); 
        
        // Obs.: classST deve ser inicializada antes de methodST, pois os métodos podem ser de tipos abstratos
        
        // Instancia tabela com tipos e lexemas de todo Métodos
        initMethodTable(inputFile);
    }
    
    /*
     * Método que inicia o processo de análise sintática do compilador
     */
    public void execute() throws CompilerException
    {
        advance();
        program();
    }
    private void advance() throws CompilerException
    {
        lToken = scan.nextToken();
        
        System.out.print(lToken.name + "(" + lToken.lineNumber + ")" + " " );
    }
    private void match(EnumToken cTokenName) throws CompilerException
    {
        if (lToken.name == cTokenName)
            advance();
        else
        {            //Erro
            throw new CompilerException("Token inesperado: " + lToken.name + "\n"
                    + "Linha: " + lToken.lineNumber);
        }
    }
    
    /*
     * Método para o símbolo inicial da gramática
     */    
    private void program() throws CompilerException
    {
        mainClass();
        
        while (lToken.name == EnumToken.CLASS){ 
            classDeclaration();
        }
        
        match(EnumToken.EOF);
        //classST.clear();
        //methodST.clear();
        System.out.println("\nCompilação encerrada com sucesso");
        
    }    
    
    private void mainClass() throws CompilerException
    {
        match(EnumToken.CLASS);
        match(EnumToken.ID);
        match(EnumToken.LBRACE);
        match(EnumToken.PUBLIC);
        match(EnumToken.STATIC);
        match(EnumToken.VOID);
        match(EnumToken.MAIN);
        match(EnumToken.LPARENTHESE);
        match(EnumToken.STRING);
        match(EnumToken.LBRACKET);
        match(EnumToken.RBRACKET);
        
        classScopeST.add(new STEntry(lToken.value));
        match(EnumToken.ID);
        
        match(EnumToken.RPARENTHESE);
        match(EnumToken.LBRACE);        
        statement();
        match(EnumToken.RBRACE);
        match(EnumToken.RBRACE);  
        
        classScopeST.clear(); // Limpando tabela
    }
    private void classDeclaration() throws CompilerException
    {        
        match(EnumToken.CLASS);
        match(EnumToken.ID);
        
        if(lToken.name == EnumToken.EXTENDS)
        {
            match(EnumToken.EXTENDS);
            if(classST.get(lToken.value) != null) // Verifica se classe está na classST
                match(EnumToken.ID);
            else
                throw new CompilerException("Erro Semântico: Classe " + lToken.value + " não declarada" 
                 + "\nLinha: " + lToken.lineNumber);
        }
        match(EnumToken.LBRACE);
        while(lToken.name == EnumToken.INT || lToken.name == EnumToken.BOOLEAN || lToken.name == EnumToken.ID)
        {
            varDeclaration(classScopeST);
        }
        while(lToken.name == EnumToken.PUBLIC)
        {
            methodDeclaration();
        }
        match(EnumToken.RBRACE);
        
        classScopeST.clear(); // Limpando tabela
    }
    
    /*  1 - Os dois primeiros if e else if de varDeclaration tratam o caso em que statement é chamado após 
        varDeclaration
        2 - É preciso passar auxST pois varDeclaration pode ser chamado no escopo de uma classe ou de um
            método
    */
    private void varDeclaration(SymbolTable auxST) throws CompilerException
    {
        // nT.name vai conter tipo da variável e rT1 vai referenciar uma variável na tabela (se for uma atribuição)
        type(auxST); // nT.name vai conter tipo da variável 
        
        // Se próximo token for '=' então quero verificar se uma variável foi declarada
        if(lToken.name == EnumToken.ATTRIB)
        { 
            if(auxST.get(nT.value) != null)
            {
                match(EnumToken.ATTRIB);
                expression();
                
                verificaTiposAtrib();
                
                match(EnumToken.SEMICOLON);
            }else
                throw new CompilerException("Erro Semântico: Variável " + nT.value + " não declarada" 
                 + "\nLinha: " + lToken.lineNumber);
        } 
        
        else if(lToken.name == EnumToken.LBRACKET)
        {
            if(rT1 != null) // Verificando se variável foi declarada
            {
                if(rT1.tokName == EnumToken.ARRAY){
                    match(EnumToken.LBRACKET);
                    expression();
                    match(EnumToken.RBRACKET);
                    match(EnumToken.ATTRIB);
                    expression();  
                    
                    if(rT2.tokName != EnumToken.INT)
                    {
                        throw new CompilerException("Erro Semântico: " + rT1.lexeme + "[] espera tipo INT"
                        + "\nLinha: " + lToken.lineNumber);
                    }
                    
                    match(EnumToken.SEMICOLON);
                }
                else
                    throw new CompilerException("Erro Semântico: Variável " + nT.value + " precisa ser Array"
                    + "\nLinha: " + lToken.lineNumber);
            }
            else
                throw new CompilerException("Erro Semântico: Variável " + nT.value + " não declarada"
                + "\nLinha: " + lToken.lineNumber);
        }
        
        else
        { // Adiciona o tipo e o nome da variável em classScopeST
            
            /*  1 - Se o tipo for INT ou BOOLEAN então lineNumber = -1
                2 - Se o tipo for abstrato então lineNumber = -2
            */
            if(nT.lineNumber == -1)
            {
                auxST.add(new STEntry(nT.name, lToken.value));
            }
            else if(nT.lineNumber == -2)
            {
                if(classST.get(nT.value) != null)
                    auxST.add(new STEntry(nT.value, lToken.value));
                else
                    throw new CompilerException("Erro Semântico: Tipo abstrato " + nT.value + " não declarado" 
                    + "\nLinha: " + lToken.lineNumber);
            }
            
            match(EnumToken.ID);
            match(EnumToken.SEMICOLON);
        }
    }
    private void methodDeclaration() throws CompilerException 
    {
        match(EnumToken.PUBLIC);
        type();
        
        referenciaMetodo = methodST.get(lToken.value); // Pega referencia desse Método na tabela (para verificar tipo de retorno)
        
        match(EnumToken.ID);
        match(EnumToken.LPARENTHESE);
       
        // Passagem de parâmetros
        if(lToken.name == EnumToken.INT || lToken.name == EnumToken.BOOLEAN || lToken.name == EnumToken.ID)
        {
            type();
            
            if(nT.lineNumber == -1)
            { 
                methodScopeST.add(new STEntry(nT.name, lToken.value)); // PRESTE ATENÇÃO NO ESCOPO
            }
            else if(nT.lineNumber == -2)
            {
                if(classST.get(nT.value) != null)
                    methodScopeST.add(new STEntry(nT.value, lToken.value));
                else
                    throw new CompilerException("Erro Semântico: Tipo abstrato " + nT.value + " não declarado" 
                    + "\nLinha: " + lToken.lineNumber);
            }    
            
            match(EnumToken.ID);
            while(lToken.name == EnumToken.COMMA)
            {
                match(EnumToken.COMMA);
                type();
                
                if(nT.lineNumber == -1)
                { 
                    methodScopeST.add(new STEntry(nT.name, lToken.value)); // PRESTE MUITA ATENÇÃO NO ESCOPO
                }
                else if(nT.lineNumber == -2) 
                {
                    if(classST.get(nT.value) != null)
                        methodScopeST.add(new STEntry(nT.value, lToken.value));
                    else
                        throw new CompilerException("Erro Semântico: Tipo abstrato " + nT.value + " não declarado" 
                        + "\nLinha: " + lToken.lineNumber);
                }   
                
                match(EnumToken.ID);
            }
        }
        match(EnumToken.RPARENTHESE);
        match(EnumToken.LBRACE);
        
        while(lToken.name == EnumToken.INT || lToken.name == EnumToken.BOOLEAN || lToken.name == EnumToken.ID)
        {
            varDeclaration(methodScopeST);
        }                  
        while(lToken.name == EnumToken.LBRACE || lToken.name == EnumToken.IF || lToken.name == EnumToken.WHILE ||
              lToken.name == EnumToken.SOPRINTLN ||lToken.name == EnumToken.ID)
        {
            statement();
        }
        match(EnumToken.RETURN);
        expression();
        
        verificaTipoRetorno(); 
        
        match(EnumToken.SEMICOLON);
        match(EnumToken.RBRACE);
        
        methodScopeST.clear(); // Limpando tabela
    }
    
    // Método type típico usado na maioria do código
    private void type() throws CompilerException
    {
        if(lToken.name == EnumToken.INT){
            match(EnumToken.INT);
            t_();
        }
        else if(lToken.name == EnumToken.BOOLEAN){
            nT = new Token(EnumToken.BOOLEAN);
            match(EnumToken.BOOLEAN);
        }
        else{           
            nT = new Token(EnumToken.ID, lToken.value); // nT.value contém lexema do tipo abstrato
            
            match(EnumToken.ID); 
        }
    }
    
    /*  Método type com parâmetro foi criado especificamente para a correção de operações 
        de atribuição que vêm logo após declarações de variáveis
    */
    private void type(SymbolTable auxST) throws CompilerException
    {
        if(lToken.name == EnumToken.INT){
            match(EnumToken.INT);
            t_();
        }
        else if(lToken.name == EnumToken.BOOLEAN){
            nT = new Token(EnumToken.BOOLEAN);
            match(EnumToken.BOOLEAN);
        }
        else{ // ID pode ser tanto lexema de Classe quanto nome de variável
            // Portanto           
            rT1 = auxST.get(lToken.value);
            
            nT = new Token(EnumToken.ID, lToken.value); // nT.value contém lexema do tipo abstrato
            
            match(EnumToken.ID); 
        }
    }
    private void t_() throws CompilerException
    {
        if(lToken.name == EnumToken.LBRACKET){
            
            match(EnumToken.LBRACKET);
            match(EnumToken.RBRACKET);
            nT = new Token(EnumToken.ARRAY);
        }
        else{
            nT = new Token(EnumToken.INT);
        }
        // Ou não faz nada
    }
    private void statement() throws CompilerException
    {
        if(lToken.name == EnumToken.LBRACE)
        {
            match(EnumToken.LBRACE);
            while(lToken.name == EnumToken.LBRACKET || lToken.name == EnumToken.IF || lToken.name == EnumToken.WHILE ||
                  lToken.name == EnumToken.SOPRINTLN ||lToken.name == EnumToken.ID)
            {
                statement();
            }
            match(EnumToken.RBRACE);
        }
        else if(lToken.name == EnumToken.IF)
        {
            match(EnumToken.IF);
            match(EnumToken.LPARENTHESE);
            expression();
            match(EnumToken.RPARENTHESE);
            statement();
            match(EnumToken.ELSE);
            statement();
        } 
        else if(lToken.name == EnumToken.WHILE)
        {
            match(EnumToken.WHILE);
            match(EnumToken.LPARENTHESE);
            expression();
            match(EnumToken.RPARENTHESE);
            statement();
        }
        else if(lToken.name == EnumToken.SOPRINTLN)
        {
            match(EnumToken.SOPRINTLN);
            match(EnumToken.LPARENTHESE);
            expression();
            match(EnumToken.RPARENTHESE);
            match(EnumToken.SEMICOLON);
        }
        else //if(lToken.name == EnumToken.ID)
        {
            if(methodScopeST.get(lToken.value) != null)
            {
                
                rT1 = methodScopeST.get(lToken.value); // Salvando referência de ID da tabela
                
                match(EnumToken.ID);
                s_();
            }else
                throw new CompilerException("Erro Semântico: Variável " + lToken.value + " não declarada" 
                + "\nLinha: " + lToken.lineNumber);
        }
    }
    private void s_() throws CompilerException
    {
        if(lToken.name == EnumToken.ATTRIB){
            
            match(EnumToken.ATTRIB);
            expression();
            
            verificaTiposAtrib();
            
            match(EnumToken.SEMICOLON);
        }
        else{ 
            if(rT1.tokName == EnumToken.ARRAY)
            {
                match(EnumToken.LBRACKET);
                expression();
                match(EnumToken.RBRACKET);
                match(EnumToken.ATTRIB);
                expression();
  
            }else
                throw new CompilerException("Erro Semântico: Variável " + rT1.lexeme + " precisa ser Array"
                + "\nLinha: " + lToken.lineNumber);
            
            
            //verificaTiposAtrib(); // Não vou chamar o Método aqui pois creio que fique mais intuitivo
            if(rT2.tokName != EnumToken.INT)
            {
                throw new CompilerException("Erro Semântico: " + rT1.lexeme + "[] espera tipo INT"
                + "\nLinha: " + lToken.lineNumber);
            }
                   
            match(EnumToken.SEMICOLON);
        }
    }
    private void expression() throws CompilerException
    {
        if(lToken.name == EnumToken.INTEGER_LITERAL)
        {
            rT2 = new STEntry(EnumToken.INT, lToken.value); // rT2.tokName = INT;
            
            match(EnumToken.INTEGER_LITERAL);
            e_();
        }
        else if(lToken.name == EnumToken.TRUE)
        {
            rT2 = new STEntry(EnumToken.BOOLEAN, lToken.value); // rT2.tokName = BOOLEAN;
            
            match(EnumToken.TRUE);
            e_();
        }
        else if(lToken.name == EnumToken.FALSE)
        {
            rT2 = new STEntry(EnumToken.BOOLEAN, lToken.value); // rT2.tokName = BOOLEAN;
            
            match(EnumToken.FALSE);
            e_();
        }
        else if(lToken.name == EnumToken.ID)
        {
            if(methodScopeST.get(lToken.value) != null)
            {
                rT2 = methodScopeST.get(lToken.value);                
                
                match(EnumToken.ID);
                e_();
            }else
                throw new CompilerException("Erro Semântico: Variável " + lToken.value + " não declarada" 
                + "\nLinha: " + lToken.lineNumber);
        }
        else if(lToken.name == EnumToken.THIS)
        {
            match(EnumToken.THIS);
            e_();
        }
        else if(lToken.name == EnumToken.NEW)
        {
            match(EnumToken.NEW);
            e_Id();
        }
        else if(lToken.name == EnumToken.NOT)
        {
            match(EnumToken.NOT);
            expression();
            e_();
        } 
        else 
        {
            match(EnumToken.LPARENTHESE);
            expression();
            match(EnumToken.RPARENTHESE);
            e_();
        }
    }
    private void e_() throws CompilerException
    {
        if(lToken.name == EnumToken.PLUS || lToken.name == EnumToken.MINUS || lToken.name == EnumToken.MULT || 
           lToken.name == EnumToken.DIV ||  lToken.name == EnumToken.EQ ||  lToken.name == EnumToken.NE || 
           lToken.name == EnumToken.GT || lToken.name == EnumToken.LT || lToken.name == EnumToken.AND )
        {
            op();
            expression();
            e_();
        }
        else if(lToken.name == EnumToken.LBRACKET)
        {
            match(EnumToken.LBRACKET);
           
            expression();
            match(EnumToken.RBRACKET);
            e_();
        }
        else if(lToken.name == EnumToken.PERIOD)
        {
            match(EnumToken.PERIOD);
            e_Period();
         
            rT2 = auxRef; // Guardando em rT2
        }
        else{
        }
        // Epsilon
        
    }
    private void e_Id() throws CompilerException
    {
        if(lToken.name == EnumToken.INT)
        {
            array = new STEntry(EnumToken.ARRAY); // Tenho que armazenar em um lugar diferente
            
            match(EnumToken.INT);
            match(EnumToken.LBRACKET);
            expression(); // Esse expression me força a criar outras variáveis e 
            match(EnumToken.RBRACKET);
            e_();
        }
        else
        {
            if(classST.get(lToken.value) != null)
            {
                rT2 = classST.get(lToken.value);
                rT2.tokNameAbst = lToken.value;
                
                match(EnumToken.ID);
                match(EnumToken.LPARENTHESE);
                match(EnumToken.RPARENTHESE);
                e_();
            }else
                throw new CompilerException("Erro Semântico: Classe " + lToken.value + " não declarada"
                + "\nLinha: " + lToken.lineNumber);
        }
    }
    private void e_Period() throws CompilerException
    {
        if(lToken.name == EnumToken.LENGTH)
        {
            rT2 = new STEntry(EnumToken.INT, "LENGTH");
            
            match(EnumToken.LENGTH);
            e_();
        }
        else // Se for nome de método
        { 
            auxRef = methodST.get(lToken.value); // Guardando referência da tabela ao Método 
            
            
            if(lToken.name == EnumToken.ID)
            {
                if(methodST.get(lToken.value) != null)
                {
                    match(EnumToken.ID);
                    match(EnumToken.LPARENTHESE); 
                    if( lToken.name == EnumToken.INTEGER_LITERAL || lToken.name == EnumToken.TRUE || lToken.name == EnumToken.FALSE ||
                        lToken.name == EnumToken.ID || lToken.name == EnumToken.THIS || lToken.name == EnumToken.NEW || 
                        lToken.name == EnumToken.NOT || lToken.name == EnumToken.LPARENTHESE)
                    {
                        expression();
                        if(lToken.name == EnumToken.COMMA){
                            while(lToken.name == EnumToken.COMMA){
                                match(EnumToken.COMMA);
                                expression(); // Esse expression me obriga a criar auxRef
                            }
                        }
                    }
                    match(EnumToken.RPARENTHESE);
                    e_();
                }
                else
                    throw new CompilerException("Erro Semântico: Método " + lToken.value + " não declarado"
                    + "\nLinha: " + lToken.lineNumber);
            }
        }
    }
    private void op() throws CompilerException
    {
        if(lToken.name == EnumToken.PLUS){
            match(EnumToken.PLUS);
        }
        else if(lToken.name == EnumToken.MINUS){
            match(EnumToken.MINUS);
        }
        else if(lToken.name == EnumToken.MULT){
            match(EnumToken.MULT);
        }
        else if(lToken.name == EnumToken.DIV){
            match(EnumToken.DIV);
        }
        else if(lToken.name == EnumToken.EQ){
            match(EnumToken.EQ);
        }
        else if(lToken.name == EnumToken.NE){
            match(EnumToken.NE);
        }
        else if(lToken.name == EnumToken.GT){
            match(EnumToken.GT);
        }
        else if(lToken.name == EnumToken.LT){
            match(EnumToken.LT);
        }
        else{
            match(EnumToken.AND);
        }
    }
    
    /*
        Métodos que inicializam tabelas de símbolos
    */
    private void initSymbolTable()
    {
        // Lista de palavras reservadas
        globalST.add(new STEntry(EnumToken.BOOLEAN, "boolean", true));
        globalST.add(new STEntry(EnumToken.CLASS, "class", true));
        globalST.add(new STEntry(EnumToken.ELSE, "else", true));
        globalST.add(new STEntry(EnumToken.EXTENDS, "extends", true));
        globalST.add(new STEntry(EnumToken.FALSE, "false", true));
        globalST.add(new STEntry(EnumToken.IF, "if", true));
        globalST.add(new STEntry(EnumToken.INT, "int", true));
        globalST.add(new STEntry(EnumToken.LENGTH, "length", true));
        globalST.add(new STEntry(EnumToken.MAIN, "main", true));
        globalST.add(new STEntry(EnumToken.NEW, "new", true));
        globalST.add(new STEntry(EnumToken.PUBLIC, "public", true));
        globalST.add(new STEntry(EnumToken.RETURN, "return", true));
        globalST.add(new STEntry(EnumToken.STATIC, "static", true));
        globalST.add(new STEntry(EnumToken.STRING, "String", true));
        globalST.add(new STEntry(EnumToken.SOPRINTLN, "System.out.println", true));
        globalST.add(new STEntry(EnumToken.THIS, "this", true));
        globalST.add(new STEntry(EnumToken.TRUE, "true", true));
        globalST.add(new STEntry(EnumToken.VOID, "void", true));
        globalST.add(new STEntry(EnumToken.WHILE, "while", true));
    }
    private void initClassTable(String inputFile) 
    {
        Scanner scanAux = new Scanner(globalST, inputFile); // Scanner auxiliar
        Token auxTok = scanAux.nextToken(); // Token auxliar para percorrer inputFile
        
        do
        {   // Se token for CLASS então o próximo é o nome da classe a ser adicionada
            if(auxTok.name == EnumToken.CLASS)
            { 
                auxTok = scanAux.nextToken();
                classST.add(new STEntry(auxTok.value));
            }
            auxTok = scanAux.nextToken();
        }while(auxTok.name != EnumToken.EOF);
    }
    private void initMethodTable(String inputFile)
    {
        Scanner scanAux = new Scanner(globalST, inputFile);
        Token auxTok = scanAux.nextToken();
        Token auxTipo = new Token(EnumToken.UNDEF); // Token auxiliar para guardar tipo do Método
        
        do
        {
            // Se token for PUBLIC então os dois próximos tokens são o tipo e o lexema do método a serem adicionados (exceto main)
            if(auxTok.name == EnumToken.PUBLIC)
            {
                /*  1 - Se o tipo for INT ou BOOLEAN então lineNumber = -1
                    2 - Se o tipo for abstrato então lineNumber = -2
                */
                auxTok = scanAux.nextToken();
                
                if(auxTok.name == EnumToken.ID)
                {
                    auxTipo = new Token(EnumToken.ID, auxTok.value); // Tipo abstrato
                    auxTok = scanAux.nextToken(); // Lexema do Método
                }
                else if(auxTok.name == EnumToken.INT) // Gambiarra
                {
                    auxTok = scanAux.nextToken();
                    if(auxTok.name == EnumToken.LBRACKET)
                    {
                        auxTok = scanAux.nextToken();
                        if(auxTok.name == EnumToken.RBRACKET)
                            auxTipo = new Token(EnumToken.ARRAY);
                        auxTok = scanAux.nextToken(); // Lexema do Método
                    }
                    else
                        auxTipo = new Token(EnumToken.INT); // auxTok já está com lexema do Método
                }   
                else if(auxTok.name == EnumToken.BOOLEAN)
                {
                    auxTipo = new Token(EnumToken.BOOLEAN);
                    auxTok = scanAux.nextToken();
                }
                
                
                if(auxTipo.lineNumber == -1)
                { 
                    methodST.add(new STEntry(auxTipo.name, auxTok.value)); // PRESTE ATENÇÃO NO ESCOPO
                }
                else if(auxTipo.lineNumber == -2)
                {
                    if(classST.get(auxTipo.value) != null)
                        methodST.add(new STEntry(auxTipo.value, auxTok.value));
                    else
                        throw new CompilerException("Erro Semântico: Tipo abstrato" + auxTipo.value + " não declarado" 
                        + "\nLinha: " + auxTok.lineNumber);
                }    
            }
        
            auxTok = scanAux.nextToken();
        
        }while(auxTok.name != EnumToken.EOF);
    }
    
    /*
        Métodos que verificam tipos
    */
    private void verificaTipoRetorno(){
        /*if(array != null)
            rT2 = array;*/
        
        if(referenciaMetodo.abstrato == true){
            if(referenciaMetodo.tokNameAbst.equals(rT2.tokNameAbst) == false) // (tok.nameAbst != rT2.tokNameAbst) não funciona!!!
            {
                if(rT2.tokNameAbst != null)
                {
                    throw new CompilerException("Erro Semântico: Método " + referenciaMetodo.lexeme + " retorna " 
                    + referenciaMetodo.tokNameAbst + " mas " + rT2.lexeme + " é de tipo " + rT2.tokNameAbst + "\nLinha: " + lToken.lineNumber);
                }
                else{
                    throw new CompilerException("Erro Semântico: Método " + referenciaMetodo.lexeme + " retorna "
                    + referenciaMetodo.tokNameAbst + " mas " + rT2.lexeme + " é de tipo " + rT2.tokName + "\nLinha: " + lToken.lineNumber);
                }
            }
        }
        else{
            if(referenciaMetodo.tokName.equals(rT2.tokName) == false){
                if(rT2.tokName != null)
                {
                    throw new CompilerException("Erro Semântico: Método " + referenciaMetodo.lexeme + " retorna "
                    + referenciaMetodo.tokName + " mas " + rT2.lexeme + " é de tipo " + rT2.tokName + "\nLinha: " + lToken.lineNumber);
                }
                else{
                    throw new CompilerException("Erro Semântico: Método " + referenciaMetodo.lexeme + " retorna "
                    + referenciaMetodo.tokName + " mas " + rT2.lexeme + " é de tipo " + rT2.tokNameAbst + "\nLinha: " + lToken.lineNumber);
                }
            }
        }
        
        array = null;
    }
    private void verificaTiposAtrib(){
        if(array != null)
            rT2 = array;
        
        if(rT1.abstrato == true){
            if(rT1.tokNameAbst.equals(rT2.tokNameAbst) == false) // (tok.nameAbst != rT2.tokNameAbst) não funciona!!!
            {
                if(rT2.tokNameAbst != null)
                {
                    throw new CompilerException("Erro Semântico: " + rT1.lexeme + " espera tipo " + rT1.tokNameAbst
                    + " mas " + rT2.lexeme + " é de tipo " + rT2.tokNameAbst + "\nLinha: " + lToken.lineNumber);
                    }
                else{
                    throw new CompilerException("Erro Semântico: " + rT1.lexeme + " espera tipo " + rT1.tokNameAbst
                    + " mas " + rT2.lexeme + " é de tipo " + rT2.tokName + "\nLinha: " + lToken.lineNumber);
                }
            }
        }
        else{
            if(rT1.tokName.equals(rT2.tokName) == false){
                if(rT2.tokName != null)
                {
                    throw new CompilerException("Erro Semântico: " + rT1.lexeme + " espera tipo " + rT1.tokName
                    + " mas " + rT2.lexeme + " é de tipo " + rT2.tokName + "\nLinha: " + lToken.lineNumber);
                }
                else{
                    throw new CompilerException("Erro Semântico: " + rT1.lexeme + " espera tipo " + rT1.tokName
                    + " mas " + rT2.lexeme + " é de tipo " + rT2.tokNameAbst + "\nLinha: " + lToken.lineNumber);
                }
            }
        }
        
        array = null;
    }
    //-------------------------------------------------------------------------------------------------------------------
    
    public Scanner getScan() {
        return scan;
    }  
    
}