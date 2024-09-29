/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.xades;

import java.io.FileOutputStream;
import java.io.IOException;

import dijisoz.signer.esign.EslStrategy;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignResult;
import dijisoz.signer.esign.smartcard.Manager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.config.ConfigurationException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.Context;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignature;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignatureException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.config.TimestampConfig;

/**
 *
 * @author hakan
 */
@SuppressWarnings("deprecation")
public class XadesEslStrategy extends XadesBase implements EslStrategy {
    private final SignInfo signInfo;

    public XadesEslStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }

    @Override
    public SignResult<Object> signEsl() {
        SignResult<Object> signResult = null;

        try {
            // create context with working directory
            String workingDirectory = signInfo.getFile().getParent();
            Context context = createContext(workingDirectory);
            String tsServerUserId = context.getConfig().getTimestampConfig().getUserId();
            if (tsServerUserId == null || tsServerUserId.equals("")) {
                TSSettings tsSettings = getTSSettings();
                TimestampConfig tsConfig = new TimestampConfig(tsSettings.getHostUrl(),
                        String.valueOf(tsSettings.getCustomerID()), tsSettings.getCustomerPassword(), DigestAlg.SHA256);
                context.getConfig().setTimestampConfig(tsConfig);
            }

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

            // now sign it by using smart card
            signature.sign(Manager.getInstance().getSigner(signInfo.getPassword(), cert));

            // upgrade to XL
            signature.upgrade(SignatureType.ES_XL);

            String signedFilePath = signInfo.getFile().getAbsolutePath() + ".EXL.xml";
            try (FileOutputStream fileOutputStream = new FileOutputStream(signedFilePath)) {
                signature.write(fileOutputStream);
            }

            XadesValidationInfo validationInfo = XadesValidationInfo.builder().workingDirectory(workingDirectory)
                    .filePath(signedFilePath).build();
            baseValidate(validationInfo);

            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() +".sign.xml")
            //.validationResult(validationResult)
            .build();
        } catch (IOException | LoginException | SmartCardException | XMLSignatureException
                | ConfigurationException ex) {
            System.out.println(ex);
        }

        System.out.println("XADES ESL İmza atıldı");

        return signResult;
    }

    @Override
    public SignResult<Object> sign() {
        return signEsl();
    }

    @Override
    public void validate() {
        baseValidate(null);
    }
}
