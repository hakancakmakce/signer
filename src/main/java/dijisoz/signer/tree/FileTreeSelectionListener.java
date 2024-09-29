/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.tree;

import java.io.File;
import java.text.SimpleDateFormat;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author hakan
 */
public class FileTreeSelectionListener implements TreeSelectionListener {
    private JTree tree;
    
    public FileTreeSelectionListener(JTree tree) {
        //this.tree = tree;
    }
    
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            this.tree = (JTree)e.getSource();
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) return;

            FileNode fileNode = (FileNode) selectedNode.getUserObject();
            File selectedFile = fileNode.getFile();
            
            if (selectedFile.isFile()) {
                String fileInfo = getFileInfo(selectedFile);
                System.err.println(fileInfo);
            } 
        }

        private String getFileInfo(File file) {
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
