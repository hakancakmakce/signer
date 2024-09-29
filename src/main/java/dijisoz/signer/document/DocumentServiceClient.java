package dijisoz.signer.document;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dijisoz.signer.authentication.UserSession;
import dijisoz.signer.util.ApplicationConfig;
import dijisoz.signer.util.RestApiClient;
import dijisoz.signer.util.RestApiClientRequest;
import dijisoz.signer.util.RestApiClientResponse;

public class DocumentServiceClient {
    private static final String TOKEN = UserSession.getInstance().getToken();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Document getDocument(String documentId) throws IOException {
        Document document;
        String requestUrl = ApplicationConfig.GET_DOCUMENT_URL + documentId;
        RestApiClientRequest apiRequest = RestApiClientRequest.builder().token(TOKEN.substring(7)).type("GET").url(requestUrl).build();
        RestApiClientResponse apiResponse = RestApiClient.sendRequest(apiRequest);

        document = objectMapper.readValue(apiResponse.getResult(), Document.class);
        return document;
    }

    public static Boolean uploadDocument(SignedDocument document){
        boolean uploaded = true;

        try {
            RestApiClientRequest request = RestApiClientRequest.builder().token(TOKEN.substring(7)).payload(document.toJSON()).type("POST")
            .url(ApplicationConfig.UPLOAD_SIGNED_DOCUMENT_URL).build();
            RestApiClientResponse response = RestApiClient.sendRequest(request);
            System.out.println(response);
        } catch (Exception e) {
            uploaded = false;
        }

        return uploaded;
    }

    public static String getToken() {
        return TOKEN;
    }
}
