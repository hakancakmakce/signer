/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dijisoz.signer.authentication;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import dijisoz.signer.util.ApplicationConfig;
import dijisoz.signer.util.Observed;
import dijisoz.signer.util.RestApiClient;
import dijisoz.signer.util.RestApiClientRequest;
import dijisoz.signer.util.RestApiClientResponse;
import dijisoz.signer.util.WebSocketClient;


/**
 *
 * @author hakan
 */
public class UserSession extends Observed {
    private static UserSession instance;
    private boolean isUserAuthenticated;
    private String token;
    private Timer timer;
    
    private UserSession() {
        this.isUserAuthenticated = false;
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        
        return instance;
    }

    
    
    public void authenticate(String userName, String password, JFrame mainFrame) {
        stopTimer();
        LoginRequest loginRequest = new LoginRequest(userName, password);
        RestApiClientRequest request = RestApiClientRequest.builder()
                .url(ApplicationConfig.LOGIN_URL)
                .type("POST")
                .payload(loginRequest.toJson()).build();
        RestApiClientResponse response = RestApiClient.sendRequest(request);
        String uri = ApplicationConfig.LOGIN_URL;
        System.out.println(uri);
        if(response.getSuccess()){
            LoginResponse loginResponse = new LoginResponse(response.getResult());
            this.isUserAuthenticated = loginResponse.isLogged();
            this.token = loginResponse.getToken();
            notifyObservers(this.isUserAuthenticated);
            if(this.isUserAuthenticated)
                startTimer();
            
            WebSocketClient wsClient = new WebSocketClient(mainFrame);
            wsClient.connect(token, "TestTopic");
        }
    }
    
    public boolean isAuthenticated() {
        return isUserAuthenticated;
    }

    public String getToken() {
        return token;
    }
    
    private void startTimer() {
        if (timer != null) {
            timer.cancel(); // Mevcut timer'ı durdur
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                resetData();
            }
        }, 3 * 60 * 1000); // 30 dakika
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    private void resetData() {
        this.isUserAuthenticated = false;
        this.token = null;
        notifyObservers(false);
    }
}
