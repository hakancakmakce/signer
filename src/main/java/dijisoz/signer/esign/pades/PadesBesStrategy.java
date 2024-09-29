/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.pades;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import dijisoz.signer.esign.BesStrategy;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignResult;
import dijisoz.signer.esign.smartcard.Manager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.Signature;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureException;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;

/**
 *
 * @author hakan
 */
public class PadesBesStrategy extends PadesBase implements BesStrategy {
    private final SignInfo signInfo;

    public PadesBesStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }

    @Override
    public SignResult<Object> signBes() {
        SignResult<Object> signResult = null;

        try {
            String workingDirectory = signInfo.getFile().getParent();
            SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                    new FileInputStream(signInfo.getFile()), createContext(workingDirectory));

            ECertificate eCertificate = signInfo.getECertificate();
            BaseSigner signer = Manager.getInstance().getSigner(signInfo.getPassword(), eCertificate,signInfo.getAlgorithm(), signInfo.getAlgorithmParams());
            //Manager.getInstance().logout();
            // add signature
            
            Signature signature = signatureContainer.createSignature(eCertificate);
            signature.setSigningTime(Calendar.getInstance());
            signature.sign(signer);
            signatureContainer.write(new FileOutputStream(signInfo.getFile().getAbsolutePath() + "signed-bes.pdf"));

            /*
             * // read and validate
             * SignatureContainer readContainer =
             * SignatureFactory.readContainer(SignatureFormat.PAdES,
             * new FileInputStream(getTestDataFolder() + "signed-bes.pdf"),
             * createContext());
             */
            
            ContainerValidationResult validationResult = signatureContainer.verifyAll();
            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() + "signed-bes.pdf")
            .validationResult(validationResult).build();
            System.out.println(validationResult);

            

        } catch (FileNotFoundException | SignatureException | LoginException | SmartCardException ex) {
            System.out.println(ex);
        }

        System.out.println("PADES BES imza atıldı.");
        return signResult;
        
    }

    @Override
    public SignResult<Object> sign() {
        return signBes();
    }

    @Override
    public void validate() {
        baseValidate(null);
    }
}
