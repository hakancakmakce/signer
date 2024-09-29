/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.xades;

import java.io.FileOutputStream;
import java.io.IOException;

import dijisoz.signer.esign.BesStrategy;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignResult;
import dijisoz.signer.esign.smartcard.Manager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.Context;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignature;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignatureException;

/**
 *
 * @author hakan
 */
public class XadesBesStrategy extends XadesBase implements BesStrategy {
    private final SignInfo signInfo;

    public XadesBesStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }

    @Override
    public SignResult<Object> signBes() {
        SignResult<Object> signResult = null;

        try {
            // create context with working directory
            String workingDirectory = signInfo.getFile().getParent();
            Context context = createContext(workingDirectory);

            // create signature according to context,
            // with default type (XADES_BES)
            XMLSignature signature = new XMLSignature(context);

            // add document as reference, but do not embed it
            // into the signature (embed=false)
            signature.addDocument(signInfo.getFile().getAbsolutePath(), "text/plain", false);

            // false-true gets non-qualified certificates while true-false gets qualified
            // ones
            ECertificate cert = signInfo.getECertificate();

            // add certificate to show who signed the document
            signature.addKeyInfo(cert);

            BaseSigner signer = Manager.getInstance().getSigner(signInfo.getPassword(), cert);
            // now sign it by using smart card
            signature.sign(signer);

            try (FileOutputStream fileOutputStream = new FileOutputStream(
                    signInfo.getFile().getAbsolutePath() + ".sign.xml")) {
                signature.write(fileOutputStream);
            }

            XadesValidationInfo validationInfo = XadesValidationInfo.builder()
                    .filePath(signInfo.getFile().getAbsolutePath() + ".sign.xml").workingDirectory(workingDirectory)
                    .build();
            baseValidate(validationInfo);

            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() +".sign.xml")
            //.validationResult(validationResult)
            .build();
        } catch (IOException | ESYAException | XMLSignatureException ex) {
            System.out.println(ex);
        }
        System.out.println("XADES BES imza atıldı.");

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
