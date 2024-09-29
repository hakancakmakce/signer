package dijisoz.signer.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.json.JSONObject;

import com.itextpdf.text.DocumentException;

import dijisoz.signer.document.Document;
import dijisoz.signer.document.DocumentServiceClient;
import dijisoz.signer.esign.SignBase;
import dijisoz.signer.esign.SignInfo;
import dijisoz.signer.esign.SignStructure;
import dijisoz.signer.forms.WebSign;
import tr.gov.tubitak.uekae.esya.api.common.crypto.Algorithms;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureType;

@ClientEndpoint
public class WebSocketClient extends Endpoint {

    private Session userSession;
    private final JFrame mainFrame;

    public WebSocketClient(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void connect(String token, String topic) {
        try {
            URI endpointURI = URI.create(ApplicationConfig.SOCKET_URL);
            Map<String, List<String>> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", Arrays.asList(token));
            requestHeaders.put("Topic", Arrays.asList(topic));

            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            headers.putAll(requestHeaders);
                        }

                        
                    })
                    .build();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            
            container.connectToServer(this, clientEndpointConfig, endpointURI);
        } catch (IOException | DeploymentException e) {
            System.out.println(e);
        }
    }

    @OnOpen
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.userSession = session;
        System.out.println("Bağlantı açıldı: " + session.getId());
        session.addMessageHandler(String.class, new MessageHandler());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Mesaj alındı: " + message);
    }

    @OnClose
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Bağlantı kapatıldı: " + session.getId() + ", Sebep: " + closeReason);
    }

    @OnError
    @Override
    public void onError(Session session, Throwable throwable) {
        System.out.println("Hata: " + throwable.getMessage());
    }

    public void sendMessage(String message) {
        if (userSession != null && userSession.isOpen()) {
            userSession.getAsyncRemote().sendText(message);
        }
    }

    public void closeConnection() {
        if (userSession != null) {
            try {
                userSession.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {
        @Override
        public void onMessage(String message) {
            System.out.println("Received message via MessageHandler: " + message);
            JSONObject jsonObject = new JSONObject(message);
            String documentId = Long.toString(jsonObject.getLong("documentId"));
            Document document;
            try {
                document = DocumentServiceClient.getDocument(documentId);
                
                if(document == null){
                    System.out.println(documentId + " numaralı dokümanın bilgileri alınamadı");
                } else {
                    String strPath = SignBase.getRootDir() + "\\myDocument\\" + document.getGeneratedId() + ".pdf";
                    Path filePath = Paths.get(strPath);
                    File file = PdfOperation.convertHtmlToPdfy(document.getHtmlContent(), filePath);
                    SignInfo signInfo = SignInfo.builder().file(file).format(SignatureFormat.PAdES).isQualified(true)
                    .structure(SignStructure.INTEGRATED).algorithm(Algorithms.SIGNATURE_RSA_SHA256).type(SignatureType.ES_XL).build();
                    
                    java.awt.EventQueue.invokeLater(() -> {
                        WebSign webSign = new WebSign(signInfo, mainFrame, document);
                        webSign.setVisible(true);
                        webSign.requestFocus();
        });

                }
            } catch (DocumentException | IOException e) {
                System.out.println(e);
            }  
        }
    }
}

