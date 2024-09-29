package dijisoz.signer.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignedDocument {
    private Long id;
    private String name;
    private String directory;
    private String content;  
    private byte[] contentByte;
    private String sign;
    private DigiSignType type;

    public String toJSON() {
        String jsonString = null;
            // Nesneyi JSON string'e çevir
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                jsonString = objectMapper.writeValueAsString(this);
            } catch (JsonProcessingException e) {
                System.out.println(e);
            }
        return jsonString;
    }
}
