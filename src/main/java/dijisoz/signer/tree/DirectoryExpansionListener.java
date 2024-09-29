/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.tree;

import java.io.File;
import java.util.Arrays;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author hakan
 */
public class DirectoryExpansionListener implements TreeExpansionListener {
    private FileSystemView fileSystemView;
    private final DefaultTreeModel treeModel;
    
    public DirectoryExpansionListener(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }
    
    @Override
        public void treeExpanded(TreeExpansionEvent event) {
            
            fileSystemView = FileSystemView.getFileSystemView();
            TreePath path = event.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (node.getChildCount() == 1 && "Loading...".equals(node.getFirstChild().toString())) {
                node.removeAllChildren();
                FileNode fileNode = (FileNode) node.getUserObject();
                File[] files = fileSystemView.getFiles(fileNode.getFile(), false);
                Arrays.stream(files).forEach(file -> {
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new FileNode(file));
                    if (file.isDirectory()) {
                        childNode.add(new DefaultMutableTreeNode("Loading...")); // Placeholder
                    }
                    node.add(childNode);
                });
                
               
                treeModel.nodeStructureChanged(node);
            }
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            // Boş
        }
}
