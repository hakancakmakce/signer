/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.cades;

import java.io.IOException;
import java.util.HashMap;

import dijisoz.signer.esign.BesStrategy;
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
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

/**
 *
 * @author hakan
 */
public class CadesBesStrategy extends CadesBase implements BesStrategy {
    private final SignInfo signInfo;

    public CadesBesStrategy(SignInfo signInfo) {
        this.signInfo = signInfo;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public SignResult<Object> signBes() {
        SignResult<Object> signResult = null;
        
        try {
            BaseSignedData baseSignedData = new BaseSignedData();
            ISignable content = new SignableFile(signInfo.getFile());
            
            //create parameters necessary for signature creation
            HashMap<String, Object> params = new HashMap<>();
            //necessary for certificate validation.By default,certificate validation is done.But if the user
            //does not want certificate validation,he can add P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
            params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            boolean contentIncluded = signInfo.getStructure().equals(SignStructure.INTEGRATED);
            baseSignedData.addContent(content, contentIncluded);
            //Get qualified or non-qualified certificate.
            ECertificate cert = signInfo.getECertificate();
            BaseSigner signer = Manager.getInstance().getSigner(signInfo.getPassword(), cert, signInfo.getAlgorithm(), signInfo.getAlgorithmParams());
            //add signer
            //Since the specified attributes are mandatory for bes,null is given as parameter for optional attributes
            baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

            Manager.getInstance().logout();

            byte[] signature = baseSignedData.getEncoded();

            //write the contentinfo to file
            String newFileName = signInfo.getFile().getAbsolutePath() +".p7s";
            AsnIO.dosyayaz(signature, newFileName);
            
            CadesValidationInfo validationInfo = CadesValidationInfo.builder().signature(signature).build();
            SignedDataValidationResult validationResult = baseValidate(validationInfo);
            validationResult.printDetails();
            System.out.println("CADES BES imza atıldı.");

            signResult = SignResult.builder().documentPath(signInfo.getFile().getAbsolutePath() +".p7s")
            .validationResult(validationResult).build();

        } catch (IOException | CMSSignatureException | ESYAException ex) {

        }

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
