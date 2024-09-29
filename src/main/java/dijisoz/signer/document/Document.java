package dijisoz.signer.document;

import lombok.Data;

@Data
public class Document {
    private Long id;
    private String htmlContent;
    private Boolean isSigned = false;
    private String preSignedUrl = "";
    private String generatedId;
    
}
