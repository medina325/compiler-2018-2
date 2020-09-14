package mjcompiler;

import mjcompiler.view.main.MainScreenJF;

public class MJCompiler 
{
    //Método main para testar apenas o analisador léxico. Considera que a
    //entrada está salva em um arquivo com nome "teste1.mj"
    public static void main(String[] args) 
    {                   
        // GUI
        MainScreenJF janela = new MainScreenJF();
        janela.setVisible(true);
    }

}
