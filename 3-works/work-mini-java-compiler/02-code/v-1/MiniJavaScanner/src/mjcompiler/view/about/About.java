
package mjcompiler.view.about;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class About extends JDialog {

   
    public About(JFrame parent) {
        super(parent,true);
        initComponents();
        pack();
        setTitle("About");
        Rectangle parentBounds = parent.getBounds();
        Dimension size = getSize();
        // Center in the parent
        int x = Math.max(0, parentBounds.x + (parentBounds.width - size.width) / 2);
        int y = Math.max(0, parentBounds.y + (parentBounds.height - size.height) / 2);
        setLocation(new Point(x, y));
       
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        txtProjectIcon = new javax.swing.JLabel();
        txtProjectName = new javax.swing.JLabel();
        txtProjectVersion = new javax.swing.JLabel();
        txtProjectDescription1 = new javax.swing.JTextArea();
        txtProjectDescription2 = new javax.swing.JTextArea();
        txtProjectCopyright = new javax.swing.JTextArea();
        txtProjectAuthors = new javax.swing.JTextArea();
        txtProjectLicence = new javax.swing.JTextArea();
        txtProjectAuthors1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About Anagrams");
        setResizable(false);

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 11, 12, 12));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtProjectIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mjcompiler/helpers/icons/icon.png"))); // NOI18N
        mainPanel.add(txtProjectIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 60, 90));

        txtProjectName.setText("MiniJava Compiler");
        mainPanel.add(txtProjectName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, -1, -1));

        txtProjectVersion.setText("0.0.1");
        mainPanel.add(txtProjectVersion, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, -1, -1));

        txtProjectDescription1.setEditable(false);
        txtProjectDescription1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectDescription1.setColumns(25);
        txtProjectDescription1.setLineWrap(true);
        txtProjectDescription1.setRows(8);
        txtProjectDescription1.setText("MiniJava is a simple compiler.");
        txtProjectDescription1.setWrapStyleWord(true);
        txtProjectDescription1.setBorder(null);
        txtProjectDescription1.setFocusable(false);
        txtProjectDescription1.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectDescription1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 190, 20));

        txtProjectDescription2.setEditable(false);
        txtProjectDescription2.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectDescription2.setColumns(25);
        txtProjectDescription2.setLineWrap(true);
        txtProjectDescription2.setRows(8);
        txtProjectDescription2.setText("It has functions of lexical, syntactic and semantic analysis.");
        txtProjectDescription2.setWrapStyleWord(true);
        txtProjectDescription2.setBorder(null);
        txtProjectDescription2.setFocusable(false);
        txtProjectDescription2.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectDescription2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 370, 30));

        txtProjectCopyright.setEditable(false);
        txtProjectCopyright.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectCopyright.setColumns(25);
        txtProjectCopyright.setLineWrap(true);
        txtProjectCopyright.setRows(8);
        txtProjectCopyright.setText("MiniJava Compiler Copyleft (c) 2018.\n");
        txtProjectCopyright.setWrapStyleWord(true);
        txtProjectCopyright.setBorder(null);
        txtProjectCopyright.setFocusable(false);
        txtProjectCopyright.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectCopyright, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 260, 20));

        txtProjectAuthors.setEditable(false);
        txtProjectAuthors.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectAuthors.setColumns(25);
        txtProjectAuthors.setLineWrap(true);
        txtProjectAuthors.setRows(8);
        txtProjectAuthors.setText("     Thanks:\n- Prof. Bianca");
        txtProjectAuthors.setWrapStyleWord(true);
        txtProjectAuthors.setBorder(null);
        txtProjectAuthors.setFocusable(false);
        txtProjectAuthors.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectAuthors, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 270, 120, 40));

        txtProjectLicence.setEditable(false);
        txtProjectLicence.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectLicence.setColumns(25);
        txtProjectLicence.setLineWrap(true);
        txtProjectLicence.setRows(8);
        txtProjectLicence.setText("This program comes with absolutely no warranty.\nSee the Apache License, version 2.0 or later for details.\n");
        txtProjectLicence.setWrapStyleWord(true);
        txtProjectLicence.setBorder(null);
        txtProjectLicence.setFocusable(false);
        txtProjectLicence.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectLicence, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 310, 360, 50));

        txtProjectAuthors1.setEditable(false);
        txtProjectAuthors1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        txtProjectAuthors1.setColumns(25);
        txtProjectAuthors1.setLineWrap(true);
        txtProjectAuthors1.setRows(8);
        txtProjectAuthors1.setText("     Authors:\n- Gabriel Medina\n- Mário Carvalho");
        txtProjectAuthors1.setWrapStyleWord(true);
        txtProjectAuthors1.setBorder(null);
        txtProjectAuthors1.setFocusable(false);
        txtProjectAuthors1.setPreferredSize(new java.awt.Dimension(300, 200));
        mainPanel.add(txtProjectAuthors1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 210, 120, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextArea txtProjectAuthors;
    private javax.swing.JTextArea txtProjectAuthors1;
    private javax.swing.JTextArea txtProjectCopyright;
    private javax.swing.JTextArea txtProjectDescription1;
    private javax.swing.JTextArea txtProjectDescription2;
    private javax.swing.JLabel txtProjectIcon;
    private javax.swing.JTextArea txtProjectLicence;
    private javax.swing.JLabel txtProjectName;
    private javax.swing.JLabel txtProjectVersion;
    // End of variables declaration//GEN-END:variables

}
