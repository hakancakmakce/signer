/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.cades;

import java.io.IOException;
import java.util.HashMap;

import dijisoz.signer.esign.EstStrategy;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignResult;
import dijisoz.signer.esign.SignStructure;
import dijisoz.signer.esign.smartcard.Manager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableFile;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

/**
 *
 * @author hakan
 */
public class CadesEstStrategy extends CadesBase implements EstStrategy {
    private final SignInfo signInfo;

    public CadesEstStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }

    @SuppressWarnings("deprecation")
    @Override
    public SignResult<Object> signEst() {
        SignResult<Object> signResult = null;
        
        try {
            BaseSignedData baseSignedData = new BaseSignedData();
            ISignable content = new SignableFile(signInfo.getFile());
            boolean contentIncluded = signInfo.getStructure().equals(SignStructure.INTEGRATED);
            baseSignedData.addContent(content, contentIncluded);

            HashMap<String, Object> params = new HashMap<>();
            //if the user does not want certificate validation,he can add
            //P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
            //if the user want to do timestamp validation while generating signature,he can add
            //P_VALIDATE_TIMESTAMP_WHILE_SIGNING parameter with its value set to true
            params.put(EParameters.P_VALIDATE_TIMESTAMP_WHILE_SIGNING, false);
            params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
            params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            //necessary for getting signature time stamp.
            //for getting test TimeStamp or qualified TimeStamp account, mail to bilgi@kamusm.gov.tr
            TSSettings tsSettings = getTSSettings();
            params.put(EParameters.P_TSS_INFO, tsSettings);

            //Get qualified or non-qualified certificate.
            ECertificate cert = signInfo.getECertificate();
            BaseSigner signer = Manager.getInstance().getSigner(signInfo.getPassword(), cert, signInfo.getAlgorithm(), signInfo.getAlgorithmParams());

            //add signer
            baseSignedData.addSigner(ESignatureType.TYPE_EST, cert, signer, null, params);

            Manager.getInstance().logout();

            byte[] signedDocument = baseSignedData.getEncoded();

            AsnIO.dosyayaz(signedDocument, signInfo.getFile().getAbsolutePath() + "EST-1.p7s");

            CadesValidationInfo validationInfo = CadesValidationInfo.builder().signature(signedDocument).build();
            SignedDataValidationResult validationResult = baseValidate(validationInfo);
            validationResult.printDetails();
            System.out.println("CADES EST imza atıldı.");

            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() +".p7s")
            .validationResult(validationResult).build();
        } catch (IOException | CMSSignatureException | ESYAException ex) {
            System.out.println(ex.getMessage());
        }

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
