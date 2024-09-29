package dijisoz.signer.esign.pades;

import lombok.Builder;
import lombok.Data;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;

@Data
@Builder
public class PadesValidationResult {
    private ContainerValidationResult result;
}
