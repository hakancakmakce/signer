package dijisoz.signer.esign;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignResult<T> {
    boolean isSigned;
    String documentName;
    String documentPath;
    byte[] contentByteArray;
    T validationResult;
}
