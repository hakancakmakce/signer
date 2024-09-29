/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.tree;

import java.io.File;
import java.util.Arrays;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author hakan
 */
public class FileSystemTree {
    private final DefaultTreeModel treeModel;
    private final FileSystemView fileSystemView;
    private final JTree fileSystemTree;
    private final DefaultMutableTreeNode rootNode;
            
    public FileSystemTree() {
        fileSystemView = FileSystemView.getFileSystemView();
        File[] roots = fileSystemView.getRoots();
        rootNode = new DefaultMutableTreeNode(new FileNode(roots[0]));
        treeModel = new DefaultTreeModel(rootNode);
        expandRootNode();
        fileSystemTree = new JTree(treeModel);
        fileSystemTree.setCellRenderer(new FileTreeCellRenderer());
        fileSystemTree.addTreeExpansionListener(new DirectoryExpansionListener(treeModel));        
    }
    
    private void expandRootNode() {
        FileNode fileNode = (FileNode) rootNode.getUserObject();
        File[] files = fileSystemView.getFiles(fileNode.getFile(), false);
        Arrays.stream(files).forEach(file -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FileNode(file));
            if (file.isDirectory()) {
                node.add(new DefaultMutableTreeNode("Loading...")); // Placeholder
            }
            
            rootNode.add(node);
        });
        
        treeModel.reload(rootNode);
    } 
    
    public JTree GetInstance() {
        return fileSystemTree;
    }
}
