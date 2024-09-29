package dijisoz.signer.esign.cades;

import lombok.Builder;
import lombok.Data;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;

@Data
@Builder
public class CadesValidationResult {
    private SignedDataValidationResult result;
}
