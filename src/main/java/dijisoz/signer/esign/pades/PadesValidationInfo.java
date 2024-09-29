package dijisoz.signer.esign.pades;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PadesValidationInfo {
    private String filePath;
    private String workingDirectory;
}
