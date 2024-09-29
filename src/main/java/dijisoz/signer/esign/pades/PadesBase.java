/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.pades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import dijisoz.signer.esign.SignBase;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContext;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureException;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.config.Config;

/**
 *
 * @author hakan
 */
public class PadesBase extends SignBase {

    protected PadesBase() {

    }

    /**
     * Creates context for signature creation and validation
     *
     * @return created context
     */
    protected PAdESContext createContext(String workingDirectory) {
        PAdESContext c = new PAdESContext(new File(workingDirectory).toURI());
        c.setConfig(new Config(getRootDir() + "/configs/esya-signature-config.xml"));
        return c;
    }

    protected void baseValidate(PadesValidationInfo validationInfo) {
        try {
            SignatureContainer readContainer = SignatureFactory.readContainer(
                    new FileInputStream(validationInfo.getFilePath()),
                    createContext(validationInfo.getWorkingDirectory()));
            ContainerValidationResult validationResult = readContainer.verifyAll();
            System.out.println(validationResult);
            System.out.println("Pades validasyonu yapıldı");
        } catch (FileNotFoundException | SignatureException ex) {
            System.out.println(ex);
        }

    }
}
