/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.pades;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

import dijisoz.signer.esign.EstStrategy;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignResult;
import dijisoz.signer.esign.smartcard.Manager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContext;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.Signature;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureException;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.config.TimestampConfig;

/**
 *
 * @author hakan
 */
@SuppressWarnings("deprecation")
public class PadesEstStrategy extends PadesBase implements EstStrategy {
    private final SignInfo signInfo;

    public PadesEstStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }

    @Override
    public SignResult<Object> signEst() {
        SignResult<Object> signResult = null;

        try {
            String workingDirectory = signInfo.getFile().getParent();
            PAdESContext context = createContext(workingDirectory);
            String tsServerUserId = context.getConfig().getTimestampConfig().getUserId();
            if (tsServerUserId == null || tsServerUserId.equals("")) {
                TSSettings tsSettings = getTSSettings();
                TimestampConfig tsConfig = new TimestampConfig(tsSettings.getHostUrl(),
                        String.valueOf(tsSettings.getCustomerID()), tsSettings.getCustomerPassword(), DigestAlg.SHA256);
                context.getConfig().setTimestampConfig(tsConfig);
            }
            context.setSignWithTimestamp(true);
            SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                    new FileInputStream(signInfo.getFile()), context);

            ECertificate eCertificate = signInfo.getECertificate();
            BaseSigner signer = Manager.getInstance().getSigner(signInfo.getPassword(), eCertificate);

            // add signature
            Signature signature = signatureContainer.createSignature(eCertificate);
            signature.setSigningTime(Calendar.getInstance());
            signature.sign(signer);

            // write to file
            signatureContainer.write(new FileOutputStream(signInfo.getFile().getAbsolutePath() + "signed-est.pdf"));

            ContainerValidationResult validationResult = signatureContainer.verifyAll();
            System.out.println(validationResult);

            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() + "signed-bes.pdf")
            .validationResult(validationResult).build();

        } catch (FileNotFoundException | SignatureException | LoginException | SmartCardException ex) {
            System.err.println(ex);
        }
        System.out.println("PADES EST imza atıldı.");

        return signResult;
    }

    @Override
    public SignResult<Object> sign() {
        return signEst();
    }

    @Override
    public void validate() {
        baseValidate(null);
    }
}
