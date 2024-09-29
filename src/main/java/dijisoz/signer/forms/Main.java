/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dijisoz.signer.forms;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import dijisoz.signer.esign.cades.CadesValidation;
import dijisoz.signer.esign.cades.CadesValidationInfo;
import dijisoz.signer.esign.cades.CadesValidationResult;
import dijisoz.signer.esign.pades.PadesValidation;
import dijisoz.signer.esign.pades.PadesValidationInfo;
import dijisoz.signer.esign.pades.PadesValidationResult;
import dijisoz.signer.esign.xades.XadesValidation;
import dijisoz.signer.esign.xades.XadesValidationInfo;
import dijisoz.signer.esign.xades.XadesValidationResult;
import dijisoz.signer.tree.FileNode;
import dijisoz.signer.tree.FileSystemTree;
import dijisoz.signer.util.FileOperation;
import dijisoz.signer.util.Observer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;



/**
 *
 * @author 
 */
public class Main extends javax.swing.JFrame implements Observer{
    private static final long serialVersionUID = 1L;
    private final FileOperation fileOperation;
    public Main() {
         
        fileSystemTree = (new FileSystemTree()).GetInstance();
        fileSystemTree.addTreeSelectionListener(new FileTreeSelectionListener());
        
        initComponents();
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/dijisoz.png"));
        setIconImage(icon.getImage());
        
        fileInfoEditor.addHyperlinkListener(new HyperlinkClickListener());
        //setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        fileOperation = new FileOperation(this);
        
    }
    
    @Override
    public void update(boolean isUserAuthenticated) {
        sign.setEnabled(isUserAuthenticated);
        loginMenuItem.setEnabled(!isUserAuthenticated);
    }
    
    public class FileTreeSelectionListener implements TreeSelectionListener {   
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileSystemTree.getLastSelectedPathComponent();
            if (selectedNode == null) return;
            FileNode fileNode = (FileNode) selectedNode.getUserObject();
            File selectedFile = fileNode.getFile();
            
            if (selectedFile.isFile()) {
                String fileInfo = getFileInfo(selectedFile);
                fileInfoEditor.setText(fileInfo);
            } 
        }

        private String getFileInfo(File file)  {
            try {
                SignatureFormat format = SignatureFactory.detectSignatureFormat(new BufferedInputStream(new FileInputStream(file)));

                if(format == null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    return "<html>File Info:<br>" +
                            "Name: <a href=\"file:///" + file.getAbsolutePath() + "\">" + file.getName() + "</a><br>" +
                            "Path: " + file.getAbsolutePath() + "<br>" +
                            "Type: " + getFileExtension(file) + "<br>" +
                            "Size: " + file.length() + " bytes<br>" +
                            "Last Modified: " + sdf.format(file.lastModified()) + "</html>";
                }
                
                if(format.equals(SignatureFormat.CAdES)){
                    byte[] signature = Files.readAllBytes(file.toPath());
                    CadesValidationInfo validationInfo = CadesValidationInfo.builder().signature(signature).build();
                    CadesValidationResult result = CadesValidation.builder().validationInfo(validationInfo).build().validate();
                    return result.toString();
                }
                
                if(format.equals(SignatureFormat.XAdES)){
                    XadesValidationInfo validationInfo = XadesValidationInfo.builder().filePath(file.getAbsolutePath()).workingDirectory(file.getParent()).build();
                    XadesValidationResult result = XadesValidation.builder().validationInfo(validationInfo).build().validate();
                    return result.getValidationResult().toString();
                }
                
                if(format.equals(SignatureFormat.PAdES)){
                    PadesValidationInfo validationInfo = PadesValidationInfo.builder().filePath(file.getAbsolutePath()).workingDirectory(file.getParent()).build();
                    PadesValidationResult result = PadesValidation.builder().validationInfo(validationInfo).build().validate();
                    String son = result.getResult().getSignatureValidationResults().toString();
                    son += "--" + result.getResult().toString();
                    
                    return son;
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return "<html>File Info:<br>" +
                    "Name: <a href=\"file:///" + file.getAbsolutePath() + "\">" + file.getName() + "</a><br>" +
                    "Path: " + file.getAbsolutePath() + "<br>" +
                    "Type: " + getFileExtension(file) + "<br>" +
                    "Size: " + file.length() + " bytes<br>" +
                    "Last Modified: " + sdf.format(file.lastModified()) + "</html>";
        }

        private String getFileExtension(File file) {
            String fileName = file.getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "Unknown" : fileName.substring(dotIndex + 1);
        }
    }
    
    private class HyperlinkClickListener implements HyperlinkListener {
        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().open(new File(e.getURL().toURI()));
                } catch (IOException | java.net.URISyntaxException ex) {
                    System.err.println(ex);
                }
            }
        }
    }


    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        try {
            fileSystemTree =(javax.swing.JTree)java.beans.Beans.instantiate(getClass().getClassLoader(), "dijisoz/signer/forms.Main_fileSystemTree");
        } catch (ClassNotFoundException | java.io.IOException e) {
            System.out.println(e);
        }
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        sign = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        fileInfoEditor = new javax.swing.JEditorPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        loginMenuItem = new javax.swing.JMenuItem();
        sectificatesMenuItem = new javax.swing.JMenuItem();
        cardReaderMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        userManuelMenuItem = new javax.swing.JMenuItem();
        licenceMenuItem = new javax.swing.JMenuItem();
        aboutMaenuItem = new javax.swing.JMenuItem();

        jMenu1.setText("File");
        jMenuBar2.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar2.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dijisoz İmzacı");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        jScrollPane1.setViewportView(fileSystemTree);

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jTabbedPane1.setFocusCycleRoot(true);
        jTabbedPane1.setName("gdfgdf"); // NOI18N

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 968, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("tab2", jLayeredPane1);

        sign.setText("İmzala");
        sign.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sign.addActionListener(this::signActionPerformed);

        fileInfoEditor.setEditable(false);
        fileInfoEditor.setContentType("text/html"); // NOI18N
        fileInfoEditor.setName("fileInfoEditor"); // NOI18N
        jScrollPane2.setViewportView(fileInfoEditor);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(sign, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sign)
                .addGap(97, 97, 97))
        );

        jTabbedPane1.addTab("Dosya Bilgileri", null, jPanel1, "");

        fileMenu.setText("Dosya");

        loginMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        loginMenuItem.setText("Kullanıcı Giriş");
        loginMenuItem.setToolTipText("");
        loginMenuItem.addActionListener(this::loginMenuItemActionPerformed);
        fileMenu.add(loginMenuItem);

        sectificatesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        sectificatesMenuItem.setText("Sertificalar");
        sectificatesMenuItem.addActionListener(this::sectificatesMenuItemActionPerformed);
        fileMenu.add(sectificatesMenuItem);

        cardReaderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        cardReaderMenuItem.setText("Kart Okuyucular");
        cardReaderMenuItem.addActionListener(this::cardReaderMenuItemActionPerformed);
        fileMenu.add(cardReaderMenuItem);

        jMenuBar1.add(fileMenu);

        helpMenu.setText("Yardım");

        userManuelMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        userManuelMenuItem.setText("Kullanım Klavuzu");
        userManuelMenuItem.addActionListener(this::userManuelMenuItemActionPerformed);
        helpMenu.add(userManuelMenuItem);

        licenceMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        licenceMenuItem.setText("Lisans Sözleşmesi");
        licenceMenuItem.addActionListener(this::licenceMenuItemActionPerformed);
        helpMenu.add(licenceMenuItem);

        aboutMaenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        aboutMaenuItem.setText("Hakkında");
        aboutMaenuItem.addActionListener(this::aboutMaenuItemActionPerformed);
        helpMenu.add(aboutMaenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 968, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1)
                        .addGap(475, 475, 475)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginMenuItemActionPerformed
        new Login(this).setVisible(true);
    }//GEN-LAST:event_loginMenuItemActionPerformed

    private void sectificatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sectificatesMenuItemActionPerformed
        new Certificate(this).setVisible(true);
    }//GEN-LAST:event_sectificatesMenuItemActionPerformed

    private void cardReaderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cardReaderMenuItemActionPerformed
        new CardReader(this).setVisible(true);
    }//GEN-LAST:event_cardReaderMenuItemActionPerformed

    private void aboutMaenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMaenuItemActionPerformed
        new About(this).setVisible(true);
    }//GEN-LAST:event_aboutMaenuItemActionPerformed

    private void userManuelMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userManuelMenuItemActionPerformed
        fileOperation.openUserManuel();
    }//GEN-LAST:event_userManuelMenuItemActionPerformed

    private void licenceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_licenceMenuItemActionPerformed
        fileOperation.openLicenceTerm();
    }//GEN-LAST:event_licenceMenuItemActionPerformed

    private void signActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signActionPerformed
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileSystemTree.getLastSelectedPathComponent();
        if (selectedNode == null) return;
        FileNode fileNode = (FileNode) selectedNode.getUserObject();
        File selectedFile = fileNode.getFile();

        if (selectedFile.isFile()) {

        }         //startProgressBar();
        new Sign(selectedFile, this).setVisible(true);
        //UserSession session = UserSession.getInstance(userName.getText(), password.getText());
    }//GEN-LAST:event_signActionPerformed

    
    
    public void startProgressBar() {
        Timer timer = new Timer(100, new ActionListener() {
            int progress = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                progress++;
                //jProgressBar1.setValue(progress);
                if (progress >= 100) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMaenuItem;
    private javax.swing.JMenuItem cardReaderMenuItem;
    private javax.swing.JEditorPane fileInfoEditor;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTree fileSystemTree;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem licenceMenuItem;
    private javax.swing.JMenuItem loginMenuItem;
    private javax.swing.JMenuItem sectificatesMenuItem;
    private javax.swing.JButton sign;
    private javax.swing.JMenuItem userManuelMenuItem;
    // End of variables declaration//GEN-END:variables
}
