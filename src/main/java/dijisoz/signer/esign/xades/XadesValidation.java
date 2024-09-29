package dijisoz.signer.esign.xades;

import java.io.File;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.Context;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.ValidationResult;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignature;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignatureException;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.document.FileDocument;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.model.xades.UnsignedSignatureProperties;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class XadesValidation extends XadesBase {
    private XadesValidationInfo validationInfo;
    private ValidationResult validationResult;
    private XadesValidationResult result;
    private List<XMLSignature> counterSignatures;

    public XadesValidationResult validate() {
        try {
            Context context = createContext(validationInfo.getWorkingDirectory());

            /*
             * optional - specifying policy from code
             * // generate policy to be used in certificate validation
             * ValidationPolicy policy = PolicyReader.readValidationPolicy(POLICY_FILE);
             * 
             * CertValidationPolicies policies = new CertValidationPolicies();
             * // null means default
             * policies.register(null,policy);
             * 
             * context.getConfig().getValidationConfig().setCertValidationPolicies(policies)
             * ;
             */

            // add external resolver to resolve policies
            context.addExternalResolver(getOfflineResolver());

            XMLSignature signature = XMLSignature.parse(new FileDocument(new File(validationInfo.getFilePath())),
                    context);

            // no parameters, use the certificate in key info
            validationResult = signature.verify();
            UnsignedSignatureProperties usp = signature.getQualifyingProperties().getUnsignedSignatureProperties();

            if (usp != null)
                counterSignatures = usp.getAllCounterSignatures();

            result = XadesValidationResult.builder().validationResult(validationResult)
                    .counterSignatures(counterSignatures).build();
        } catch (XMLSignatureException e) {
        }
        System.out.println("XADES Validation");

        return result;
    }

    public void signatureVerify(String fileName, XMLSignature signature) throws XMLSignatureException {

        UnsignedSignatureProperties usp = signature.getQualifyingProperties().getUnsignedSignatureProperties();
        //List<ValidationResult> validationList = new ArrayList<>();

        if (usp != null) {
            try {
                counterSignatures = usp.getAllCounterSignatures();

                for (XMLSignature counterSignature : counterSignatures) {
                    ValidationResult counterResult = signature.verify();
    
                    System.out.println(counterResult.toXml());
                    System.out.println(" verify counter signature" + fileName + " : " + counterSignature.getId() + "-"
                            + counterResult.getType());
                }
            } catch (XMLSignatureException e) {
                
                System.out.println(e);
            }

            
        }
    }
}
