/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.tree;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author hakan
 */
public class FileNode {
    private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private final File file;

        public FileNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return fileSystemView.getSystemDisplayName(file);
        }
}
