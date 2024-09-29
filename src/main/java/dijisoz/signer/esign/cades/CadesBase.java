/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.esign.cades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import dijisoz.signer.esign.SignBase;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.ESYARuntimeException;

/**
 *
 * @author hakan
 */
public class CadesBase extends SignBase {
    private static ValidationPolicy validationPolicy;

    public CadesBase() {

    }

    protected SignedDataValidationResult baseValidate(CadesValidationInfo validationInfo) {
        SignedDataValidationResult validationResult = null;

        try {
            Hashtable<String, Object> params = new Hashtable<>();

            if (validationInfo.getTurkishESigProfile() != null)
                params.put(EParameters.P_VALIDATION_PROFILE, validationInfo.getTurkishESigProfile());

            params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            if (validationInfo.getExternalContent() != null)
                params.put(EParameters.P_EXTERNAL_CONTENT, validationInfo.getExternalContent());

            // Use only reference and their corresponding value to validate signature
            params.put(EParameters.P_FORCE_STRICT_REFERENCE_USE, true);

            // Ignore grace period which means allow usage of CRL published before signature
            // time
            // params.put(EParameters.P_IGNORE_GRACE, true);

            // Use multiple policies if you want to use different policies to validate
            // different types of certificate
            // CertValidationPolicies certificateValidationPolicies = new
            // CertValidationPolicies();
            // certificateValidationPolicies.register(CertificateType.DEFAULT.toString(),
            // policy);
            // ValidationPolicy maliMuhurPolicy=PolicyReader.readValidationPolicy(new
            // FileInputStream("./config/certval-policy-malimuhur.xml"));
            // certificateValidationPolicies.register(CertificateType.MaliMuhurCertificate.toString(),
            // maliMuhurPolicy);
            // params.put(EParameters.P_CERT_VALIDATION_POLICIES,
            // certificateValidationPolicies);

            SignedDataValidation sdv = new SignedDataValidation();
            validationResult = sdv.verify(validationInfo.getSignature(), params);
        } catch (IOException | CMSSignatureException | ESYAException ex) {

        }

        System.out.println("CADES validation");
        return validationResult;

    }

    protected synchronized ValidationPolicy getPolicy() throws ESYAException, IOException {

        if (validationPolicy == null) {
            try {
                InputStream in = CadesBase.class.getResourceAsStream("/configs/policy.xml");
                File tempPolicyFile = File.createTempFile("policy", ".xml");
                tempPolicyFile.deleteOnExit(); // Program sonlandığında geçici dosyayı siler

                // InputStream'den geçici dosyaya yazın
                try (FileOutputStream fileOutputStream = new FileOutputStream(tempPolicyFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
                validationPolicy = PolicyReader.readValidationPolicy(new FileInputStream(tempPolicyFile));
            } catch (FileNotFoundException e) {
                throw new ESYARuntimeException("Policy file could not be found", e);
            }
        }

        return validationPolicy;
    }
}
