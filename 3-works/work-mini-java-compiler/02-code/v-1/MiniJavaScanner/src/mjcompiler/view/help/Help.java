
package mjcompiler.view.help;

import mjcompiler.view.about.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class Help extends JDialog {
   
    public Help(JFrame parent) throws FileNotFoundException, IOException {
        super(parent,true);
        initComponents();
        pack();
        setTitle("Help MiniJava Compiler 0.0.1");
        Rectangle parentBounds = parent.getBounds();
        Dimension size = getSize();
        // Center in the parent
        int x = Math.max(0, parentBounds.x + (parentBounds.width - size.width) / 2);
        int y = Math.max(0, parentBounds.y + (parentBounds.height - size.height) / 2);
        setLocation(new Point(x, y));
        
       //get InputStream of a file
        InputStream is = getClass().getResourceAsStream("help.html");
        String strContent = "";
        //Create BufferedReader object
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbfFileContents = new StringBuffer();
        String line = null;

        //read file line by line
        while( (line = bReader.readLine()) != null){
                sbfFileContents.append(line);
        }

        //finally convert StringBuffer object to String!
        strContent = sbfFileContents.toString();

        txtHelp.setContentType("text/html");
        txtHelp.setText(strContent);
        txtHelp.setEditable(false);
       
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollHelp = new javax.swing.JScrollPane();
        txtHelp = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About Anagrams");

        scrollHelp.setViewportView(txtHelp);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollHelp, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollHelp, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollHelp;
    private javax.swing.JTextPane txtHelp;
    // End of variables declaration//GEN-END:variables

}
