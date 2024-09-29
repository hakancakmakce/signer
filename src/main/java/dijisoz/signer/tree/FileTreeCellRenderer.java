/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.tree;

import java.awt.Component;
import java.io.File;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author hakan
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer{
    private final FileSystemView fileSystemView;
        
        // Varsayılan ikonlar
        private final Icon defaultFolderIcon;
        private final Icon defaultFileIcon;
    
        public FileTreeCellRenderer() {
            // Varsayılan ikonları yükle
            defaultFolderIcon = loadIcon("/icons/default-folder.png");
            defaultFileIcon = loadIcon("/icons/default-file.png");
            fileSystemView = FileSystemView.getFileSystemView();
        }
    
        private Icon loadIcon(String path) {
            URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Icon not found: " + path);
                return null;
            }
        }
    
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();
    
                if (userObject instanceof FileNode ) {
                    FileNode fileNode = (FileNode) userObject;
                    File file = fileNode.getFile();
    
                    if (file != null) {
                        
                        Icon icon = null;
                        try {
                            icon = fileSystemView.getSystemIcon(file);
                        }
                        catch(Exception ex){
                            System.out.println(ex);
                        }
                        // İkon varsa ayarla, yoksa varsayılan ikonları kullan
                        if (icon == null) {
                            if (file.isDirectory()) {
                                setIcon(defaultFolderIcon);
                            } else {
                                setIcon(defaultFileIcon);
                            }
                        } else {
                            setIcon(icon);
                        }
    
                        // Dosya veya klasör adını ayarla
                        setText(fileSystemView.getSystemDisplayName(file));
                    } else {
                        // Dosya nesnesi null ise, sadece metni göster
                        setText(userObject.toString());
                        setIcon(defaultFileIcon); // Default ikon kullan
                    }
                }
            } else {
            }
            
            return this;
        }
}
