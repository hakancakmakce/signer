/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.util;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.JOptionPane;

/**
 *
 * @author hakan
 */
public class FileOperation {
    private final String userManuelPath = "documents/ImzagerKurumsalKullanimKilavuzu.pdf";
    private final String licenceTermPath = "documents/KLS.pdf";
    private final Component component;
    
    public FileOperation(Component component){
        this.component = component;
    }
    
    public void openUserManuel() {
        openDocument(this.userManuelPath);
    }
    
    public void openLicenceTerm() {
        openDocument(this.licenceTermPath);
    }
    
    private void openDocument(String path) {
        try {
            // Kaynağı al
            URL fileURL = getClass().getClassLoader().getResource(path);
            if (fileURL != null) {
                File file = new File(fileURL.toURI());
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (file.exists()) {
                        desktop.open(file);
                    } else {
                        JOptionPane.showMessageDialog(this.component, "Doküman bulunamadı.", "Hata", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this.component, "Doküman bulunamadı.", "Hata", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.component, "An error occurred while trying to open the file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
