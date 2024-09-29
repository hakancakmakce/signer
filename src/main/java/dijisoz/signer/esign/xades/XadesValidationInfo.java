package dijisoz.signer.esign.xades;

import lombok.Builder;
import lombok.Data;

@Data
@Builder    
public class XadesValidationInfo {
    private String filePath;
    private String workingDirectory;
}
