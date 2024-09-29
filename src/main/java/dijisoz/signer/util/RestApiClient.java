/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author hakan
 */
public class RestApiClient {
    public static RestApiClientResponse sendRequest(RestApiClientRequest request) {
        RestApiClientResponse response = new RestApiClientResponse("", false, "");
        try {
            URL url = new URL(request.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getType());
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            if (request.getToken() != null && !request.getToken().isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + request.getToken());
            }
            connection.setDoOutput(true);

            if(request.getPayload() != null && !request.getPayload().isEmpty()){
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = request.getPayload().getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }}

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder responseBuilder = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
                
                response.setResult(responseBuilder.toString());
                response.setSuccess(Boolean.TRUE);
            }
        } catch (IOException e) {
            response.setSuccess(Boolean.FALSE);
            response.setErrorMessage(e.getMessage());
        }
        
        return response;
    }
}
