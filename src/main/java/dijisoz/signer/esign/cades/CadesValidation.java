package dijisoz.signer.esign.cades;

import java.io.IOException;
import java.util.HashMap;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CadesValidation extends CadesBase {
    private CadesValidationInfo validationInfo;
    private SignedDataValidationResult validationResult;
    private CadesValidationResult result;

    public CadesValidationResult validate(){
        try {
        HashMap<String, Object> params = new HashMap<>();

        if(validationInfo.getTurkishESigProfile() != null)
            params.put(EParameters.P_VALIDATION_PROFILE, validationInfo.getTurkishESigProfile());

        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        if (validationInfo.getExternalContent() != null)
            params.put(EParameters.P_EXTERNAL_CONTENT, validationInfo.getExternalContent());

        //Use only reference and their corresponding value to validate signature
        params.put(EParameters.P_FORCE_STRICT_REFERENCE_USE, true);

        //Ignore grace period which means allow usage of CRL published before signature time
        //params.put(EParameters.P_IGNORE_GRACE, true);

        //Use multiple policies if you want to use different policies to validate different types of certificate
        //CertValidationPolicies certificateValidationPolicies = new CertValidationPolicies();
        //certificateValidationPolicies.register(CertificateType.DEFAULT.toString(), policy);
        //ValidationPolicy maliMuhurPolicy=PolicyReader.readValidationPolicy(new FileInputStream("./config/certval-policy-malimuhur.xml"));
        //certificateValidationPolicies.register(CertificateType.MaliMuhurCertificate.toString(), maliMuhurPolicy);
        //params.put(EParameters.P_CERT_VALIDATION_POLICIES, certificateValidationPolicies);

        SignedDataValidation sdv = new SignedDataValidation();
        validationResult = sdv.verify(validationInfo.getSignature(), params);
        result = CadesValidationResult.builder().result(validationResult).build();
        } catch(IOException | CMSSignatureException | ESYAException ex) {
            
        }
        
        System.out.println("CADES validation");
        
        return result;
    }
    
}
