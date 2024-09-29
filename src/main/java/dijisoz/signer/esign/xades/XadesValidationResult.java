package dijisoz.signer.esign.xades;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.ValidationResult;
import tr.gov.tubitak.uekae.esya.api.xmlsignature.XMLSignature;

@Data
@Builder
public class XadesValidationResult {
    private ValidationResult validationResult;
    private List<XMLSignature> counterSignatures;
}
